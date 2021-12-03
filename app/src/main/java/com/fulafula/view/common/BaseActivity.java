package com.fulafula.view.common;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.fulafula.utils.AppUtil;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity implements HasAndroidInjector {

    @Inject
    DispatchingAndroidInjector<Object> androidInjector;


    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }

    protected final CompositeDisposable mDisposable = new CompositeDisposable();

    protected VB _binding = null;

    protected abstract String getCurrentClassName();

    protected abstract VB getViewBinding();

    protected abstract void initView();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(AppUtil.configFontScale(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _binding = getViewBinding();

        setContentView(_binding.getRoot());

        initView();
    }

    @Override
    protected void onDestroy() {
        mDisposable.clear();
        super.onDestroy();
        _binding = null;
    }
}
