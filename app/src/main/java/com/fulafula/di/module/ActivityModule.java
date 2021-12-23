package com.fulafula.di.module;

import com.fulafula.view.ui.main.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

//TODO
@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();
}
