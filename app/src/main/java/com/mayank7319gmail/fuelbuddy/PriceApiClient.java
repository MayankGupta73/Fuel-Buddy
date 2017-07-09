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

/**
 * Created by Mayank Gupta on 29-06-2017.
 */

public class PriceApiClient {

    private String urlPetrol = "http://www.mypetrolprice.com/petrol-price-in-india.aspx";
    //private String urlDiesel = "http://www.mypetrolprice.com/diesel-price-in-india.aspx";
    private RequestQueue reqQueue;
    private ArrayList<PriceItem> fuelPriceList ;
    //private ArrayList<SubPriceItem> subPetrolList, subDieselList;
    private ArrayList<Double> petrolList,dieselList;
    private LinkExtractor extractor;


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
        StringRequest request = getRequest(urlPetrol,stateCode);

        reqQueue.add(request);
    }

     private StringRequest getRequest(final String url, final String stateCode){
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
                        Log.e(TAG, "onErrorResponse: unable to access data from server. type is " + url);
                        Toast.makeText(ctx,"Unable to fetch data. Please try again later.",Toast.LENGTH_SHORT).show();

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

//                        Log.d(TAG, "doInBackground: test name "+cityName);
//                        Log.d(TAG, "doInBackground: test link " +petrolLink);

                        String dieselLink = petrolLink.replaceAll("Petrol","Diesel");
//                        Log.d(TAG, "doInBackground: test diesel link "+dieselLink);

//                        SubPriceItem tempItem = new SubPriceItem(cityName, 0, 0);
                        if(checkName(fuelPriceList,cityName)){
                            fuelPriceList.add(new PriceItem(cityName,0.0,0.0));

//                            subPetrolList.add(tempItem);                    //Fetch Petrol
                            publishProgress(cityName,petrolLink,"p");

//                            subDieselList.add(tempItem);                    //Fetch Diesel
                            publishProgress(cityName,dieselLink,"d");
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "getFuelPriceList: error jsoup" );
                Toast.makeText(ctx,"Unable to fetch data. Please try again later.",Toast.LENGTH_SHORT).show();
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
//            Log.d(TAG, "onPostExecute: arraySize "+arraySize);

//            if(!(subDieselList == null || subPetrolList == null || subPetrolList.isEmpty() || subDieselList.isEmpty()))
//                createPriceList();
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
                Log.d(TAG, "doInBackground: "+extract);
                Double price;
                if(extract.contains("R")) {
                     price = Double.valueOf(extract.substring(0, 2));
                }
                else  price = Double.valueOf(extract);

                Log.d(TAG, "doInBackground: price of "+type+" in "+cityName+" is "+price);

                if(type.equals("p")){
                    /*for(SubPriceItem subP: subPetrolList){
                        if(subP.getCity().equals(cityName)) {
                            subP.price = price;
                            petrolList.add(price);
                            Log.d(TAG, "doInBackground: added "+subP.getPrice()+" to petrol index "+subPetrolList.indexOf(subP));
                        }
                    }
*/
//                    Log.d(TAG, "doInBackground: petrol "+price);
                    int x =petrolList.size();
                    if(cityName.equals(fuelPriceList.get(x).getLocation()))
                    petrolList.add(price);
                }
                else if(type.equals("d")){
                    /*for(SubPriceItem subD: subDieselList){
                        if(subD.getCity().equals(cityName)) {
                            subD.price = price;
                            dieselList.add(price);
                            Log.d(TAG, "doInBackground: added " + subD.getPrice() + " to diesel index "+subDieselList.indexOf(subD));
                        }
                    }*/
//                    Log.d(TAG, "doInBackground: diesel "+price);
                    int x =dieselList.size();
                    if(cityName.equals(fuelPriceList.get(x).getLocation()))
                    dieselList.add(price);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "doInBackground: error getting price of "+reqUrl);
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
                Log.d(TAG, "onPostExecute: count "+count);
            }else {

                /*Log.d(TAG, "onPostExecute: petrol list");
                for(SubPriceItem sub1: subPetrolList)
                    Log.d(TAG, "onPostExecute: "+sub1.getCity()+sub1.getPrice());

                Log.d(TAG, "onPostExecute: diesel list");
                for(SubPriceItem sub: subDieselList)
                    Log.d(TAG, "onPostExecute: "+sub.getCity()+sub.getPrice());*/

                createNewList();
               // createPriceList();
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


   /* public void createPriceList(){
        if(subPetrolList == null || subDieselList == null ){
            Log.e(TAG, "createPriceList: error one of the lists is empty");
            return;
        }

        Log.d(TAG, "createPriceList: function was called");

        for(int i=0; i<subPetrolList.size(); i++){
            fuelPriceList.add(new PriceItem(subPetrolList.get(i).city,subPetrolList.get(i).price,subDieselList.get(i).price));

            PriceItem temp = fuelPriceList.get(i);
            Log.d(TAG, "createPriceList: price in "+temp.getLocation()+" is "+temp.getPetrolPrice()+" "+temp.getDieselPrice());
        }

        *//*iocPriceList = new ArrayList<>();
        for(int i=0; i<subPetrolIoc.size(); i++){
            iocPriceList.add(new PriceItem(subPetrolIoc.get(i).city,subPetrolIoc.get(i).price,subDieselIoc.get(i).price));
        }*//*

        //Todo set these lists in recycler
    }*/

    private void createNewList(){
//        Log.d(TAG, "createNewList: function called");

        for(int i=0 ; i<petrolList.size();i++){
            fuelPriceList.get(i).setPetrolPrice(petrolList.get(i));
            fuelPriceList.get(i).setDieselPrice(dieselList.get(i));

            /*PriceItem temp = fuelPriceList.get(i);
            Log.d(TAG, "createNewList: price in "+temp.getLocation()+" is "+temp.getPetrolPrice()+" "+temp.getDieselPrice());*/
        }

        ((MainActivity)ctx).setListPrice(fuelPriceList);
    }

    void clearAllRequests(){
        reqQueue.cancelAll(TAG);
        //extractor.cancel(true);
    }
}
