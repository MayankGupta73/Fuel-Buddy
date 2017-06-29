package com.mayank7319gmail.fuelbuddy;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    AdView mAdView;
    RecyclerView recyclerHp, recyclerIoc;
    TextView tvLocation, tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvDate = (TextView) findViewById(R.id.tvDate);
        recyclerHp = (RecyclerView) findViewById(R.id.recyclerHp);
        recyclerIoc = (RecyclerView) findViewById(R.id.recyclerIoc);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
        //TODO: Replace the Ad Unit ID for correct Id also in layout

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //TODO: Fetch data from API
        ArrayList<PriceItem> priceItemList = new ArrayList<>();
        priceItemList.add(new PriceItem("New Delhi", 66.66, 66.66));
        priceItemList.add(new PriceItem("Mumbai", 12.34, 45.67));
        priceItemList.add(new PriceItem("Kolkata", 56.56, 78.02));
        priceItemList.add(new PriceItem("Chennai", 68.78, 69.69));

        recyclerHp.setLayoutManager(new LinearLayoutManager(this));
        recyclerIoc.setLayoutManager(new LinearLayoutManager(this));

        PriceRecycler priceRecycler = new PriceRecycler(priceItemList,this);
        recyclerHp.setAdapter(priceRecycler);
        recyclerIoc.setAdapter(priceRecycler);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
