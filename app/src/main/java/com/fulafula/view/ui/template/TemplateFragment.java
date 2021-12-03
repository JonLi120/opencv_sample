package com.fulafula.view.ui.template;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fulafula.databinding.FragmentTemplateBinding;
import com.fulafula.view.common.BaseFragment;

public class TemplateFragment extends BaseFragment<FragmentTemplateBinding> {

    public static final String TAG = "TemplateFragment";
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    public static TemplateFragment newInstance(String param1) {
        TemplateFragment fragment = new TemplateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    protected String getCurrentClassName() {
        return TAG;
    }

    @Override
    protected FragmentTemplateBinding bindingInflater(LayoutInflater inflater, ViewGroup viewGroup) {
        return FragmentTemplateBinding.inflate(inflater, viewGroup, false);
    }

    @Override
    protected void initView() {
        _binding.tvBuildName.setText(String.format("Build Name：%s", mParam1));
    }
}