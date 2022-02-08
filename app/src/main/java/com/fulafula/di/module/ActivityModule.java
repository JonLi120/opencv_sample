package com.fulafula.di.module;

import com.fulafula.view.ui.pdfrender.PdfRenderActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

//TODO
@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract PdfRenderActivity contributePdfRenderActivity();
}
