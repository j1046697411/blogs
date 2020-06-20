package org.jzl.android.library_no1.fun;

import androidx.recyclerview.widget.RecyclerView;

public interface OnCreatedViewHolderListener<VH extends RecyclerView.ViewHolder> {
    void onCreatedViewHolder(VH holder);
}
