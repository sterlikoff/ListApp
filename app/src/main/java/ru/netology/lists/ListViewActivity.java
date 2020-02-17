package ru.netology.lists;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewActivity extends AppCompatActivity {

    BaseAdapter listContentAdapter;
    SwipeRefreshLayout swipeLayout;
    List<Map<String, String>> content = new ArrayList<>();
    ArrayList<Integer> deletedIds = new ArrayList<>();

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        deletedIds = savedInstanceState.getIntegerArrayList("deletedIds");

        if (deletedIds != null && deletedIds.size() > 0) {

            for (int i = 0; i < deletedIds.size(); i++) {

                content.remove(deletedIds.get(i).intValue());
                listContentAdapter.notifyDataSetChanged();

            }

        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putIntegerArrayList("deletedIds", deletedIds);
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView list = findViewById(R.id.list);

        prepareContent();
        listContentAdapter = createAdapter(content);

        list.setAdapter(listContentAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                content.remove(position);
                deletedIds.add(position);

                listContentAdapter.notifyDataSetChanged();

            }
        });

        swipeLayout = findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                prepareContent();
                listContentAdapter.notifyDataSetChanged();

                swipeLayout.setRefreshing(false);

            }
        });

    }

    @NonNull
    private BaseAdapter createAdapter(List<Map<String, String>> values) {
        return new SimpleAdapter(this, values, R.layout.simple_list_item, new String[]{"text", "length"}, new int[]{R.id.list_item_title, R.id.list_item_text});
    }

    @NonNull
    private void prepareContent() {

        content.clear();

        SharedPreferences pref = getSharedPreferences("MyPref", MODE_PRIVATE);
        String string = pref.getString("text", "");

        if (string.equals("")) {

            string = getString(R.string.large_text);
            SharedPreferences.Editor myEditor = pref.edit();
            myEditor.putString("text", string);
            myEditor.apply();

        }

        String[] strings = string.split("\n\n");

        for (int i = 0; i < strings.length; ) {

            Map<String, String> row = new HashMap<>();
            row.put("text", strings[i]);
            row.put("length", Integer.toString(strings[i].length()));

            content.add(row);

            i++;

        }

    }

}
