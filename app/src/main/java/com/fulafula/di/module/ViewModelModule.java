package com.fulafula.di.module;

import androidx.lifecycle.ViewModelProvider;

import com.fulafula.di.AppViewModelFactory;

import dagger.Binds;
import dagger.Module;

//TODO
@Module
public abstract class ViewModelModule {
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(AppViewModelFactory factory);

//    @Binds
//    @IntoMap
//    @ViewModelKey(MainViewModel.class)
//    abstract ViewModel bindMainViewModel(MainViewModel mainViewModel);
}
