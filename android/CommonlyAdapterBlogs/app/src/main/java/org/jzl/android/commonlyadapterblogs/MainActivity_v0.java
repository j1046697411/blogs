package org.jzl.android.commonlyadapterblogs;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jzl.android.library_no1.v0.CommonlyAdapter;

import java.util.Arrays;
import java.util.List;

public class MainActivity_v0 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> data = Arrays.asList("1", "2", "3", "4");
        CommonlyAdapter adapter = new CommonlyAdapter(data, this, R.layout.item_test);
        RecyclerView recyclerView = findViewById(R.id.rv_test);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}