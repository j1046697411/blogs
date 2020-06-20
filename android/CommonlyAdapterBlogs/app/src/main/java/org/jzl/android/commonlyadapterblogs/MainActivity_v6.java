package org.jzl.android.commonlyadapterblogs;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jzl.android.library_no1.v4.DataProvider;
import org.jzl.android.library_no1.v6.Configurator;
import org.jzl.android.library_no1.vh.CommonlyViewHolder;

public class MainActivity_v6 extends AppCompatActivity {

    private DataProvider<String> dataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Configurator.<String>of()
                .itemTypes((position, data) -> position % 2)
                .dataBinds((holder, data) -> holder.provide().setText(R.id.tv_test, data))
                .createItemViews((layoutInflater, parent) -> layoutInflater.inflate(R.layout.item_test, parent, false), 0)
                .createItemViews((layoutInflater, parent) -> layoutInflater.inflate(R.layout.item_test_3, parent, false), 1)
                .bindRecyclerView(target -> target.setLayoutManager(new LinearLayoutManager(this)))
                .bindDataProvider(target -> this.dataProvider = target)
                .plugin(new DataPlugin())
                .bind(findViewById(R.id.rv_test));
    }

    public static class DataPlugin implements Configurator.Plugin<String, CommonlyViewHolder> {

        @Override
        public void setup(Configurator<String, CommonlyViewHolder> configurator, int... viewTypes) {
            configurator.data("1", "2", "3", "4");
        }
    }

}