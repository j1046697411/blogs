# 第六步，插件的出现，终于可以不用在重复编写代码了
## 前言
去掉重复，让事情变得更简单
## 目标
### 1、插件的出现，终于可以不用在重复编写代码了
### 2、使得适配器内的属性在外部跟容易获取
## 实现代码
先上代码，有🐎有真相
```java
public class MainActivity_v6 extends AppCompatActivity {

    private DataProvider<String> dataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Configurator.<String>of()
                .itemTypes((position, data) -> position % 2)
                .dataBinds((holder, data) -> holder.provide().setText(R.id.tv_test, data))
                .createItemViews((layoutInflater, parent) -> layoutInflater.inflate(R.layout.item_test, parent, false), 0)
                .createItemViews((layoutInflater, parent) -> layoutInflater.inflate(R.layout.item_test_3, parent, false), 1)
                .bindRecyclerView(target -> target.setLayoutManager(new LinearLayoutManager(this)))
                .bindDataProvider(target -> this.dataProvider = target)
                .plugin(new DataPlugin())
                .bind(findViewById(R.id.rv_test));
    }

    public static class DataPlugin implements Configurator.Plugin<String, CommonlyViewHolder> {

        @Override
        public void setup(Configurator<String, CommonlyViewHolder> configurator, int... viewTypes) {
            configurator.data("1", "2", "3", "4");
        }
    }

}
```
```java
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
```
```java
public class Observable<T> {

    private MatchPolicy matchPolicy;
    private List<ObserverHolder<T>> holders;

    public Observable(MatchPolicy matchPolicy) {
        this.matchPolicy = ObjectUtils.requireNonNull(matchPolicy, "matchPolicy");
        this.holders = new ArrayList<>();
    }

    public void register(int viewType, T observer) {
        if (ObjectUtils.nonNull(observer)) {
            this.holders.add(ObserverHolder.of(viewType, observer));
        }
    }

    public void match(int targetViewType, IntConsumer<T> consumer) {
        for (ObserverHolder<T> holder : holders) {
            int viewType = holder.getViewType();
            if (matchPolicy.match(targetViewType, viewType)) {
                consumer.accept(viewType, holder.getObserver());
            }
        }
    }

    public T getSingleObserver(int targetViewType) {
        for (ObserverHolder<T> holder : holders) {
            if (matchPolicy.match(targetViewType, holder.getViewType())) {
                return holder.getObserver();
            }
        }
        return null;
    }

    private static class ObserverHolder<T> {

        private int viewType;
        private T observer;

        private ObserverHolder(int viewType, T observer) {
            this.viewType = viewType;
            this.observer = observer;
        }

        public int getViewType() {
            return viewType;
        }

        public T getObserver() {
            return observer;
        }

        public static <T> ObserverHolder<T> of(int viewType, T observer) {
            return new ObserverHolder<>(viewType, ObjectUtils.requireNonNull(observer, "observer"));
        }
    }
}
```
&emsp;&emsp;插件功能终于完成了，现在就可以愉快的告别那重复又枯燥的重复代码编写时光。  
&emsp;&emsp;现在整个项目到这儿差不多就整体实现了，还有很多细节方面需要实现的，这里就先不实现了，大家有空就可以自由发挥了。
&emsp;&emsp;现在我们来看看我们最初顶下的目标，`使用简单`相信大家应该都看的出来把！`易于扩展`，哈哈，我们有了插件，在加上Configurator
类一系列强大的方法，扩展不是轻轻松松的事吗？（虽然还有一部分方法没有实现，这个就看自己发挥了）`代码复用`别说不会，通过各种回调和插件系统这个应该
也算完成了把，至于`适配java8的lambda语法`和`引进java流行的链式编程`就更不用说了，使用过程中正好。
#### 码了一天的字了，谢谢大家看

#加油！！！努力的人最帅！
# link
[作者](https://github.com/j1046697411)  
[CommonlyAdapter系列文章](commonly_adapter.md)  