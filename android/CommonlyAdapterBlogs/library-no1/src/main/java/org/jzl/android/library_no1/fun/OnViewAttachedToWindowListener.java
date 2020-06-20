package org.jzl.android.library_no1.fun;

import androidx.recyclerview.widget.RecyclerView;

public interface OnViewAttachedToWindowListener<VH extends RecyclerView.ViewHolder> {
    void onViewAttachedToWindow(VH holder);
}