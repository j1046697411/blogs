package org.jzl.android.library_no1.vh;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jzl.android.ViewBinder;
import org.jzl.android.ViewFinder;
import org.jzl.android.provider.Provider;

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
