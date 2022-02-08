package com.fulafula.view.ui.pdfrender;

import com.fulafula.R;
import com.fulafula.databinding.ActivityPdfRenderBinding;
import com.fulafula.view.common.BaseActivity;

public class PdfRenderActivity extends BaseActivity<ActivityPdfRenderBinding> {

    @Override
    protected String getCurrentClassName() {
        return "PdfRenderActivity";
    }

    @Override
    protected ActivityPdfRenderBinding getViewBinding() {
        return ActivityPdfRenderBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        _binding.toolbar.setTitle(getString(R.string.app_name));

        openPdfRenderFragment();
    }

    private void openPdfRenderFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(_binding.container.getId(), PdfRenderFragment.newInstance(), PdfRenderFragment.TAG)
                .commit();
    }
}