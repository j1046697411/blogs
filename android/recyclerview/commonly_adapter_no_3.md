# 第三步，使用链式编程和lambda语法使调用更优美

## 前言
好多时候代码都被写死了，有些时候简单的一点改动就能让代码活过来。
## 目标
## 实现代码
先上代码，有🐎有真相
```java
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
```
```java
public class CommonlyViewHolder extends RecyclerView.ViewHolder implements ViewFinder, Provider<ViewBinder> {

    private ViewBinder viewBinder;

    public CommonlyViewHolder(@NonNull View itemView) {
        super(itemView);
        this.viewBinder = ViewBinder.bind(itemView);
    }

    @Override
    public <V extends View> V findViewById(int id) {
        return viewBinder.findViewById(id);
    }

    @Override
    public ViewBinder provide() {
        return viewBinder;
    }
}
```
```java
public class MainActivity_v3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        List<String> data = Arrays.asList("1", "2", "3", "4");
        CommonlyAdapter.<String, CommonlyViewHolder>of(this, data)
                .setItemViewFactory((layoutInflater, parent) -> layoutInflater.inflate(R.layout.item_test, parent, false))
                .setViewHolderFactory((itemView, viewType) -> new CommonlyViewHolder(itemView))
                .setDataBinder((holder, data1) -> holder.provide().setText(R.id.tv_test, data1))
                .bind(findViewById(R.id.rv_test), new LinearLayoutManager(this));
    }

}
```
&emsp;&emsp;看着是不是很像我们最后的版本了，哈哈，其实还差的挺远的，不过和最初的版本已经有了很大的改变了，已经能适用更多的地方了而且还能通过重写一些接口
实现部分代码重用，而且已经有了链式编程和java的lambda语法的雏形了。
调用起来也比较优美了，但是其实还是有许多不足的，我们现在就来慢慢分析下吧！  
##### 1、首先我们这个适配器还只能适配一种类型的ViewType的远远没有达到我们通用的要求。跟别说适用所用的地方了。
##### 2、有些回调的点我们还无法获取，比如在ViewHolder创建完成的时候，我们要怎么知道呢？在ViewHolder绑定上Window的时候我们也不清楚等等...
##### 3、现在的数据操作和更新UI都需要外面手动处理
好像越说路越长的样子，加油

&emsp;&emsp;上面说了整体的优点和欠缺的地方，我们争取在下一篇中补齐吧，这儿我们来说说这节中CommonlyViewHolder又有哪些优化吧！
从代码来看好像只是多了一个ViewBinder对象，其实这个是我开源的一个库中的其中一个简化操作的一个类喜欢的可以关注一下，还在继续更新。
```
implementation 'org.jzl.android:android-commons:0.0.1'
```
源码
```java
public class ViewBinder implements ViewFinder {

    private ViewFinder finder;

    private ViewBinder(ViewFinder finder) {
        this.finder = finder;
    }

    public ViewBinder setText(@IdRes int id, @StringRes int stringId) {
        this.<TextView>findViewById(id).setText(stringId);
        return this;
    }

    public ViewBinder setText(@IdRes int id, CharSequence text) {
        this.<TextView>findViewById(id).setText(text);
        return this;
    }

    public ViewBinder setTextColor(@IdRes int id, @ColorInt int color) {
        this.<TextView>findViewById(id).setTextColor(color);
        return this;
    }

    public ViewBinder setBackground(@IdRes int id, Drawable background) {
        this.findViewById(id).setBackground(background);
        return this;
    }

    public ViewBinder setBackground(@IdRes int id, @DrawableRes int backgroundId) {
        findViewById(id).setBackgroundResource(backgroundId);
        return this;
    }

    public ViewBinder setBackgroundColor(@IdRes int id, @ColorInt int colorId) {
        findViewById(id).setBackgroundColor(colorId);
        return this;
    }

    public ViewBinder setImageResource(@IdRes int id, @DrawableRes int resId) {
        this.<ImageView>findViewById(id).setImageResource(resId);
        return this;
    }

    public ViewBinder setImageBitmap(@IdRes int id, Bitmap bitmap) {
        this.<ImageView>findViewById(id).setImageBitmap(bitmap);
        return this;
    }

    public ViewBinder setImageDrawable(@IdRes int id, Drawable drawable) {
        this.<ImageView>findViewById(id).setImageDrawable(drawable);
        return this;
    }

    public ViewBinder setVisibility(@IdRes int id, int visibility) {
        findViewById(id).setVisibility(visibility);
        return this;
    }

    public ViewBinder setChecked(@IdRes int id, boolean checked) {
        this.<CompoundButton>findViewById(id).setChecked(checked);
        return this;
    }

    public ViewBinder bindClickListener(@IdRes int id, View.OnClickListener listener) {
        findViewById(id).setOnClickListener(listener);
        return this;
    }

    public ViewBinder bindCheckedChangeListener(@IdRes int id, CompoundButton.OnCheckedChangeListener listener) {
        this.<CompoundButton>findViewById(id).setOnCheckedChangeListener(listener);
        return this;
    }

    @Override
    public <V extends View> V findViewById(@IdRes int id) {
        return finder.findViewById(id);
    }

    public static ViewBinder bind(ViewFinder viewFinder) {
        return new ViewBinder(ViewFinders.cacheViewFinder(viewFinder));
    }

    public static ViewBinder bind(View view) {
        return new ViewBinder(ViewFinders.cacheViewFinder(view));
    }

    public static ViewBinder bind(Activity activity) {
        return new ViewBinder(ViewFinders.cacheViewFinder(activity));
    }

    public static ViewBinder bind(Dialog dialog) {
        return new ViewBinder(ViewFinders.cacheViewFinder(dialog));
    }
}

```

##下节预告

### 1、适配多类型的ItemViewType
### 2、优化数据更新


# 加油！！！努力的人最帅！

## link
[作者](https://github.com/j1046697411)  
[CommonlyAdapter系列文章](commonly_adapter.md)  