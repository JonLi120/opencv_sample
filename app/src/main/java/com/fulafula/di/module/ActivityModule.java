package com.fulafula.di.module;

import com.fulafula.view.ui.template.TemplateActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

//TODO
@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract TemplateActivity contributeTemplateActivity();
}
