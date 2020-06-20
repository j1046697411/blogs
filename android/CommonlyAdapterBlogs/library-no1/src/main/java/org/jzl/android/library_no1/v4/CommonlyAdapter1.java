package org.jzl.android.library_no1.v4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jzl.android.library_no1.fun.DataBinder;
import org.jzl.android.library_no1.fun.DataClassifier;
import org.jzl.android.library_no1.fun.ViewHolderFactory;
import org.jzl.android.provider.ContextProvider;
import org.jzl.lang.util.ObjectUtils;

public class CommonlyAdapter1<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public static final int TYPE_ALL = -1;

    private LayoutInflater layoutInflater;
    private DataBinder<T, VH> dataBinder;
    private ItemViewFactory2 itemViewFactory;
    private ViewHolderFactory<VH> viewHolderFactory;
    private ContextProvider contextProvider;
    private DataProvider<T> dataProvider;
    private DataClassifier<T> dataClassifier;

    public CommonlyAdapter1(ContextProvider contextProvider, DataProvider<T> dataProvider) {
        this.contextProvider = contextProvider;
        this.layoutInflater = LayoutInflater.from(contextProvider.provide());
        this.dataProvider = dataProvider;
        dataClassifier = (position, data) -> TYPE_ALL;
        dataProvider.bind(this);
    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewHolderFactory.createViewHolder(itemViewFactory.createItemView(layoutInflater, parent, viewType), viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        dataBinder.bind(holder, dataProvider.getData(position));
    }

    @Override
    public int getItemCount() {
        return dataProvider.getDataCount();
    }

    public CommonlyAdapter1<T, VH> setDataBinder(DataBinder<T, VH> dataBinder) {
        this.dataBinder = dataBinder;
        return this;
    }

    public CommonlyAdapter1<T, VH> setViewHolderFactory(ViewHolderFactory<VH> viewHolderFactory) {
        this.viewHolderFactory = viewHolderFactory;
        return this;
    }

    public CommonlyAdapter1<T, VH> setItemViewFactory(ItemViewFactory2 itemViewFactory) {
        this.itemViewFactory = itemViewFactory;
        return this;
    }

    @Override
    public int getItemViewType(int position) {
        return dataClassifier.getItemType(position, dataProvider.getData(position));
    }

    public CommonlyAdapter1<T, VH> setDataClassifier(DataClassifier<T> dataClassifier) {
        this.dataClassifier = ObjectUtils.get(dataClassifier, this.dataClassifier);
        return this;
    }

    public void bind(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
        recyclerView.setAdapter(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    public static <T, VH extends RecyclerView.ViewHolder> CommonlyAdapter1<T, VH> of(ContextProvider contextProvider, DataProvider<T> dataProvider) {
        return new CommonlyAdapter1<>(contextProvider, dataProvider);
    }

    public interface ItemViewFactory2 {
        View createItemView(LayoutInflater layoutInflater, ViewGroup parent, int viewType);
    }
}
