package org.jzl.android.library_no1.v1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
