package com.fulafula.di.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.fulafula.di.AppViewModelFactory;
import com.fulafula.di.ViewModelKey;
import com.fulafula.view.ui.main.MainViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

//TODO
@Module
public abstract class ViewModelModule {
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(AppViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel bindMainViewModel(MainViewModel mainViewModel);
}
