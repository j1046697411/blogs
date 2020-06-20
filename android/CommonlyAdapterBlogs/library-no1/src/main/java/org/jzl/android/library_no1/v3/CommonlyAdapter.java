package org.jzl.android.library_no1.v3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jzl.android.library_no1.fun.DataBinder;
import org.jzl.android.library_no1.fun.ItemViewFactory;
import org.jzl.android.library_no1.fun.ViewHolderFactory;

import java.util.List;

public class CommonlyAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> data;
    private LayoutInflater layoutInflater;
    private DataBinder<T, VH> dataBinder;
    private ItemViewFactory itemViewFactory;
    private ViewHolderFactory<VH> viewHolderFactory;

    public CommonlyAdapter(Context context, List<T> data) {
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
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

    public CommonlyAdapter<T, VH> setDataBinder(DataBinder<T, VH> dataBinder) {
        this.dataBinder = dataBinder;
        return this;
    }

    public CommonlyAdapter<T, VH> setViewHolderFactory(ViewHolderFactory<VH> viewHolderFactory) {
        this.viewHolderFactory = viewHolderFactory;
        return this;
    }

    public CommonlyAdapter<T, VH> setItemViewFactory(ItemViewFactory itemViewFactory) {
        this.itemViewFactory = itemViewFactory;
        return this;
    }

    public void bind(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager){
        recyclerView.setAdapter(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    public static <T, VH extends RecyclerView.ViewHolder> CommonlyAdapter<T, VH> of(Context context, List<T> data){
        return new CommonlyAdapter<>(context, data);
    }
}
