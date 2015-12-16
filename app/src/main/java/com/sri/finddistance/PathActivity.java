package com.sri.finddistance;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sri.finddistance.adapter.PathListAdapter;
import com.sri.finddistance.gson.Directions;
import com.sri.finddistance.utils.L;

public class PathActivity extends ActionBarActivity {

    private Directions directionsObj;

    private RecyclerView pathList;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);
        initUI();
        initComponents();
    }

    private void initUI() {
        pathList = (RecyclerView) findViewById(R.id.pathList);
        pathList.setHasFixedSize(true);
    }

    private void initComponents() {
        if (getIntent().hasExtra("DIRECTION")) {
            directionsObj = (Directions) getIntent().getSerializableExtra("DIRECTION");
            L.d("status: " + directionsObj.status);
            populatePath();
        } else {
            L.e("DIRECTION object not found in the intent data");
            finish();
        }
    }

    private void populatePath() {
        layoutManager = new LinearLayoutManager(this);
        pathList.setLayoutManager(layoutManager);

        myAdapter = new PathListAdapter(directionsObj.routes.get(0).legs.get(0).steps);
        pathList.setAdapter(myAdapter);
    }

}
