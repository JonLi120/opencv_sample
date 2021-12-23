package com.fulafula.view.ui.main;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fulafula.view.common.BaseViewModel;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends BaseViewModel {

    @Inject
    public MainViewModel() {
    }

    private final MutableLiveData<Integer> _blurScore = new MutableLiveData<>();

    public LiveData<Integer> getBlurScore() {
        return _blurScore;
    }

    public void getScoreFromOpenCV(ContentResolver contentResolver, Uri imageUri) {
        Disposable disposable = Observable.just(imageUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(uri -> {
                    Bitmap bitmap;
                    if (Build.VERSION.SDK_INT < 28) {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri);
                    } else {
                        ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, imageUri);
                        bitmap = ImageDecoder.decodeBitmap(source, (decoder, imageInfo, source1) ->
                                decoder.setMutableRequired(true));
                    }
                    return bitmap;
                })
                .map(this::detectBitmap)
                .subscribe(_blurScore::setValue, Throwable::printStackTrace);

        mDisposable.add(disposable);
    }

//    private Double detectBitmap(Bitmap bitmap, Mat sourceMatImage) {
//        Mat destination = new Mat();
//        Mat matGray = new Mat();
//        Utils.bitmapToMat(bitmap, sourceMatImage);
//        Imgproc.cvtColor(sourceMatImage, matGray, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.Laplacian(matGray, destination, CV_64F);
//        MatOfDouble median = new MatOfDouble();
//        MatOfDouble std = new MatOfDouble();
//        Core.meanStdDev(destination, median, std);
//        return Double.valueOf(new DecimalFormat("0.00").format(Math.pow(std.get(0, 0)[0], 2.0)));
//    }

    private int detectBitmap(Bitmap bitmap) {
        Mat matImage = new Mat();
        Utils.bitmapToMat(bitmap, matImage);
        Mat matImageGrey = new Mat();
        Imgproc.cvtColor(matImage, matImageGrey, Imgproc.COLOR_BGR2GRAY);

        matImage.release();

        Bitmap destImage = Bitmap.createBitmap(bitmap);
        Mat dst2 = new Mat();
        Utils.bitmapToMat(destImage, dst2);
        Mat laplacianImage = new Mat();
        dst2.convertTo(laplacianImage, CvType.CV_8UC1);
        Imgproc.Laplacian(matImageGrey, laplacianImage, CvType.CV_8U);
        Mat laplacianImage8bit = new Mat();
        laplacianImage.convertTo(laplacianImage8bit, CvType.CV_8UC1);

        destImage.recycle();
        matImageGrey.release();
        dst2.release();
        laplacianImage.release();

        Bitmap bmp = Bitmap.createBitmap(laplacianImage8bit.cols(), laplacianImage8bit.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(laplacianImage8bit, bmp);
        int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        int maxLap = -16777216; // 16m
        for (int pixel : pixels) {
            if (pixel > maxLap)
                maxLap = pixel;
        }

        bitmap.recycle();
        bmp.recycle();
        laplacianImage8bit.release();

        return maxLap;
    }
}
