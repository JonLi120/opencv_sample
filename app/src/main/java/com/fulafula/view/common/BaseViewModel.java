package com.fulafula.view.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel {

    protected final CompositeDisposable mDisposable = new CompositeDisposable();

    protected final MutableLiveData<Boolean> _loadStatus = new MutableLiveData<>();

    public LiveData<Boolean> getLoadStatus() {
        return _loadStatus;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.clear();
    }
}
