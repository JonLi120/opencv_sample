package com.fulafula.view.ui.main;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fulafula.databinding.FragmentMainBinding;
import com.fulafula.view.common.BaseFragment;

public class MainFragment extends BaseFragment<FragmentMainBinding> {

    public static final String TAG = "MainFragment";

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    protected String getCurrentClassName() {
        return TAG;
    }

    @Override
    protected FragmentMainBinding bindingInflater(LayoutInflater inflater, ViewGroup viewGroup) {
        return FragmentMainBinding.inflate(inflater, viewGroup, false);
    }

    @Override
    protected void initView() {
        _binding.btnBlurDetection.setOnClickListener(view -> {
            if (requireActivity() instanceof MainActivity) {
                ((MainActivity) requireActivity()).addBlurDetectionFragment();
            }
        });

        _binding.btnRectangleDetection.setOnClickListener(view -> {

        });
    }
}