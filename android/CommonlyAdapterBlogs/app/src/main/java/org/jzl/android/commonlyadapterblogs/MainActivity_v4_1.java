package org.jzl.android.commonlyadapterblogs;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jzl.android.library_no1.v4.CommonlyAdapter1;
import org.jzl.android.library_no1.v4.ListDataProvider;
import org.jzl.android.library_no1.vh.CommonlyViewHolder;
import org.jzl.android.provider.ContextProvider;

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