package org.jzl.android.library_no1.v2;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jzl.android.library_no1.fun.DataBinder;
import org.jzl.android.library_no1.fun.ItemViewFactory;
import org.jzl.android.library_no1.fun.ViewHolderFactory;
import org.jzl.lang.util.ObjectUtils;

import java.util.List;

public class CommonlyAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> data;
    private LayoutInflater layoutInflater;
    private DataBinder<T, VH> dataBinder;
    private ItemViewFactory itemViewFactory;
    private ViewHolderFactory<VH> viewHolderFactory;

    public CommonlyAdapter(Context context, List<T> data, DataBinder<T, VH> dataBinder, ItemViewFactory itemViewFactory, ViewHolderFactory<VH> vhViewHolderFactory) {
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
        this.dataBinder = dataBinder;
        this.itemViewFactory = itemViewFactory;
        this.viewHolderFactory = vhViewHolderFactory;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = itemViewFactory.createItemView(layoutInflater, parent);
        return viewHolderFactory.createViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        dataBinder.bind(holder, data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
