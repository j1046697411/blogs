package org.jzl.android.library_no1.v4;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jzl.android.library_no1.fun.DataBinder;
import org.jzl.android.library_no1.fun.DataClassifier;
import org.jzl.android.library_no1.fun.ItemViewFactory;
import org.jzl.android.library_no1.fun.ViewHolderFactory;
import org.jzl.android.provider.ContextProvider;
import org.jzl.lang.util.ObjectUtils;

public class CommonlyAdapter2<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public static final int TYPE_ALL = -1;

    private LayoutInflater layoutInflater;
    private ViewHolderFactory<VH> viewHolderFactory;
    private ContextProvider contextProvider;
    private DataProvider<T> dataProvider;
    private DataClassifier<T> dataClassifier;

    private Observable<ItemViewFactory> itemViewFactories = new Observable<>();
    private Observable<DataBinder<T, VH>> dataBinders = new Observable<>();

    public CommonlyAdapter2(ContextProvider contextProvider, DataProvider<T> dataProvider) {
        this.contextProvider = contextProvider;
        this.layoutInflater = LayoutInflater.from(contextProvider.provide());
        this.dataProvider = dataProvider;
        dataClassifier = (position, data) -> TYPE_ALL;
        dataProvider.bind(this);
    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewFactory factory = itemViewFactories.getObserver(viewType);
        ObjectUtils.requireNonNull(factory, "itemViewFactory");
        return viewHolderFactory.createViewHolder(factory.createItemView(layoutInflater, parent), viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        DataBinder<T, VH> dataBinder = dataBinders.getObserver(holder.getItemViewType());
        if (ObjectUtils.nonNull(dataBinder)) {
            dataBinder.bind(holder, dataProvider.getData(position));
        }
    }

    @Override
    public int getItemCount() {
        return dataProvider.getDataCount();
    }

    public CommonlyAdapter2<T, VH> addDataBinder(DataBinder<T, VH> dataBinder, int viewType) {
        this.dataBinders.register(viewType, dataBinder);
        return this;
    }

    public CommonlyAdapter2<T, VH> setViewHolderFactory(ViewHolderFactory<VH> viewHolderFactory) {
        this.viewHolderFactory = viewHolderFactory;
        return this;
    }

    public CommonlyAdapter2<T, VH> addItemViewFactory(ItemViewFactory itemViewFactory, int viewType) {
        this.itemViewFactories.register(viewType, itemViewFactory);
        return this;
    }

    @Override
    public int getItemViewType(int position) {
        return dataClassifier.getItemType(position, dataProvider.getData(position));
    }

    public CommonlyAdapter2<T, VH> setDataClassifier(DataClassifier<T> dataClassifier) {
        this.dataClassifier = ObjectUtils.get(dataClassifier, this.dataClassifier);
        return this;
    }

    public void bind(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
        recyclerView.setAdapter(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    public static <T, VH extends RecyclerView.ViewHolder> CommonlyAdapter2<T, VH> of(ContextProvider contextProvider, DataProvider<T> dataProvider) {
        return new CommonlyAdapter2<>(contextProvider, dataProvider);
    }

}
