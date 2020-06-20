package org.jzl.android.library_no1.v6;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.jzl.android.library_no1.fun.DataBinder;
import org.jzl.android.library_no1.fun.DataClassifier;
import org.jzl.android.library_no1.fun.ItemViewFactory;
import org.jzl.android.library_no1.fun.ObjectBinder;
import org.jzl.android.library_no1.fun.OnCreatedViewHolderListener;
import org.jzl.android.library_no1.fun.OnViewAttachedToWindowListener;
import org.jzl.android.library_no1.fun.ViewHolderFactory;
import org.jzl.android.library_no1.v4.DataProvider;
import org.jzl.android.library_no1.v4.ListDataProvider;
import org.jzl.android.library_no1.vh.CommonlyViewHolder;
import org.jzl.android.provider.ContextProvider;
import org.jzl.lang.util.ArrayUtils;
import org.jzl.lang.util.CollectionUtils;
import org.jzl.lang.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class Configurator<T, VH extends RecyclerView.ViewHolder> {

    public static final int TYPE_ALL = -1;
    public static final int MAX_PLUGIN_NESTING = 10;

    private ViewHolderFactory<VH> viewHolderFactory;
    private DataProvider<T> dataProvider;
    private DataClassifier<T> dataClassifier;

    private ItemViewFactoryObservable itemViewFactoryObservable = new ItemViewFactoryObservable();
    private DataBinderObservable dataBinderObservable;
    private OnAttachedToRecyclerViewListenerObservable attachedToRecyclerViewListenerObservable = new OnAttachedToRecyclerViewListenerObservable();
    private OnCreatedViewHolderListenerObservable createdViewHolderListenerObservable = new OnCreatedViewHolderListenerObservable();
    private OnViewAttachedToWindowListenerObservable viewAttachedToWindowListenerObservable = new OnViewAttachedToWindowListenerObservable();

    private List<PluginHolder<T, VH>> pluginHolders = new ArrayList<>();

    public Configurator(ViewHolderFactory<VH> viewHolderFactory) {
        dataProvider = ListDataProvider.of();
        this.viewHolderFactory = ObjectUtils.requireNonNull(viewHolderFactory, "viewHolderFactory");
        this.dataClassifier = (position, data) -> TYPE_ALL;
        MatchPolicy matchPolicy = (targetViewType, viewType) -> targetViewType == TYPE_ALL || targetViewType == viewType;
        this.dataBinderObservable = new DataBinderObservable(matchPolicy);

    }

    public Configurator<T, VH> itemTypes(DataClassifier<T> dataClassifier) {
        this.dataClassifier = dataClassifier;
        return this;
    }

    public Configurator<T, VH> dataBinds(DataBinder<T, VH> dataBinder, int... viewTypes) {
        if (ArrayUtils.isEmpty(viewTypes)) {
            this.dataBinderObservable.register(TYPE_ALL, dataBinder);
        } else {
            for (int type : viewTypes) {
                this.dataBinderObservable.register(type, dataBinder);
            }
        }
        return this;
    }

    public Configurator<T, VH> createItemViews(ItemViewFactory itemViewFactory, int... viewTypes) {
        if (ArrayUtils.nonEmpty(viewTypes)) {
            this.itemViewFactoryObservable.register(TYPE_ALL, itemViewFactory);
        } else {
            for (int viewType : viewTypes) {
                this.itemViewFactoryObservable.register(viewType, itemViewFactory);
            }
        }
        return this;
    }

    public Configurator<T, VH> data(T... data) {
        dataProvider.addAll(data);
        return this;
    }

    public Configurator<T, VH> addOnAttachedToRecyclerViewListener(CommonlyAdapter.OnAttachedToRecyclerViewListener<T, VH> attachedToRecyclerViewListener) {
        this.attachedToRecyclerViewListenerObservable.register(TYPE_ALL, attachedToRecyclerViewListener);
        return this;
    }

    public Configurator<T, VH> addOnViewAttachedToWindowListener(OnViewAttachedToWindowListener<VH> viewAttachedToWindowListener) {
        this.viewAttachedToWindowListenerObservable.register(TYPE_ALL, viewAttachedToWindowListener);
        return this;
    }

    public Configurator<T, VH> addOnCreatedViewHolderListener(OnCreatedViewHolderListener<VH> createdViewHolderListener) {
        this.createdViewHolderListenerObservable.register(TYPE_ALL, createdViewHolderListener);
        return this;
    }

    public Configurator<T, VH> bindAdapter(ObjectBinder<CommonlyAdapter<T, VH>> binder) {
        return addOnAttachedToRecyclerViewListener((contextProvider, adapter, recyclerView) -> binder.bind(adapter));
    }

    public Configurator<T, VH> bindContextProvider(ObjectBinder<ContextProvider> binder) {
        return addOnAttachedToRecyclerViewListener((contextProvider, adapter, recyclerView) -> binder.bind(contextProvider));
    }

    public Configurator<T, VH> bindRecyclerView(ObjectBinder<RecyclerView> binder) {
        return addOnAttachedToRecyclerViewListener((contextProvider, adapter, recyclerView) -> binder.bind(recyclerView));
    }

    public Configurator<T, VH> bindDataProvider(ObjectBinder<DataProvider<T>> binder) {
        return addOnAttachedToRecyclerViewListener((contextProvider, adapter, recyclerView) -> binder.bind(adapter.dataProvider));
    }

    public Configurator<T, VH> plugin(Plugin<T, VH> plugin, int... viewTypes) {
        this.pluginHolders.add(PluginHolder.of(plugin, viewTypes));
        return this;
    }

    private void plugins(int nesting) {
        if (CollectionUtils.nonEmpty(pluginHolders) && nesting < MAX_PLUGIN_NESTING) {
            for (PluginHolder<T, VH> pluginHolder : pluginHolders) {
                pluginHolder.setup(this);
            }
            plugins(nesting + 1);
        }
    }

    public void bind(RecyclerView recyclerView) {
        plugins(1);
        CommonlyAdapter<T, VH> adapter = new CommonlyAdapter<>(
                viewHolderFactory,
                ContextProvider.of(recyclerView),
                dataProvider,
                dataClassifier,
                itemViewFactoryObservable,
                dataBinderObservable,
                this.viewAttachedToWindowListenerObservable,
                this.attachedToRecyclerViewListenerObservable,
                this.createdViewHolderListenerObservable
        );
        recyclerView.setAdapter(adapter);
    }

    public static <T, VH extends RecyclerView.ViewHolder> Configurator<T, VH> of(ViewHolderFactory<VH> viewHolderFactory) {
        return new Configurator<>(viewHolderFactory);
    }

    public static <T> Configurator<T, CommonlyViewHolder> of() {
        return of((itemView, viewType) -> new CommonlyViewHolder(itemView));
    }


    public interface Plugin<T, VH extends RecyclerView.ViewHolder> {
        void setup(Configurator<T, VH> configurator, int... viewTypes);
    }

    private static class PluginHolder<T, VH extends RecyclerView.ViewHolder> {

        private Plugin<T, VH> plugin;
        private int[] viewTypes;

        public PluginHolder(Plugin<T, VH> plugin, int... viewTypes) {
            this.plugin = plugin;
            this.viewTypes = viewTypes;
        }

        void setup(Configurator<T, VH> configurator) {
            plugin.setup(configurator, viewTypes);
        }

        public static <T, VH extends RecyclerView.ViewHolder> PluginHolder<T, VH> of(Plugin<T, VH> plugin, int... viewTypes) {
            return new PluginHolder<>(ObjectUtils.requireNonNull(plugin, "plugin"), viewTypes);
        }
    }

    public static class ItemViewFactoryObservable extends Observable<ItemViewFactory> implements CommonlyAdapter.CommonlyAdapterItemViewFactory {

        public ItemViewFactoryObservable() {
            super((targetViewType, viewType) -> targetViewType == viewType);
        }

        @Override
        public View createItemView(LayoutInflater layoutInflater, ViewGroup parent, int viewType) {
            return getSingleObserver(viewType).createItemView(layoutInflater, parent);
        }
    }

    public class DataBinderObservable extends Observable<DataBinder<T, VH>> implements DataBinder<T, VH> {

        public DataBinderObservable(MatchPolicy matchPolicy) {
            super(matchPolicy);
        }

        @Override
        public void bind(VH holder, T data) {
            match(holder.getItemViewType(), (index, target) -> {
                target.bind(holder, data);
            });
        }
    }

    public class OnAttachedToRecyclerViewListenerObservable extends Observable<CommonlyAdapter.OnAttachedToRecyclerViewListener<T, VH>> implements CommonlyAdapter.OnAttachedToRecyclerViewListener<T, VH> {

        public OnAttachedToRecyclerViewListenerObservable() {
            super((targetViewType, viewType) -> true);
        }

        @Override
        public void onAttachedToRecyclerView(ContextProvider contextProvider, CommonlyAdapter<T, VH> adapter, RecyclerView recyclerView) {
            match(-1, (index, target) -> target.onAttachedToRecyclerView(contextProvider, adapter, recyclerView));
        }
    }

    public class OnCreatedViewHolderListenerObservable extends Observable<OnCreatedViewHolderListener<VH>> implements OnCreatedViewHolderListener<VH> {

        public OnCreatedViewHolderListenerObservable() {
            super((targetViewType, viewType) -> true);
        }

        @Override
        public void onCreatedViewHolder(VH holder) {
            match(TYPE_ALL, (index, target) -> target.onCreatedViewHolder(holder));
        }
    }

    public class OnViewAttachedToWindowListenerObservable extends Observable<OnViewAttachedToWindowListener<VH>> implements OnViewAttachedToWindowListener<VH> {

        public OnViewAttachedToWindowListenerObservable() {
            super((targetViewType, viewType) -> true);
        }

        @Override
        public void onViewAttachedToWindow(VH holder) {
            match(TYPE_ALL, (index, target) -> target.onViewAttachedToWindow(holder));
        }
    }

}
