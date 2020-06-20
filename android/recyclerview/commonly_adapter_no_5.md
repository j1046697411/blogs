# 第五步，我本就很累，为什么还需要做这么多事？ Configurator 的到来，为我分担任务
## 前言
学会分工，相互合作
## 目标
### 1、我本就很累，为什么还需要做这么多事？ Configurator 的到来，为我分担任务
### 2、添加更多的事件回调，让我们掌握全局。
## 实现代码
先上代码，有🐎有真相
```java
public class MainActivity_v5 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Configurator.<String>of()
                .data("1", "2", "3", "4")
                .itemTypes((position, data) -> position % 2)
                .dataBinds((holder, data) -> holder.provide().setText(R.id.tv_test, data), 0, 1)
                .createItemViews((layoutInflater, parent) -> layoutInflater.inflate(R.layout.item_test, parent, false), 0)
                .createItemViews((layoutInflater, parent) -> layoutInflater.inflate(R.layout.item_test_3, parent, false), 1)
                .bind(findViewById(R.id.rv_test), new LinearLayoutManager(this));
    }

}
```
```java
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

```
```java
public class CommonlyAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private LayoutInflater layoutInflater;
    private ViewHolderFactory<VH> viewHolderFactory;
    private ContextProvider contextProvider;
    private DataProvider<T> dataProvider;
    private DataClassifier<T> dataClassifier;

    private Observable<ItemViewFactory> itemViewFactories;
    private Observable<DataBinder<T, VH>> dataBinders;
    private OnViewAttachedToWindowListener<VH> viewAttachedToWindowListener;
    private OnAttachedToRecyclerViewListener<T, VH> attachedToRecyclerViewListener;
    private OnCreatedViewHolderListener<VH> createdViewHolderListener;

    public CommonlyAdapter(ViewHolderFactory<VH> viewHolderFactory,
                           ContextProvider contextProvider,
                           DataProvider<T> dataProvider,
                           DataClassifier<T> dataClassifier,
                           Observable<ItemViewFactory> itemViewFactories,
                           Observable<DataBinder<T, VH>> dataBinders,
                           OnViewAttachedToWindowListener<VH> viewAttachedToWindowListener,
                           OnAttachedToRecyclerViewListener<T, VH> attachedToRecyclerViewListener,
                           OnCreatedViewHolderListener<VH> createdViewHolderListener) {
        this.layoutInflater = LayoutInflater.from(contextProvider.provide());
        this.viewHolderFactory = viewHolderFactory;
        this.contextProvider = contextProvider;
        this.dataProvider = dataProvider;
        this.dataClassifier = dataClassifier;
        this.itemViewFactories = itemViewFactories;
        this.dataBinders = dataBinders;
        this.viewAttachedToWindowListener = viewAttachedToWindowListener;
        this.attachedToRecyclerViewListener = attachedToRecyclerViewListener;
        this.createdViewHolderListener = createdViewHolderListener;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (ObjectUtils.nonNull(attachedToRecyclerViewListener)){
            attachedToRecyclerViewListener.onAttachedToRecyclerView(contextProvider, this, recyclerView);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull VH holder) {
        super.onViewAttachedToWindow(holder);
        if (ObjectUtils.nonNull(viewAttachedToWindowListener)){
            viewAttachedToWindowListener.onViewAttachedToWindow(holder);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewFactory factory = itemViewFactories.getObserver(viewType);
        ObjectUtils.requireNonNull(factory, "itemViewFactory");
        VH holder = viewHolderFactory.createViewHolder(factory.createItemView(layoutInflater, parent), viewType);
        if (ObjectUtils.nonNull(createdViewHolderListener)){
            createdViewHolderListener.onCreatedViewHolder(holder);
        }
        return holder;
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

    @Override
    public int getItemViewType(int position) {
        return dataClassifier.getItemType(position, dataProvider.getData(position));
    }
}
```
&emsp;&emsp;哈哈，把事情分出去了，我终于轻松了也不用那么累了，只负责自己的事情，把自己能做的做到最好。现在看看整体的结构好像已经有血有肉了。  
&emsp;&emsp;真的是这样吗？现在好像还是没有达到我们的目标，易于扩展，现在只是整个RecyclerViewAdapter构造比较简单，我们还需要努力。

## 下节预告
### 1、插件的出现，终于可以不用在重复编写代码了
### 2、使得适配器内的属性在外部跟容易获取

#加油！！！努力的人最帅！
# link
[作者](https://github.com/j1046697411)  
[CommonlyAdapter系列文章](commonly_adapter.md)  