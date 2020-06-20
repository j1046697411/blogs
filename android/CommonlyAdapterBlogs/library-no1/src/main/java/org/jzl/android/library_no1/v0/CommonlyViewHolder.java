package org.jzl.android.library_no1.v0;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jzl.android.library_no1.R;

public class CommonlyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTest;

        public CommonlyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvTest = itemView.findViewById(R.id.tv_test);
        }
    }