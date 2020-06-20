package org.jzl.android.commonlyadapterblogs;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jzl.android.library_no1.v3.CommonlyAdapter;
import org.jzl.android.library_no1.vh.CommonlyViewHolder;

import java.util.Arrays;
import java.util.List;

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