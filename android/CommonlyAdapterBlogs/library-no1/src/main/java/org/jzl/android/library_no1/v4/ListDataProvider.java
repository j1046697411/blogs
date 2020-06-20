package org.jzl.android.library_no1.v4;

import androidx.recyclerview.widget.RecyclerView;

import org.jzl.android.provider.Provider;
import org.jzl.lang.fun.IntConsumer;
import org.jzl.lang.util.ArrayUtils;
import org.jzl.lang.util.CollectionUtils;
import org.jzl.lang.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
