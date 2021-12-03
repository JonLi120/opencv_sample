package com.fulafula.view.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.fulafula.di.Injectable;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public abstract class BaseFragment<VB extends ViewBinding> extends Fragment implements Injectable {

    protected final CompositeDisposable mDisposable = new CompositeDisposable();

    protected VB _binding;

    protected abstract String getCurrentClassName();

    protected abstract VB bindingInflater(LayoutInflater inflater, ViewGroup viewGroup);

    protected abstract void initView();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _binding = bindingInflater(inflater, container);
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onDestroyView() {
        mDisposable.clear();
        super.onDestroyView();
        _binding = null;
    }
}
