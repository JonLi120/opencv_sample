package com.fulafula.view.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel {

    protected final CompositeDisposable mDisposable = new CompositeDisposable();

    private final MutableLiveData<Object> _loadStatus = new MutableLiveData<>();

    public LiveData<Object> getLoadStatus() {
        return (LiveData<Object>) _loadStatus;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.clear();
    }
}
