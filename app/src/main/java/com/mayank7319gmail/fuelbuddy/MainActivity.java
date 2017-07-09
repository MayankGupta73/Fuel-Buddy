package com.mayank7319gmail.fuelbuddy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    AdView mAdView;
    RecyclerView recyclerFuel;
    TextView tvLocation, tvDate,tvState;
    PriceApiClient priceApiClient;
    ProgressBar progressBar;

    SharedPreferences sharedPref;
    String state;

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
        recyclerFuel = (RecyclerView) findViewById(R.id.recyclerFuel);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
        //TODO: Replace the Ad Unit ID for correct Id also in layout

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Calendar cal = Calendar.getInstance();
        tvDate.setText(cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR));

        tvState.setText(state);
        recyclerFuel.setLayoutManager(new LinearLayoutManager(this));

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        checkFirstRun();

    }

    void checkFirstRun(){
        if(sharedPref.contains("state")){
            state = sharedPref.getString("state","Delhi");
            setPrice();
        }
        else {
            //First Run of app
            createDisclaimerDialog();
        }
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
                        setPrice();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    void setPrice(){
        progressBar.setVisibility(View.VISIBLE);
        recyclerFuel.setVisibility(View.INVISIBLE);
        if(priceApiClient != null)
            priceApiClient.clearAllRequests();
        tvState.setText(state);
        priceApiClient = new PriceApiClient(this); //Fetches data from API
        priceApiClient.getFuelPriceList(state);
    }

    public void setListPrice(ArrayList<PriceItem> fuelList){
        Log.d("Fuel Buddy", "setListPrice: list size "+fuelList.size());

        for(PriceItem item :fuelList)
        Log.d(TAG, "createNewList: price in "+item.getLocation()+" is "+item.getPetrolPrice()+" "+item.getDieselPrice());

        PriceRecycler priceRecycler = new PriceRecycler(fuelList,this);

        recyclerFuel.setAdapter(priceRecycler);
        recyclerFuel.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
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
                Toast.makeText(this,"This feature has not been implemented yet.",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_state:
                createStatePickerDialog();
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
