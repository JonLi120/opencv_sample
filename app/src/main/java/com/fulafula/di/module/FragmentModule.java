package com.fulafula.di.module;

import com.fulafula.view.ui.main.MainFragment;
import com.fulafula.view.ui.opencv.BlurDetectionFragment;
import com.fulafula.view.ui.opencv.RectangleDetectionFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

//TODO
@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract MainFragment contributeMainFragment();

    @ContributesAndroidInjector
    abstract BlurDetectionFragment contributeBlurDetectionFragment();

    @ContributesAndroidInjector
    abstract RectangleDetectionFragment contributeRectangleDetectionFragment();
}
