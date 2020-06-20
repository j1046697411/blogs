package org.jzl.android.library_no1.v5;

import androidx.recyclerview.widget.RecyclerView;

import org.jzl.android.library_no1.fun.DataBinder;
import org.jzl.android.library_no1.fun.DataClassifier;
import org.jzl.android.library_no1.fun.ItemViewFactory;
import org.jzl.android.library_no1.fun.OnAttachedToRecyclerViewListener;
import org.jzl.android.library_no1.fun.OnCreatedViewHolderListener;
import org.jzl.android.library_no1.fun.OnViewAttachedToWindowListener;
import org.jzl.android.library_no1.fun.ViewHolderFactory;
import org.jzl.android.library_no1.v4.DataProvider;
import org.jzl.android.library_no1.v4.ListDataProvider;
import org.jzl.android.library_no1.v4.Observable;
import org.jzl.android.library_no1.vh.CommonlyViewHolder;
import org.jzl.android.provider.ContextProvider;
import org.jzl.lang.util.ArrayUtils;
import org.jzl.lang.util.ObjectUtils;

public class Configurator<T, VH extends RecyclerView.ViewHolder> {

    public static final int TYPE_ALL = -1;

    private ViewHolderFactory<VH> viewHolderFactory;
    private DataProvider<T> dataProvider;
    private DataClassifier<T> dataClassifier;

    private Observable<ItemViewFactory> itemViewFactories = new Observable<>();
    private Observable<DataBinder<T, VH>> dataBinders = new Observable<>();

    private OnAttachedToRecyclerViewListener<T, VH> attachedToRecyclerViewListener;
    private OnCreatedViewHolderListener<VH> createdViewHolderListener;
    private OnViewAttachedToWindowListener<VH> viewAttachedToWindowListener;


    public Configurator(ViewHolderFactory<VH> viewHolderFactory) {
        dataProvider = ListDataProvider.of();
        this.viewHolderFactory = ObjectUtils.requireNonNull(viewHolderFactory, "viewHolderFactory");
        this.dataClassifier = (position, data) -> TYPE_ALL;
    }

    public Configurator<T, VH> itemTypes(DataClassifier<T> dataClassifier) {
        this.dataClassifier = dataClassifier;
        return this;
    }

    public Configurator<T, VH> dataBinds(DataBinder<T, VH> dataBinder, int... viewTypes) {
        if (ArrayUtils.isEmpty(viewTypes)) {
            this.dataBinders.register(TYPE_ALL, dataBinder);
        } else {
            for (int type : viewTypes) {
                this.dataBinders.register(type, dataBinder);
            }
        }
        return this;
    }

    public Configurator<T, VH> createItemViews(ItemViewFactory itemViewFactory, int... viewTypes) {
        if (ArrayUtils.nonEmpty(viewTypes)) {
            this.itemViewFactories.register(TYPE_ALL, itemViewFactory);
        } else {
            for (int viewType : viewTypes) {
                this.itemViewFactories.register(viewType, itemViewFactory);
            }
        }
        return this;
    }

    public Configurator<T, VH> data(T... data) {
        dataProvider.addAll(data);
        return this;
    }

    public Configurator<T, VH> setAttachedToRecyclerViewListener(OnAttachedToRecyclerViewListener<T, VH> attachedToRecyclerViewListener) {
        this.attachedToRecyclerViewListener = attachedToRecyclerViewListener;
        return this;
    }

    public Configurator<T, VH> setViewAttachedToWindowListener(OnViewAttachedToWindowListener<VH> viewAttachedToWindowListener) {
        this.viewAttachedToWindowListener = viewAttachedToWindowListener;
        return this;
    }

    public Configurator<T, VH> setCreatedViewHolderListener(OnCreatedViewHolderListener<VH> createdViewHolderListener) {
        this.createdViewHolderListener = createdViewHolderListener;
        return this;
    }

    public void bind(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
        CommonlyAdapter<T, VH> adapter = new CommonlyAdapter<>(
                viewHolderFactory,
                ContextProvider.of(recyclerView),
                dataProvider,
                dataClassifier,
                itemViewFactories,
                dataBinders,
                this.viewAttachedToWindowListener,
                this.attachedToRecyclerViewListener,
                this.createdViewHolderListener
        );
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    public static <T, VH extends RecyclerView.ViewHolder> Configurator<T, VH> of(ViewHolderFactory<VH> viewHolderFactory){
        return new Configurator<>(viewHolderFactory);
    }

    public static <T> Configurator<T, CommonlyViewHolder> of(){
        return of((itemView, viewType) -> new CommonlyViewHolder(itemView));
    }

}
