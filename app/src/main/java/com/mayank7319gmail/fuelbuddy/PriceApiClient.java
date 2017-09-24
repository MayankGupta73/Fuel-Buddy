package com.mayank7319gmail.fuelbuddy;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Mayank Gupta on 29-06-2017.
 */

public class PriceApiClient {

    private String urlPetrol = "https://www.mypetrolprice.com/petrol-price-in-india.aspx";
    //private String urlDiesel = "http://www.mypetrolprice.com/diesel-price-in-india.aspx";
    private RequestQueue reqQueue;
    private ArrayList<PriceItem> fuelPriceList ;
    //private ArrayList<SubPriceItem> subPetrolList, subDieselList;
    private ArrayList<Double> petrolList,dieselList;
    private LinkExtractor extractor;

    private String state,date;
    private Context ctx;

    private int arraySize=0 ,count =0 ;

    public static final String TAG = "Fuel Buddy";

     PriceApiClient(Context ctx) {
        reqQueue = Volley.newRequestQueue(ctx);
        this.ctx = ctx;
        petrolList = new ArrayList<>();
        dieselList = new ArrayList<>();

        fuelPriceList = new ArrayList<>();
    }


     void getFuelPriceList(final String stateCode){
        /*LinkExtractor myTask = new LinkExtractor();
        myTask.execute(stateCode);*/

        /*subPetrolList = new ArrayList<>();
        subDieselList = new ArrayList<>();*/

        state = stateCode;
         Calendar cal = Calendar.getInstance();
         date = cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR);
        StringRequest request = getRequest(urlPetrol,stateCode);

        reqQueue.add(request);
    }

     private StringRequest getRequest(final String url, final String stateCode){
         Log.d(TAG, "getRequest: url "+url);
        final StringRequest stringReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        extractor = new LinkExtractor();
                        extractor.execute(stateCode,url);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx,"Unable to fetch data. Please try again later.",Toast.LENGTH_SHORT).show();
                        /*Log.d(TAG, "onErrorResponse: cant access site "+error);
                        error.printStackTrace();*/
                    }
                });


        stringReq.setTag(TAG);
        return stringReq;
    }

    private class LinkExtractor extends AsyncTask<String,String,Void>{


        @Override
        protected Void doInBackground(String... params) {
            try {
                Document doc = Jsoup.connect(params[1]).timeout(20000).get();
                Elements temp = doc.select("h3.h3State:contains("+params[0].trim()+") + div ").select("div");

                for(int i =0; i<temp.size(); i++){
                    Element x = temp.get(i);
                    Elements data = x.select("a");
                    for (Element dataItem: data){
                        String name = dataItem.text();
                        String petrolLink = dataItem.absUrl("href");
                        String cityName = getCityName(name);


                        String dieselLink = petrolLink.replaceAll("Petrol","Diesel");

                        if(checkName(fuelPriceList,cityName)){
                            fuelPriceList.add(new PriceItem(cityName,0.0,0.0));

                            publishProgress(cityName,petrolLink,"p"); //Access petrol price
                            publishProgress(cityName,dieselLink,"d"); //Access diesel price
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ctx,"Unable to fetch data. Please try again later.",Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "doInBackground: failure in parsing "+e.toString());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            PriceScraper scraper = new PriceScraper();
            scraper.execute(values[0],values[1],values[2]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            arraySize = fuelPriceList.size();

        }
    }


    private class PriceScraper extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
             String reqUrl = params[1] , type = params[2], cityName = params[0];

            try {
                reqUrl = reqUrl.replace(" ","%20");
                Document doc = Jsoup.connect(reqUrl).timeout(20000).get();
                Element data = doc.getElementById("CPH1_lblCurrent");

                String temp = data.text();
                int index = ("Current "+cityName+" Petrol Price = ").length();
                String extract = temp.substring(index,index+5).trim();
                Double price;
                if(extract.contains("R")) {
                     price = Double.valueOf(extract.substring(0, 2));
                }
                else  price = Double.valueOf(extract);


                if(type.equals("p")){

                    int x =petrolList.size();
                    if(cityName.equals(fuelPriceList.get(x).getLocation()))
                    petrolList.add(price);
                }
                else if(type.equals("d")){

                    int x =dieselList.size();
                    if(cityName.equals(fuelPriceList.get(x).getLocation()))
                    dieselList.add(price);
                }

            } catch (IOException e) {
                e.printStackTrace();
                if(type.equals("d")){
                    fuelPriceList.remove(petrolList.size());
                    count -=2;
                    arraySize--;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(count < 2*arraySize-1) {
                count++;
            }else {

                createNewList();
            }
        }
    }

    private boolean checkName(ArrayList<PriceItem> list, String name){
        for(PriceItem item : list){
            if(item.getLocation().equals(name))
                return false;
        }

        return true;
    }

    private String getCityName(String name){
        return name.substring(0,name.length()-13);

    }


    private void createNewList(){

        for(int i=0 ; i<petrolList.size();i++){
            fuelPriceList.get(i).setPetrolPrice(petrolList.get(i));
            fuelPriceList.get(i).setDieselPrice(dieselList.get(i));

        }
        DbUtils.writeToDB(fuelPriceList,state,date,ctx);
//        Log.d(TAG, "createNewList: write to db");
        ((MainActivity)ctx).setListPrice(fuelPriceList);
    }

    void clearAllRequests(){
        reqQueue.cancelAll(TAG);
        //extractor.cancel(true);
    }
}
