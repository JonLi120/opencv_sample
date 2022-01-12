package com.fulafula.view.ui.opencv;

import static com.fulafula.view.ui.opencv.PicturePreviewDialog.KEY_REQUEST_CANCEL;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraState;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.window.layout.WindowMetricsCalculator;

import com.fulafula.databinding.FragmentRectangleDetectionBinding;
import com.fulafula.di.AppViewModelFactory;
import com.fulafula.utils.ImageUtil;
import com.fulafula.view.common.BaseFragment;
import com.fulafula.view.ui.main.MainViewModel;
import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import timber.log.Timber;

public class RectangleDetectionFragment extends BaseFragment<FragmentRectangleDetectionBinding> {

    public static final String TAG = "RectangleDetectionFragment";
    private static final String EXTRA_IS_DETECT_BLUR = "EXTRA_IS_DETECT_BLUR";

    public static RectangleDetectionFragment newInstance(boolean isBlurDetection) {

        RectangleDetectionFragment fragment = new RectangleDetectionFragment();

        Bundle args = new Bundle();
        args.putBoolean(EXTRA_IS_DETECT_BLUR, isBlurDetection);

        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    AppViewModelFactory factory;
    @Inject
    ImageUtil imageUtil;

    private MainViewModel viewModel;

    private BaseLoaderCallback loaderCallback;

    private ExecutorService cameraExecutor;
    private CameraSelector cameraSelector;
    private ProcessCameraProvider cameraProvider;
    private Preview preview;
    private ImageCapture imageCapture;
    private OverlaySurfaceView overlaySurfaceView;

    private boolean isFrozen = false;

    private boolean isDetectBlur = false;

    private int screenAspectRatio, rotation;

    @Override
    protected String getCurrentClassName() {
        return TAG;
    }

    @Override
    protected FragmentRectangleDetectionBinding bindingInflater(LayoutInflater inflater, ViewGroup viewGroup) {
        return FragmentRectangleDetectionBinding.inflate(inflater, viewGroup, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loaderCallback = new BaseLoaderCallback(requireContext()) {
            @Override
            public void onManagerConnected(int status) {
                if (status == LoaderCallbackInterface.SUCCESS) {
                    setupCamera();
                } else {
                    super.onManagerConnected(status);
                }
            }
        };
    }

    @Override
    protected void initView() {
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

        isDetectBlur = getArguments() != null && getArguments().getBoolean(EXTRA_IS_DETECT_BLUR);

        cameraExecutor = Executors.newSingleThreadExecutor();

        overlaySurfaceView = new OverlaySurfaceView(_binding.surfaceView, viewModel);

        viewModel.initPublish();

        observeViewModel();

        getChildFragmentManager().setFragmentResultListener(KEY_REQUEST_CANCEL, getViewLifecycleOwner(), (requestKey, result) ->
                rebindPreview()
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, requireContext(), loaderCallback);
        } else {
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        cameraExecutor.shutdown();
    }

    private void setupCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
            } catch (InterruptedException | ExecutionException e) {
                Timber.e(e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void bindCameraUseCases() {
        Rect metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(requireActivity()).getBounds();
        screenAspectRatio = viewModel.aspectRatio(metrics.width(), metrics.height());

        rotation = _binding.preview.getDisplay().getRotation();

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview = new Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build();

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .setOutputImageRotationEnabled(true)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, image -> {
            if (image.getImage() == null) {
                return;
            }
            if (!isFrozen) {
                overlaySurfaceView.findRectangle(image.getImage());
            }
            image.close();
        });

        cameraProvider.unbindAll();

        Camera camera = cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageCapture, imageAnalysis);
        preview.setSurfaceProvider(_binding.preview.getSurfaceProvider());

//        observeCameraState(camera.getCameraInfo());
    }

    private void takePicture() {
        if (imageCapture == null) {
            return;
        }
        imageCapture.takePicture(cameraExecutor, new ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);
                viewModel.cropPicture(image.getImage(), _binding.surfaceView.getMeasuredWidth(), _binding.surfaceView.getMeasuredHeight());
                image.close();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
                isFrozen = false;
                exception.printStackTrace();
            }
        });
    }

    private void rebindPreview() {
        preview = new Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build();
        preview.setSurfaceProvider(_binding.preview.getSurfaceProvider());
        cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview);
        isFrozen = false;
    }

    private void observeViewModel() {
        viewModel.getDrawFocusRect().observe(getViewLifecycleOwner(), pair ->
                overlaySurfaceView.drawFocusRect(pair.getFirst(), pair.getSecond())
        );

        viewModel.getDetectRect().observe(getViewLifecycleOwner(), mat -> {
            cameraProvider.unbind(preview);
            takePicture();
        });

        viewModel.getCropImage().observe(getViewLifecycleOwner(), uri -> {
            isFrozen = true;
            if (!isDetectBlur) {
                showPreviewDialog(uri, Integer.MIN_VALUE);
            } else {
                viewModel.getScoreFromOpenCV(uri);
            }
        });

        viewModel.getScoreWithUri().observe(getViewLifecycleOwner(), pair ->
                showPreviewDialog(pair.getFirst(), pair.getSecond())
        );
    }

    private void showPreviewDialog(Uri uri, int score) {
        PicturePreviewDialog dialog = PicturePreviewDialog.newInstance(
                uri,
                score,
                isDetectBlur);
        dialog.show(getChildFragmentManager(), PicturePreviewDialog.TAG);
    }

//    private void observeCameraState(CameraInfo cameraInfo) {
//        cameraInfo.getCameraState().observe(getViewLifecycleOwner(), cameraState -> {
//            switch (cameraState.getType()) {
//                case PENDING_OPEN:
//                    Timber.d("%s, CameraState: Pending Open", TAG);
//                    break;
//                case OPENING:
//                    Timber.d("%s, CameraState: Opening", TAG);
//                    break;
//                case OPEN:
//                    Timber.d("%s, CameraState: Open", TAG);
//                    break;
//                case CLOSING:
//                    Timber.d("%s, CameraState: Closing", TAG);
//                    break;
//                case CLOSED:
//                    Timber.d("%s, CameraState: Closed", TAG);
//                    break;
//            }
//
//            if (cameraState.getError() != null) {
//                switch (cameraState.getError().getCode()) {
//                    case CameraState.ERROR_STREAM_CONFIG:
//                        Timber.w("%s, Stream config error", TAG);
//                        break;
//                    case CameraState.ERROR_CAMERA_IN_USE:
//                        Timber.w("%s, Camera in use", TAG);
//                        break;
//                    case CameraState.ERROR_MAX_CAMERAS_IN_USE:
//                        Timber.w("%s, Max cameras in use", TAG);
//                        break;
//                    case CameraState.ERROR_OTHER_RECOVERABLE_ERROR:
//                        Timber.w("%s, Other recoverable error", TAG);
//                        break;
//                    case CameraState.ERROR_CAMERA_DISABLED:
//                        Timber.w("%s, Camera disabled", TAG);
//                        break;
//                    case CameraState.ERROR_CAMERA_FATAL_ERROR:
//                        Timber.w("%s, Fatal error", TAG);
//                        break;
//                    case CameraState.ERROR_DO_NOT_DISTURB_MODE_ENABLED:
//                        Timber.w("%s, Do not disturb mode enabled", TAG);
//                        break;
//                }
//            }
//        });
//    }
}
