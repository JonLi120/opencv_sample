package com.fulafula.view.ui.pdfrender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fulafula.repository.repo.SimpleRepository;
import com.fulafula.utils.AppUtil;
import com.fulafula.view.common.BaseViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;

public class PdfRenderViewModel extends BaseViewModel {

    private final SimpleRepository repository;
    private Context context;
    private final AppUtil util;

    @Inject
    PdfRenderViewModel(SimpleRepository repository, Context context, AppUtil util) {
        this.repository = repository;
        this.context = context;
        this.util = util;
    }

    private final MutableLiveData<List<Bitmap>> _bitmaps = new MutableLiveData<>();

    public LiveData<List<Bitmap>> getBitmaps() {
        return _bitmaps;
    }

    public void downloadPdf(String url) {
        Disposable disposable = repository.getPdfFile(url)
                .doOnSubscribe(disposable1 -> _loadStatus.postValue(true))
                .doFinally(() -> _loadStatus.postValue(false))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> {
                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        throw new RuntimeException("Failed response, please try again.");
                    }
                })
                .map(responseBody -> {
                    File pdfFile = writePdfToFile(responseBody, url);

                    if (pdfFile != null) {
                        return renderPdfPage(pdfFile);
                    } else {
                        throw new RuntimeException("Failed to write to PDF");
                    }
                })
                .subscribe(_bitmaps::setValue, Throwable::printStackTrace);

        mDisposable.add(disposable);
    }

    private File writePdfToFile(ResponseBody body, String url) {
        try {
            String fileName = getFileNameFromUrl(url);
            File file = new File(context.getCacheDir(), fileName);
            BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(body.source());
            sink.close();
            return file;
        } catch (Exception e) {
            return null;
        }
    }

    private String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private List<Bitmap> renderPdfPage(File pdfFile) throws IOException {
        ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
        PdfRenderer renderer = new PdfRenderer(fileDescriptor);

        int pageCount = renderer.getPageCount();
        List<Bitmap> bitmaps = new ArrayList<>();

        int displayHeight = util.getDisplayHeight();
        int displayWidth = util.getDisplayWidth();

        for (int i = 0; i < pageCount; i++) {
            PdfRenderer.Page page = renderer.openPage(i);
            Bitmap bitmap = Bitmap.createBitmap(displayWidth, displayHeight, Bitmap.Config.ARGB_4444);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            bitmaps.add(bitmap);
            page.close();
        }
        renderer.close();

        return bitmaps;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        context = null;
    }
}
