package org.jzl.android.commonlyadapterblogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jzl.android.library_no1.v0.CommonlyViewHolder;
import org.jzl.android.library_no1.v1.CommonlyAdapter;

import java.util.Arrays;
import java.util.List;

public class MainActivity_v1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> data = Arrays.asList("1", "2", "3", "4");
        CommonlyAdapter<String, CommonlyViewHolder> adapter = new CommonlyAdapter<>(this, data, new CommonlyAdapter.Callback<String, CommonlyViewHolder>() {
            @Override
            public CommonlyViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup parent, int viewType) {
                return new CommonlyViewHolder(layoutInflater.inflate(R.layout.item_test, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull CommonlyViewHolder holder, String data) {
                holder.tvTest.setText(data);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.rv_test);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}