# 第一步，优化代码结构

## 前言
好多时候代码都被写死了，有些时候简单的一点改动就能让代码活过来。
## 目标
### 1、简单的实现部分代码复用
### 2、减少部分代码的复杂性
### 3、实现一般通用
## 实现过程

#### 1、优化前代码
```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> data = Arrays.asList("1", "2", "3", "4");
        CommonlyAdapter adapter = new CommonlyAdapter(data, this, R.layout.item_test);
        RecyclerView recyclerView = findViewById(R.id.rv_test);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    public class CommonlyAdapter extends RecyclerView.Adapter<CommonlyViewHolder> {

        private List<String> data;
        private Context context;
        private LayoutInflater layoutInflater;
        private int layoutId;

        public CommonlyAdapter(List<String> data, Context context, int layoutId) {
            this.data = data;
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
            this.layoutId = layoutId;
        }

        @NonNull
        @Override
        public CommonlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CommonlyViewHolder(layoutInflater.inflate(layoutId, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CommonlyViewHolder holder, int position) {
            holder.tvTest.setText(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public class CommonlyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTest;

        public CommonlyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvTest = itemView.findViewById(R.id.tv_test);
        }
    }
}
```
&emsp;&emsp;这应该就是最原始的RecyclerView的实现方式了吧，这种方式相信大家也能看出只有这儿能够适用吧，换个地方就不能在用了。  
&emsp;&emsp;这是我们身为一个偷懒的程序员应该写出来的代码吗？  
&emsp;&emsp;我相信绝大多数人应该都不希望看见这样的代码吧！所以我们现在进行第一次的代码优化,在之前的基础上加上java的泛型和Android中常用的Callback来优化代码是的代码的复用性得以提升。至少不至于只能在一个地方使用，下面我们就开始优化吧！

#### 2、优化后代码
```java
public class MainActivity_v1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> data = Arrays.asList("1", "2", "3", "4");
        CommonlyAdapter<String, CommonlyViewHolder> adapter = new CommonlyAdapter<>(this, data, new CommonlyAdapter.Callback<String, CommonlyViewHolder>() {
            @Override
            public CommonlyViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup parent, int viewType) {
                return new CommonlyViewHolder(layoutInflater.inflate(R.layout.item_test, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull CommonlyViewHolder holder, String data) {
                holder.tvTest.setText(data);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.rv_test);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
```
```java
public class CommonlyAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<T> data;
    private LayoutInflater layoutInflater;
    private Callback<T, VH> callback;

    public CommonlyAdapter(Context context, List<T> data, Callback<T, VH> callback) {
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
        this.callback = callback;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return callback.onCreateViewHolder(layoutInflater, parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        callback.onBindViewHolder(holder, data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface Callback<T, VH extends RecyclerView.ViewHolder> {

        VH onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup parent, int viewType);

        void onBindViewHolder(@NonNull VH holder, T data);
    }
}

```
优化后的代码是不是看着要比最初的要好的多了呢！但是这还远远不是我们这种偷懒程序员想要的代码，在其他地方写还需要去实现不同的Callback。
而且ViewHolder每次都还需要重复去去写。太麻烦了，一点都不符合我们偷懒的性格。
#下节预告
### 1、实现简单复用的ViewHolder
### 2、分离Callback，解决回调臃肿问题

#加油！！！努力的人最帅！

# link
[作者](https://github.com/j1046697411)  
[CommonlyAdapter系列文章](commonly_adapter.md)  