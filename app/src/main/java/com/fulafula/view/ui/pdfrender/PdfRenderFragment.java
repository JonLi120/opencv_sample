package com.fulafula.view.ui.pdfrender;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fulafula.databinding.FragmentPdfRenderBinding;
import com.fulafula.di.AppViewModelFactory;
import com.fulafula.view.common.BaseFragment;

import javax.inject.Inject;

public class PdfRenderFragment extends BaseFragment<FragmentPdfRenderBinding> {

    public static final String TAG = "PdfRenderFragment";

    public static PdfRenderFragment newInstance() {
        return new PdfRenderFragment();
    }

    @Inject
    AppViewModelFactory factory;

    private PdfRenderViewModel viewModel;
    private PdfPageAdapter pageAdapter;

    @Override
    protected String getCurrentClassName() {
        return TAG;
    }

    @Override
    protected FragmentPdfRenderBinding bindingInflater(LayoutInflater inflater, ViewGroup viewGroup) {
        return FragmentPdfRenderBinding.inflate(inflater, viewGroup, false);
    }

    @Override
    protected void initView() {
        viewModel = new ViewModelProvider(this, factory).get(PdfRenderViewModel.class);

        observeViewModel();

        pageAdapter = new PdfPageAdapter();

        _binding.rcvPdfPreview.setHasFixedSize(true);
        _binding.rcvPdfPreview.setLayoutManager(new LinearLayoutManager(requireContext()));
        _binding.rcvPdfPreview.setAdapter(pageAdapter);

        String PDF_URL = "https://www.twca.com.tw/upload/saveArea/filePage/20211227/1d6d497c08914cc3bb0b24831e811026/1d6d497c08914cc3bb0b24831e811026.pdf";
        viewModel.downloadPdf(PDF_URL);
    }

    private void observeViewModel() {
        viewModel.getLoadStatus().observe(getViewLifecycleOwner(), isShowLoading -> {
            if (isShowLoading) {
                startLoadingDialog();
            } else {
                stopLoadingDialog();
            }
        });

        viewModel.getBitmaps().observe(getViewLifecycleOwner(), bitmaps -> pageAdapter.update(bitmaps));
    }
}