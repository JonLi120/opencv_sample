package com.fulafula.di.module;

import com.fulafula.view.ui.template.TemplateFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

//TODO
@Module
public abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract TemplateFragment contributeTemplateFragment();
}
