# 第四步，适配多类型的ItemViewType
## 前言
做自己该做的事，才能把事情做更好
## 目标
### 1、适配多类型的ItemViewType
### 2、优化数据更新
## 实现代码
先上代码，有🐎有真相
```java
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
```
```java
public class MainActivity_v4_1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CommonlyAdapter1.<String, CommonlyViewHolder>of(ContextProvider.of(this), ListDataProvider.of("1", "2", "3", "4"))
                .setDataClassifier((position, data) -> position % 2)
                .setItemViewFactory((layoutInflater, parent, viewType) -> {
                    return layoutInflater.inflate(viewType == 1 ? R.layout.item_test : R.layout.item_test_2, parent, false);
                })
                .setViewHolderFactory((itemView, viewType) -> new CommonlyViewHolder(itemView))
                .setDataBinder((holder, data1) -> {
                    if (holder.getItemViewType() == 1) {
                        holder.provide().setText(R.id.tv_test, data1);
                    } else {
                        holder.provide().setText(R.id.tv_test_2, data1);
                    }
                })
                .bind(findViewById(R.id.rv_test), new LinearLayoutManager(this));
    }

}
```

第一版代码，写好了，看看好像哪里不对，不是说好了一个人只干自己的事吗？怎么还做起了类型判断的生意了呢！
这种事不能忍，所以必须改，那该怎么改呢？？？想了下，好像每个类型的ViewType都会对应一个`temViewFactory`
和 `DataBinder`,嗯，那好办我们是不是可以使用`SparseArray`来存对应的`temViewFactory`和 `DataBinder`
可以，就这么干。

```java
public class MainActivity_v4_2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CommonlyAdapter2.<String, CommonlyViewHolder>of(ContextProvider.of(this), ListDataProvider.of("1", "2", "3", "4"))
                .setDataClassifier((position, data) -> position % 2)
                .addItemViewFactory((layoutInflater, parent) -> layoutInflater.inflate(R.layout.item_test, parent, false), 0)
                .addItemViewFactory((layoutInflater, parent) -> layoutInflater.inflate(R.layout.item_test_2, parent, false), 1)
                .setViewHolderFactory((itemView, viewType) -> new CommonlyViewHolder(itemView))
                .addDataBinder((holder, data) -> holder.provide().setText(R.id.tv_test, data), 0)
                .addDataBinder((holder, data) -> holder.provide().setText(R.id.tv_test_2, data), 1)
                .bind(findViewById(R.id.rv_test), new LinearLayoutManager(this));
    }

}
```
```java
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
```
```java
public class Observable<O> {

    private SparseArray<O> observers = new SparseArray<>();

    public void register(int viewType, O observer) {
        if (ObjectUtils.nonNull(observer)) {
            observers.put(viewType, observer);
        }
    }

    public O getObserver(int viewType) {
        return observers.get(viewType);
    }

}
```
&emsp;&emsp;第二版也实现了，这个看上去好像比第一版的代码是要优美很多，每个方法都只干了自己想干的事，实现了单一原则。
但是仔细想想好像还是不太对劲，说好的单一原则，怎么觉得CommonlyAdapter2还是承受了构成的功能呢，这不是我改管的事啊！
我只需要管好我自己的任务就行了。

可能也有人发现了，怎么多了一个`DataProvider`，这个事做什么的呢？，下面就一起来看看源码吧！
```java
public interface DataProvider<T> extends Provider<List<T>>, ObjectBinder<RecyclerView.Adapter<?>> {

    @Override
    void bind(RecyclerView.Adapter<?> adapter);


    int getDataCount();

    T getData(int position);

    boolean isEmpty();

    DataProvider<T> add(T data);

    DataProvider<T> add(int index, T data);

    DataProvider<T> addAll(Collection<T> collection);

    DataProvider<T> addAll(T... data);

    DataProvider<T> addAll(int index, Collection<T> collection);

    DataProvider<T> addDataProvider(DataProvider<T> dataProvider);

    DataProvider<T> remove(T data);

    DataProvider<T> remove(int index);

    DataProvider<T> clear();

    DataProvider<T> swap(int position, int targetPosition);

    DataProvider<T> move(int position, int targetPosition);

    DataProvider<T> each(IntConsumer<T> consumer);
}
```

```java
public class ListDataProvider<T> implements DataProvider<T>, Provider<List<T>> {

    private RecyclerView.Adapter<?> adapter;
    private List<T> data;

    private ListDataProvider(List<T> data) {
        this.data = data;
    }

    @Override
    public void bind(RecyclerView.Adapter<?> adapter) {
        this.adapter = adapter;
    }


    @Override
    public int getDataCount() {
        return data.size();
    }

    @Override
    public T getData(int position) {
        return data.get(position);
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public DataProvider<T> add(T data) {
        this.data.add(data);
        if (ObjectUtils.nonNull(adapter)) {
            adapter.notifyItemChanged(this.data.size() - 1);
        }
        return this;
    }

    @Override
    public DataProvider<T> add(int index, T data) {
        if (isInRange(index)) {
            this.data.add(index, data);
            if (ObjectUtils.nonNull(adapter)) {
                adapter.notifyItemInserted(index);
            }
        } else {
            add(data);
        }
        return this;
    }

    @Override
    public DataProvider<T> addAll(Collection<T> collection) {
        if (CollectionUtils.nonEmpty(collection)) {
            int startPosition = data.size() - 1;
            this.data.addAll(collection);
            if (ObjectUtils.nonNull(adapter)) {
                adapter.notifyItemRangeChanged(startPosition, collection.size());
            }
        }
        return this;
    }

    @SafeVarargs
    @Override
    public final DataProvider<T> addAll(T... data) {
        if (ArrayUtils.nonEmpty(data)) {
            int startPosition = this.data.size() - 1;
            Collections.addAll(this.data, data);
            if (ObjectUtils.nonNull(adapter)) {
                adapter.notifyItemRangeChanged(startPosition, data.length);
            }
        }
        return this;
    }

    @Override
    public DataProvider<T> addAll(int index, Collection<T> collection) {
        if (isInRange(index)) {
            this.data.addAll(index, collection);
            if (ObjectUtils.nonNull(adapter)) {

                this.adapter.notifyItemRangeInserted(index, collection.size());
            }
        } else {
            addAll(collection);
        }
        return this;
    }

    @Override
    public DataProvider<T> addDataProvider(DataProvider<T> dataProvider) {
        return addAll(dataProvider.provide());
    }

    @Override
    public DataProvider<T> remove(T data) {
        if (ObjectUtils.nonNull(data)) {
            return remove(this.data.indexOf(data));
        }
        return this;
    }

    @Override
    public DataProvider<T> remove(int index) {
        if (isInRange(index)) {
            this.data.remove(index);
            this.adapter.notifyItemRemoved(index);
        }
        return this;
    }

    @Override
    public DataProvider<T> clear() {
        this.data.clear();
        onClear();
        if (ObjectUtils.nonNull(adapter)) {
            this.adapter.notifyDataSetChanged();
        }
        return this;
    }

    private void onClear() {
    }

    @Override
    public DataProvider<T> swap(int position, int targetPosition) {
        if (isInRange(position) && isInRange(targetPosition)) {
            Collections.swap(this.data, position, targetPosition);
            if (ObjectUtils.nonNull(adapter)) {
                adapter.notifyItemChanged(position);
                adapter.notifyItemChanged(targetPosition);
            }
        }
        return this;
    }

    @Override
    public DataProvider<T> move(int position, int targetPosition) {
        if (isInRange(position) && isInRange(targetPosition)) {
            CollectionUtils.move(data, position, targetPosition);
            if (ObjectUtils.nonNull(adapter)) {
                adapter.notifyItemMoved(position, targetPosition);
            }
        }
        return this;
    }

    @Override
    public DataProvider<T> each(IntConsumer<T> consumer) {
        ObjectUtils.requireNonNull(consumer, "consumer");
        CollectionUtils.each(data, consumer);
        if (ObjectUtils.nonNull(adapter)) {
            adapter.notifyDataSetChanged();
        }
        return this;
    }

    private boolean isInRange(int position) {
        return position >= 0 && position < data.size();
    }

    public static <T> ListDataProvider<T> of(Collection<T> data) {
        return new ListDataProvider<>(CollectionUtils.toArrayList(data));
    }

    @SafeVarargs
    public static <T> ListDataProvider<T> of(T... data) {
        return of(Arrays.asList(data));
    }

    @Override
    public List<T> provide() {
        return Collections.unmodifiableList(data);
    }
}

```
可以看出，主要就做了数据操作和通知UI更新两件事

## 下节预告
### 1、我本就很累，为什么还需要做这么多事？ Configurator 的到来，为我分担任务
### 2、添加更多的事件回调，让我们掌握全局。

# 加油！！！努力的人最帅！
# link
[作者](https://github.com/j1046697411)  
[CommonlyAdapter系列文章](commonly_adapter.md)  