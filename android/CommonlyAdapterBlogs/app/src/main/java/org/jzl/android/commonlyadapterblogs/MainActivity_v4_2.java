package org.jzl.android.commonlyadapterblogs;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jzl.android.library_no1.v4.CommonlyAdapter2;
import org.jzl.android.library_no1.v4.ListDataProvider;
import org.jzl.android.library_no1.vh.CommonlyViewHolder;
import org.jzl.android.provider.ContextProvider;

public class MainActivity_v4_2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CommonlyAdapter2.<String, CommonlyViewHolder>of(ContextProvider.of(this), ListDataProvider.of("1", "2", "3", "4"))
                .setDataClassifier((position, data) -> position % 2)
                .addItemViewFactory((layoutInflater, parent) -> layoutInflater.inflate(R.layout.item_test, parent, false), 0)
                .addItemViewFactory((layoutInflater, parent) -> layoutInflater.inflate(R.layout.item_test_2, parent, false), 1)
                .setViewHolderFactory((itemView, viewType) -> new CommonlyViewHolder(itemView))
                .addDataBinder((holder, data) -> holder.provide().setText(R.id.tv_test, data), 0)
                .addDataBinder((holder, data) -> holder.provide().setText(R.id.tv_test_2, data), 1)
                .bind(findViewById(R.id.rv_test), new LinearLayoutManager(this));
    }

}