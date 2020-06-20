package org.jzl.android.commonlyadapterblogs;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jzl.android.library_no1.v5.Configurator;

public class MainActivity_v5 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Configurator.<String>of()
                .data("1", "2", "3", "4")
                .itemTypes((position, data) -> position % 2)
                .dataBinds((holder, data) -> holder.provide().setText(R.id.tv_test, data), 0, 1)
                .createItemViews((layoutInflater, parent) -> layoutInflater.inflate(R.layout.item_test, parent, false), 0)
                .createItemViews((layoutInflater, parent) -> layoutInflater.inflate(R.layout.item_test_3, parent, false), 1)
                .bind(findViewById(R.id.rv_test), new LinearLayoutManager(this));
    }

}