package org.jzl.android.commonlyadapterblogs;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jzl.android.library_no1.v2.CommonlyViewHolder;
import org.jzl.android.library_no1.v2.CommonlyAdapter;

import java.util.Arrays;
import java.util.List;

public class MainActivity_v2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> data = Arrays.asList("1", "2", "3", "4");
        CommonlyAdapter<String, CommonlyViewHolder> adapter = new CommonlyAdapter<>(this, data, (holder, data1) -> {
            holder.<TextView>findViewById(R.id.tv_test).setText(data1);
        }, (layoutInflater, parent) -> {
            return layoutInflater.inflate(R.layout.item_test, parent, false);
        }, (itemView, viewType) -> new CommonlyViewHolder(itemView));
        RecyclerView recyclerView = findViewById(R.id.rv_test);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}