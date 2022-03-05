package com.example.allthecar.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allthecar.CommonApplication;
import com.example.allthecar.R;

public class SearchDetailActivity extends AppCompatActivity {

    private CommonApplication application;
    TextView search_parking, search_wash, search_repair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);

        application = (CommonApplication) CommonApplication.getInstance();

        createCustomToolbar();

        search_parking = (TextView) findViewById(R.id.search_parking);
        search_wash = (TextView) findViewById(R.id.search_wash_car);
        search_repair = (TextView) findViewById(R.id.search_repair_car);


        search_parking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), search_parking.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createCustomToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        String titleStr = application.getTitleWithPosition();
        title.setText(titleStr);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
