package com.fulafula.view.ui.opencv;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fulafula.databinding.DialogPicturePreviewBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PicturePreviewDialog extends BottomSheetDialogFragment {

    public static final String TAG = "PicturePreviewDialog";
    public static final String KEY_REQUEST_CANCEL = "KEY_REQUEST_CANCEL";
    private static final String EXTRA_BLUR_SCORE = "EXTRA_BLUR_SCORE";
    private static final String EXTRA_IS_DETECT_BLUR = "EXTRA_IS_DETECT_BLUR";
    private static final String EXTRA_URI = "EXTRA_URI";
    private static final int BLUR_THRESHOLD = -6118750;

    DialogPicturePreviewBinding _binding;

    public static PicturePreviewDialog newInstance(Uri uri, int blurScore, boolean isDetectBlur) {
        PicturePreviewDialog fragment = new PicturePreviewDialog();

        Bundle args = new Bundle();
        args.putInt(EXTRA_BLUR_SCORE, blurScore);
        args.putBoolean(EXTRA_IS_DETECT_BLUR, isDetectBlur);
        args.putParcelable(EXTRA_URI, uri);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _binding = DialogPicturePreviewBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            Uri uri = getArguments().getParcelable(EXTRA_URI);
            Glide.with(requireContext())
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(_binding.imgPhoto);

            if (getArguments().getBoolean(EXTRA_IS_DETECT_BLUR)) {
                _binding.tvBlurScore.setVisibility(View.VISIBLE);

                int score = getArguments().getInt(EXTRA_BLUR_SCORE);

                if (score <= BLUR_THRESHOLD) {
                    _binding.tvBlurScore.setText(String.format("This image is blurry. score: %d", score));
                    _binding.tvBlurScore.setTextColor(Color.RED);
                } else {
                    _binding.tvBlurScore.setText(String.format("This image is clear. score: %d", score));
                    _binding.tvBlurScore.setTextColor(Color.GREEN);
                }
            } else {
                _binding.tvBlurScore.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        _binding = null;
        super.onDestroyView();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        getParentFragmentManager().setFragmentResult(KEY_REQUEST_CANCEL, new Bundle());
        super.onCancel(dialog);
    }
}
