package org.jzl.android.library_no1.v6;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jzl.android.library_no1.fun.DataBinder;
import org.jzl.android.library_no1.fun.DataClassifier;
import org.jzl.android.library_no1.fun.OnCreatedViewHolderListener;
import org.jzl.android.library_no1.fun.OnViewAttachedToWindowListener;
import org.jzl.android.library_no1.fun.ViewHolderFactory;
import org.jzl.android.library_no1.v4.DataProvider;
import org.jzl.android.provider.ContextProvider;
import org.jzl.lang.util.ObjectUtils;

public class CommonlyAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private LayoutInflater layoutInflater;
    private ViewHolderFactory<VH> viewHolderFactory;
    private ContextProvider contextProvider;
    DataProvider<T> dataProvider;
    private DataClassifier<T> dataClassifier;

    private CommonlyAdapterItemViewFactory itemViewFactory;
    private DataBinder<T, VH> dataBinder;
    private OnViewAttachedToWindowListener<VH> viewAttachedToWindowListener;
    private OnAttachedToRecyclerViewListener<T, VH> attachedToRecyclerViewListener;
    private OnCreatedViewHolderListener<VH> createdViewHolderListener;

    public CommonlyAdapter(ViewHolderFactory<VH> viewHolderFactory,
                           ContextProvider contextProvider,
                           DataProvider<T> dataProvider,
                           DataClassifier<T> dataClassifier,
                           CommonlyAdapterItemViewFactory itemViewFactory,
                           DataBinder<T, VH> dataBinder,
                           OnViewAttachedToWindowListener<VH> viewAttachedToWindowListener,
                           OnAttachedToRecyclerViewListener<T, VH> attachedToRecyclerViewListener,
                           OnCreatedViewHolderListener<VH> createdViewHolderListener) {
        this.layoutInflater = LayoutInflater.from(contextProvider.provide());
        this.viewHolderFactory = viewHolderFactory;
        this.contextProvider = contextProvider;
        this.dataProvider = dataProvider;
        this.dataClassifier = dataClassifier;
        this.itemViewFactory = itemViewFactory;
        this.dataBinder = dataBinder;
        this.viewAttachedToWindowListener = viewAttachedToWindowListener;
        this.attachedToRecyclerViewListener = attachedToRecyclerViewListener;
        this.createdViewHolderListener = createdViewHolderListener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (ObjectUtils.nonNull(attachedToRecyclerViewListener)) {
            attachedToRecyclerViewListener.onAttachedToRecyclerView(contextProvider, this, recyclerView);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull VH holder) {
        super.onViewAttachedToWindow(holder);
        if (ObjectUtils.nonNull(viewAttachedToWindowListener)) {
            viewAttachedToWindowListener.onViewAttachedToWindow(holder);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VH holder = viewHolderFactory.createViewHolder(itemViewFactory.createItemView(layoutInflater, parent, viewType), viewType);
        if (ObjectUtils.nonNull(createdViewHolderListener)) {
            createdViewHolderListener.onCreatedViewHolder(holder);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (ObjectUtils.nonNull(dataBinder)) {
            dataBinder.bind(holder, dataProvider.getData(position));
        }
    }

    @Override
    public int getItemCount() {
        return dataProvider.getDataCount();
    }

    @Override
    public int getItemViewType(int position) {
        return dataClassifier.getItemType(position, dataProvider.getData(position));
    }

    public interface OnAttachedToRecyclerViewListener<T, VH extends RecyclerView.ViewHolder> {
        void onAttachedToRecyclerView(ContextProvider contextProvider, CommonlyAdapter<T, VH> adapter, RecyclerView recyclerView);
    }

    public interface CommonlyAdapterItemViewFactory {
        View createItemView(LayoutInflater layoutInflater, ViewGroup parent, int viewType);
    }

}
