package org.jzl.android.library_no1.v2;

import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jzl.lang.util.ObjectUtils;

public class CommonlyViewHolder extends RecyclerView.ViewHolder {

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
