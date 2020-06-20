package org.jzl.android.library_no1.fun;

import androidx.recyclerview.widget.RecyclerView;

import org.jzl.android.library_no1.v4.CommonlyAdapter1;
import org.jzl.android.library_no1.v5.CommonlyAdapter;
import org.jzl.android.provider.ContextProvider;

public interface OnAttachedToRecyclerViewListener<T, VH extends RecyclerView.ViewHolder> {
    void onAttachedToRecyclerView(ContextProvider contextProvider, CommonlyAdapter<T, VH> adapter, RecyclerView recyclerView);
}