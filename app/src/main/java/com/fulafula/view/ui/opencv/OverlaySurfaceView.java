package com.fulafula.view.ui.opencv;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.media.Image;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.fulafula.view.ui.main.MainViewModel;

public class OverlaySurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private final SurfaceHolder holder;
    private final int rotation;
    private final MainViewModel viewModel;
    private int width, height;
    private final Paint boundPaint = new Paint();
    private final Paint focusRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF focusRect;
    private Bitmap canvasBitmap;

    public OverlaySurfaceView(SurfaceView surfaceView, MainViewModel viewModel) {
        super(surfaceView.getContext());
        this.viewModel = viewModel;
        this.rotation = surfaceView.getDisplay().getRotation();
        surfaceView.setZOrderOnTop(true);

        holder = surfaceView.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        boundPaint.setColor(Color.RED);
        boundPaint.setStyle(Paint.Style.STROKE);
        boundPaint.setStrokeWidth(10f);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {
        this.width = width;
        this.height = height;
        focusRect = viewModel.generateRect(width, height, rotation, 0.9);
        drawFocusRect(2f, Color.WHITE);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
    }

    public void findRectangle(Image image) {
        viewModel.findRectangle(image, width, height, focusRect);
    }

    public void drawFocusRect(float borderWidth, int borderColor) {
        Canvas canvas = holder.lockCanvas();

        if (canvas == null) {
            return;
        }

        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        float radius = 10f;

        focusRectPaint.setStyle(Paint.Style.STROKE);
        focusRectPaint.setColor(0x33000000);
        focusRectPaint.setStrokeWidth(borderWidth + 5f);

        canvas.drawRoundRect(focusRect, radius, radius, focusRectPaint);

        focusRectPaint.setColor(borderColor);
        focusRectPaint.setStrokeWidth(borderWidth);
        canvas.drawRoundRect(focusRect, radius, radius, focusRectPaint);

        int outerFillColor = 0x77000000;
        int cw = canvas.getWidth();
        int ch = canvas.getHeight();

        canvasBitmap = Bitmap.createBitmap(cw, ch, Bitmap.Config.ARGB_8888);
        Canvas auxCanvas = new Canvas(canvasBitmap);

        focusRectPaint.setColor(outerFillColor);
        focusRectPaint.setStyle(Paint.Style.FILL);
        auxCanvas.drawPaint(focusRectPaint);

        focusRectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        auxCanvas.drawRoundRect(focusRect, radius, radius, focusRectPaint);

        focusRectPaint.setXfermode(null);
        focusRectPaint.setColor(borderColor);
        focusRectPaint.setStyle(Paint.Style.STROKE);
        auxCanvas.drawRoundRect(focusRect, radius, radius, focusRectPaint);

        canvas.drawBitmap(canvasBitmap, 0f, 0f, focusRectPaint);

        canvasBitmap.recycle();
        canvasBitmap = null;
        holder.unlockCanvasAndPost(canvas);
    }
}
