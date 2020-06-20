package org.jzl.android.library_no1.v0;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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