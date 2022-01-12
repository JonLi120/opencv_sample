package com.fulafula.view.ui.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.fulafula.R;
import com.fulafula.databinding.ActivityMainBinding;
import com.fulafula.utils.AppUtil;
import com.fulafula.view.common.BaseActivity;
import com.fulafula.view.ui.opencv.BlurDetectionFragment;
import com.fulafula.view.ui.opencv.RectangleDetectionFragment;

import javax.inject.Inject;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    @Override
    protected String getCurrentClassName() {
        return "MainActivity";
    }

    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Inject
    protected AppUtil appUtil;

    @Override
    protected void initView() {
        _binding.toolbar.setTitle(getString(R.string.app_name));

        addFragment(MainFragment.newInstance(), MainFragment.TAG, false);
    }

    protected void addBlurDetectionFragment() {
        addFragment(BlurDetectionFragment.newInstance(), BlurDetectionFragment.TAG, true);
    }

    protected void addRectangleDetectionFragment(boolean isBlurDetection) {
        addFragment(RectangleDetectionFragment.newInstance(isBlurDetection), RectangleDetectionFragment.TAG, true);
    }

    private void addFragment(Fragment fragment, String tag, Boolean isBack) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(_binding.container.getId(), fragment, tag);

        if (isBack) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }
}