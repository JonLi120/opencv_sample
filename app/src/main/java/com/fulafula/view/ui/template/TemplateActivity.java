package com.fulafula.view.ui.template;

import com.fulafula.R;
import com.fulafula.databinding.ActivityTemplateBinding;
import com.fulafula.utils.AppUtil;
import com.fulafula.view.common.BaseActivity;

import javax.inject.Inject;

public class TemplateActivity extends BaseActivity<ActivityTemplateBinding> {

    @Override
    protected String getCurrentClassName() {
        return "TemplateActivity";
    }

    @Override
    protected ActivityTemplateBinding getViewBinding() {
        return ActivityTemplateBinding.inflate(getLayoutInflater());
    }

    @Inject
    protected AppUtil appUtil;

    @Override
    protected void initView() {
        _binding.toolbar.setTitle(getString(R.string.app_name));

        startFragment(appUtil.generateUserAgent());
    }

    private void startFragment(String params1) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(_binding.container.getId(), TemplateFragment.newInstance(params1), TemplateFragment.TAG)
                .commit();
    }
}