package com.mayank7319gmail.fuelbuddy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    AdView mAdView;
    RecyclerView recyclerFuel;
    TextView tvLocation, tvDate,tvState, tvMessage;
    PriceApiClient priceApiClient;
    ProgressBar progressBar;
    ArrayList<PriceItem> fuelList;

    SharedPreferences sharedPref;
    String state, date;

    public static final String TAG = "Fuel Buddy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvState = (TextView) findViewById(R.id.tvState);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        recyclerFuel = (RecyclerView) findViewById(R.id.recyclerFuel);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        MobileAds.initialize(this, "ca-app-pub-5628554689532375/7942444847");

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Calendar cal = Calendar.getInstance();
        date = cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);
        tvDate.setText(date);

        recyclerFuel.setLayoutManager(new LinearLayoutManager(this));

        sharedPref = getPreferences(Context.MODE_PRIVATE);

        if(savedInstanceState == null)
        checkFirstRun();
        else {
            fuelList = savedInstanceState.getParcelableArrayList("fuelList");
            state = savedInstanceState.getString("state");
            tvState.setText(state);

            if(fuelList == null)
                checkFirstRun();
            else setListPrice(fuelList);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("fuelList",fuelList);
        outState.putString("state",state);
        super.onSaveInstanceState(outState);
    }

    void checkFirstRun(){
        if(sharedPref.contains("state")){
            state = sharedPref.getString("state","Delhi");

            checkDbState();
        }
        else {
            //First Run of app
            createDisclaimerDialog();
        }
    }

    void checkDbState(){
        DbItem item = DbUtils.readFromDB(this,state);

        if(item != null && item.date.equals(date)){
//            Log.d(TAG, "checkFirstRun: using db");
            progressBar.setVisibility(View.VISIBLE);
            tvMessage.setVisibility(View.VISIBLE);
            recyclerFuel.setVisibility(View.INVISIBLE);

            tvState.setText(state);

            setListPrice(item.fuelList);
        }
        else
            setPrice();
    }

    void createDisclaimerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Disclaimer")
                .setMessage(R.string.disclaimer)
                .setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createStatePickerDialog();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    void createStatePickerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose state")
                .setItems(Utils.stateList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        state = Utils.stateList[which];
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("state",state);
                        editor.apply();
                        checkDbState();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    void setPrice(){
        progressBar.setVisibility(View.VISIBLE);
        tvMessage.setVisibility(View.VISIBLE);
        recyclerFuel.setVisibility(View.INVISIBLE);
        if(priceApiClient != null)
            priceApiClient.clearAllRequests();
        tvState.setText(state);
        priceApiClient = new PriceApiClient(this); //Fetches data from API
        priceApiClient.getFuelPriceList(state);
    }

    public void setListPrice(ArrayList<PriceItem> fuelList){
        this.fuelList = fuelList;
        PriceRecycler priceRecycler = new PriceRecycler(fuelList,this);

        recyclerFuel.setAdapter(priceRecycler);
        recyclerFuel.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        if(priceApiClient != null){
            priceApiClient.clearAllRequests();
        }
        super.onStop();
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

        switch (id){
            case R.id.action_rate:
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mayank7319gmail.fuelbuddy"));
                startActivity(i);
//                Toast.makeText(this,"This feature has not been implemented yet.",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_state:
                createStatePickerDialog();
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
