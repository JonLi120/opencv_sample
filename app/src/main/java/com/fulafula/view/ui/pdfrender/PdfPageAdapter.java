package com.fulafula.view.ui.pdfrender;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fulafula.databinding.ViewPdfPageBinding;

import java.util.ArrayList;
import java.util.List;

public class PdfPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Bitmap> list = new ArrayList<>();

    public void update(List<Bitmap> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PageViewHolder(ViewPdfPageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PageViewHolder) {
            ((PageViewHolder) holder).onBind(list.get(position));
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        for (Bitmap bitmap : list) {
            bitmap.recycle();
        }
        list.clear();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    private static class PageViewHolder extends RecyclerView.ViewHolder {

        private final ViewPdfPageBinding _binding;

        public PageViewHolder(@NonNull ViewPdfPageBinding binding) {
            super(binding.getRoot());
            _binding = binding;
        }

        public void onBind(Bitmap bitmap) {
            _binding.imgPagePreview.setImageBitmap(bitmap);
        }
    }
}
