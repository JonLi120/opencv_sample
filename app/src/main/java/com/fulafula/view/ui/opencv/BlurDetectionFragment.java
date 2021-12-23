package com.fulafula.view.ui.opencv;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.fulafula.databinding.FragmentBlurDetectionBinding;
import com.fulafula.di.AppViewModelFactory;
import com.fulafula.view.common.BaseFragment;
import com.fulafula.view.ui.main.MainViewModel;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import javax.inject.Inject;

public class BlurDetectionFragment extends BaseFragment<FragmentBlurDetectionBinding> {

    public static final String TAG = "BlurDetectionFragment";

    public static BlurDetectionFragment newInstance() {
        return new BlurDetectionFragment();
    }

    private static final int BLUR_THRESHOLD = -6118750;

    @Inject
    AppViewModelFactory factory;

    private MainViewModel viewModel;

    private ActivityResultLauncher<String> launcher;

    private Mat sourceMatImage;

    private BaseLoaderCallback loaderCallback;

    @Override
    protected String getCurrentClassName() {
        return TAG;
    }

    @Override
    protected FragmentBlurDetectionBinding bindingInflater(LayoutInflater inflater, ViewGroup viewGroup) {
        return FragmentBlurDetectionBinding.inflate(inflater, viewGroup, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::showPhoto);
        super.onCreate(savedInstanceState);

        loaderCallback = new BaseLoaderCallback(requireContext()) {
            @Override
            public void onManagerConnected(int status) {
                if (status == LoaderCallbackInterface.SUCCESS) {
                    sourceMatImage = new Mat();
                } else {
                    super.onManagerConnected(status);
                }
            }

            @Override
            public void onPackageInstall(int operation, InstallCallbackInterface callback) {
                super.onPackageInstall(operation, callback);
            }
        };
    }

    @Override
    protected void initView() {
        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
        _binding.btnSelectImage.setOnClickListener(view -> launcher.launch("image/*"));

        setupObserver();
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

    private void showPhoto(Uri uri) {
        _binding.imgPhoto.setImageURI(uri);

        if (uri != null) {
            viewModel.getScoreFromOpenCV(requireActivity().getContentResolver(), uri, sourceMatImage);
        }
    }

    private void setupObserver() {
        viewModel.getBlurScore().observe(getViewLifecycleOwner(), score -> {
            _binding.tvDetectionInfo.setText(String.format("Blur Detection Score：%s", score));
            if (score <= BLUR_THRESHOLD) {
                _binding.tvIsBlur.setText("This image is blurry.");
                _binding.tvIsBlur.setTextColor(Color.RED);
            } else {
                _binding.tvIsBlur.setText("This image is clear.");
                _binding.tvIsBlur.setTextColor(Color.GREEN);
            }
        });
    }
}
