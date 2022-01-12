package com.fulafula.view.ui.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RectF;
import android.media.Image;
import android.net.Uri;

import androidx.camera.core.AspectRatio;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fulafula.utils.ImageUtil;
import com.fulafula.view.common.BaseViewModel;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import kotlin.Pair;
import kotlin.Triple;

public class MainViewModel extends BaseViewModel {

    private final ImageUtil imageUtil;

    @Inject
    public MainViewModel(ImageUtil imageUtil) {
        this.imageUtil = imageUtil;
    }

    private final MutableLiveData<Pair<Uri, Integer>> _scoreWithUri = new MutableLiveData<>();

    public LiveData<Pair<Uri, Integer>> getScoreWithUri() {
        return _scoreWithUri;
    }

    public void getScoreFromOpenCV(Uri imageUri) {
        Disposable disposable = Observable.just(imageUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(imageUtil::uriToBitmap)
                .map(this::detectBitmap)
                .map(score -> new Pair<>(imageUri, score))
                .subscribe(_scoreWithUri::setValue, Throwable::printStackTrace);

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

    public RectF generateRect(int frameWidth, int frameHeight, int rotation, double factor) {
        int width = (rotation == 0 || rotation == 180) ? frameWidth : frameHeight;
        int height = (rotation == 0 || rotation == 180) ? frameHeight : frameWidth;
        int left, right, top, bottom, diameter;

        diameter = Math.min(height, width);

        double offset = factor * diameter;
        diameter = (int) offset;

        left = width / 2 - diameter / 2;
        top = height / 2 - diameter / 3;
        right = width / 2 + diameter / 2;
        bottom = height / 2 + diameter / 3;

        return new RectF((float) left, (float) top, (float) right, (float) bottom);
    }

    public int aspectRatio(int width, int height) {
        final double RATIO_4_3_VALUE = 4.0 / 3.0;
        final double RATIO_16_9_VALUE = 16.0 / 9.0;

        double previewRatio = (double) (Math.max(width, height)) / Math.min(width, height);
        if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    private final MutableLiveData<RotatedRect> _rotatedRect = new MutableLiveData<>();

    public LiveData<RotatedRect> getDetectRect() {
        return _rotatedRect;
    }

    private final MutableLiveData<Uri> _cropImage = new MutableLiveData<>();

    public LiveData<Uri> getCropImage() {
        return _cropImage;
    }

    private final PublishSubject<Triple<Image, Integer, Integer>> _processor = PublishSubject.create();

    public void initPublish() {
        Disposable disposable = _processor
                .skip(2)
                .throttleFirst(5, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(triple -> {
                    Image image = triple.getFirst();
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);

                    return new Triple<>(BitmapFactory.decodeByteArray(bytes, 0, bytes.length), triple.getSecond(), triple.getThird());
                })
                .map(triple -> {
                    Bitmap originBitmap = triple.getFirst();
                    Mat mRgba = new Mat();
                    Utils.bitmapToMat(originBitmap, mRgba);
                    Imgproc.resize(mRgba, mRgba, new Size(triple.getSecond(), triple.getThird()));
                    RotatedRect rect = _rotatedRect.getValue();

                    originBitmap.recycle();

                    assert rect != null;

                    RectF focusRect = generateRect(triple.getSecond(), triple.getThird(), 0, 0.9);
                    Mat cropMat;
                    try {
                        cropMat = mRgba.submat(rect.boundingRect());
                    } catch (Exception e) {
                        cropMat = mRgba.submat(new Rect(
                                (int) focusRect.left,
                                (int) focusRect.top,
                                (int) (focusRect.right - focusRect.left),
                                (int) (focusRect.bottom - focusRect.top)));
                    }
                    Bitmap cropBitmap = Bitmap.createBitmap(cropMat.width(), cropMat.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(cropMat, cropBitmap);

                    mRgba.release();
                    cropMat.release();

                    return cropBitmap;
                })
                .map(imageUtil::bitmapToUri)
                .subscribe(uri -> {
                    _detectSuccessCount = 0;
                    _cropImage.postValue(uri);
                }, Throwable::printStackTrace);
        mDisposable.add(disposable);
    }

    public void cropPicture(Image image, int width, int height) {
        _processor.onNext(new Triple<>(image, width, height));
    }

    private int _detectSuccessCount = 0;

    private final ArrayList<RotatedRect> bounds = new ArrayList<>();

    private final MutableLiveData<Pair<Float, Integer>> _drawFocusRect = new MutableLiveData<>();

    public LiveData<Pair<Float, Integer>> getDrawFocusRect() {
        return _drawFocusRect;
    }

    public void findRectangle(Image image, int width, int height, RectF focusRect) {
        Mat mGray = imageUtil.yuvToGray(image);
        Mat detectedEdges = new Mat();
        Mat edges = new Mat();
        Mat hierarchy = new Mat();

        Imgproc.resize(mGray, mGray, new Size(width, height));
        Imgproc.medianBlur(mGray, detectedEdges, 1);
        Imgproc.Canny(detectedEdges, edges, 10, 30, 3);
        Imgproc.dilate(edges, edges, new Mat(), new Point(-1d, 1d), 2);

        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat contourImage = edges.clone();

        mGray.release();
        detectedEdges.release();
        edges.release();

        Imgproc.findContours(contourImage, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        hierarchy.release();

//        List<MatOfPoint> hullList = new ArrayList<>();
//        for (MatOfPoint contour : contours) {
//            MatOfInt hull = new MatOfInt();
//            Imgproc.convexHull(contour, hull);
//
//            Point[] contourArray = contour.toArray();
//            Point[] hullPoints = new Point[hull.rows()];
//            List<Integer> hullContourIdxList = hull.toList();
//            for (int i = 0; i < hullContourIdxList.size(); i++) {
//                hullPoints[i] = contourArray[hullContourIdxList.get(i)];
//            }
//            hullList.add(new MatOfPoint(hullPoints));
//            hull.release();
//        }

        bounds.clear();
        for (int i = 0; i < contours.size(); i++) {

            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());

            double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
            double contourArea = Math.abs(Imgproc.contourArea(contours.get(i)));

            if (Math.abs(contourArea) < 200000) continue;

            RotatedRect rotatedRect = Imgproc.minAreaRect(contour2f);
            Mat pointResult = new Mat();
            Imgproc.boxPoints(rotatedRect, pointResult);

            if (isCloseToFocusRect(contour2f, 50, focusRect)) {
                bounds.add(rotatedRect);
                break;
            }
            pointResult.release();
        }

        if (bounds.size() > 0) {
            _detectSuccessCount++;
            _drawFocusRect.postValue(new Pair<>(10f, Color.GREEN));
            if (_detectSuccessCount >= 8 && _detectSuccessCount <= 15) {
                _rotatedRect.postValue(bounds.get(0));
            }
        } else {
            _drawFocusRect.postValue(new Pair<>(2f, Color.WHITE));
            _detectSuccessCount = 0;
        }
    }

    private boolean isCloseToFocusRect(MatOfPoint2f contour, int threshold, RectF focusRect) {
        int leftTopDistance = Math.abs((int) Imgproc.pointPolygonTest(contour, new Point(focusRect.left, focusRect.top), true));
        int rightTopDistance = Math.abs((int) Imgproc.pointPolygonTest(contour, new Point(focusRect.right, focusRect.top), true));
        int leftBottomDistance = Math.abs((int) Imgproc.pointPolygonTest(contour, new Point(focusRect.left, focusRect.bottom), true));
        int rightBottomDistance = Math.abs((int) Imgproc.pointPolygonTest(contour, new Point(focusRect.right, focusRect.bottom), true));
        return leftTopDistance <= threshold && rightTopDistance <= threshold && leftBottomDistance <= threshold && rightBottomDistance <= threshold;
    }
}
