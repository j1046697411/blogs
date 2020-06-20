# 第二步，分离回调，解决臃肿

## 前言
有些时候分离并不意味着结束，而往往是新的开始。
## 目标
### 1、实现简单复用的ViewHolder
### 2、分离Callback，解决回调臃肿问题
## 实现代码
先上代码，有🐎有真相

```java
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

    public interface DataBinder<T, VH extends RecyclerView.ViewHolder> {
        void bind(VH holder, T data);
    }

    @FunctionalInterface
    public interface ItemViewFactory {
        View createItemView(LayoutInflater layoutInflater, ViewGroup parent);
    }

    @FunctionalInterface
    public interface ViewHolderFactory<VH extends RecyclerView.ViewHolder> {
        VH createViewHolder(View itemView, int viewType);
    }

    public static class CommonlyViewHolder extends RecyclerView.ViewHolder {

        private SparseArray<View> views;

        public CommonlyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @SuppressWarnings("unchecked")
        public <V extends View> V findViewById(int id) {
            View view = views.get(id);
            if (ObjectUtils.isNull(view)) {
                view = itemView.findViewById(id);
                views.put(id, view);
            }
            return (V) view;
        }

    }
}
```
```java
public class MainActivity_v2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> data = Arrays.asList("1", "2", "3", "4");
        CommonlyAdapter<String, CommonlyAdapter.CommonlyViewHolder> adapter = new CommonlyAdapter<>(this, data, (holder, data1) -> {
            holder.<TextView>findViewById(R.id.tv_test).setText(data1);
        }, (layoutInflater, parent) -> {
            return layoutInflater.inflate(R.layout.item_test, parent, false);
        }, (itemView, viewType) -> new CommonlyAdapter.CommonlyViewHolder(itemView));
        RecyclerView recyclerView = findViewById(R.id.rv_test);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
```
&emsp;&emsp;埃，这代码怎么看着好像怎么看着好像变复杂了呢？？？不是说好的解决臃肿吗？  
&emsp;&emsp;相信会有不少的人刚看见这代码会有这样的疑问，其实如果我刚看见别人这样写代码我也会有这样的疑问，
其实不然，仔细观察会发现虽然调用的代码变多了，其实接口间的逻辑更清晰了，每个接口只干直接最需要干的事，
别人的事我不需要管，这不就好比我们项目中做好自己的分工才能更好的为团队做出贡献吗？  
&emsp;&emsp;这就是设计模式中的单一原则，这儿就不详细介绍了，喜欢的可以去看看[设计模式中的六大原则](https://www.jianshu.com/p/712221ff7ffe) 和 [设计模式](https://blog.csdn.net/A1342772/article/details/91349142)  
&emsp;&emsp;扯远了，我们在回到这次实现来说吧！为什么会拆分成三个接口，而不是两个或者更多呢？我们先来分析下`onCreateViewHolder`这个方法，
从方法来看其实这个方法就是用来创建ViewHolder，但是我们仔细观察会发现，这里面好像做的并不止这一件事情，还做了创建View这么一件事，根据单一原则，
所以我们拆分出了`ItemViewFactory`和`ViewHolderFactory`接口。
```java
@FunctionalInterface
public interface ItemViewFactory {
    View createItemView(LayoutInflater layoutInflater, ViewGroup parent);
}
```
```java
@FunctionalInterface
public interface ViewHolderFactory<VH extends RecyclerView.ViewHolder> {
    VH createViewHolder(View itemView, int viewType);
}
```
我们观察发现，`ItemViewFactory`返回的为什么会是View呢？在Android中我们不是一般适用的是LayoutId来表示View吗？而且从很多框架
来看也是这么做的，直接返回的是资源Id。如下：
```java
public interface LayoutFactory{
    int getLayoutId();
}
```
在这儿说说自己的想法吧！返回id不是不行，就是少了一种灵活性，如果我们在这儿只能获取到View那是不是就不能适配到我们这个框架来了呢！比如说
第三方的广告布局，是不是有可能我们就获取不到对应的资源id呢？返回给我们的就是一个View对象，那我们是不是就不能在这里面适用了，所以个人认为
返回一个View可能跟通用一点，这里只是说说自己的理解不代表什么！希望有大佬有更好的想法。  
```java
public static class CommonlyViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> views;

    public CommonlyViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V findViewById(int id) {
        View view = views.get(id);
        if (ObjectUtils.isNull(view)) {
            view = itemView.findViewById(id);
            views.put(id, view);
        }
        return (V) view;
    }

}
```
&emsp;&emsp;说完了拆分Callback的问题了，相信大家应该也看见`CommonlyViewHolder`实现了吧!大家能看出其实就是实现了一个View
的缓存，其实在Android中findViewById是很消耗资源的一个方法，他会去遍历所有的View找到自己需要的那个View，中了缓存过后就能减少
不少的查询时间，其实 [ButterKnife](https://github.com/JakeWharton/butterknife) 就是变相的使用这种减少查询的方式来优化
的，只不过他是通过生成代码，的方式缓存在对象的属性中，我是使用的`SparseArray`缓存在内存中，也算是一种小小的优化吧！  

#下节预告
### 1、使用链式编程和lambda语法使调用更优美
### 2、继续优化ViewHolder

#加油！！！努力的人最帅！
# link
[作者](https://github.com/j1046697411)  
[CommonlyAdapter系列文章](commonly_adapter.md)  