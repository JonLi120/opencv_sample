package com.fulafula.view.ui.main;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.fulafula.databinding.FragmentMainBinding;
import com.fulafula.view.common.BaseFragment;

public class MainFragment extends BaseFragment<FragmentMainBinding> {

    public static final String TAG = "MainFragment";

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private ActivityResultLauncher<String> launcher;

    private boolean isBlurDetection = false;

    @Override
    protected String getCurrentClassName() {
        return TAG;
    }

    @Override
    protected FragmentMainBinding bindingInflater(LayoutInflater inflater, ViewGroup viewGroup) {
        return FragmentMainBinding.inflate(inflater, viewGroup, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                addRectangleDetectionFragment();
            } else {
                Toast.makeText(requireContext(), "Permission was denied.", Toast.LENGTH_SHORT).show();
            }
        });
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        _binding.btnBlurDetection.setOnClickListener(view -> {
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).addBlurDetectionFragment();
            }
        });

        _binding.btnRectangleDetection.setOnClickListener(view -> {
            isBlurDetection = false;
            checkPermission();
        });

        _binding.btnBothDetection.setOnClickListener(view -> {
            isBlurDetection = true;
            checkPermission();
        });
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            addRectangleDetectionFragment();
        } else {
            launcher.launch(Manifest.permission.CAMERA);
        }
    }

    private void addRectangleDetectionFragment() {
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).addRectangleDetectionFragment(isBlurDetection);
        }
    }
}