package com.mayank7319gmail.fuelbuddy;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by Mayank Gupta on 02-08-2017.
 */

public class DbUtils {

    private static String fileNameStart = "db_";

    static void writeToDB(ArrayList<PriceItem> priceItems, String state, String date, Context ctx){
        DbItem newItem = new DbItem(priceItems,state,date);
        Gson gson = new Gson();

        String json = gson.toJson(newItem);
        String fileName = convertStateName(state);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(ctx.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(json);
            outputStreamWriter.close();
        } catch (IOException e) {
//            Log.e("Fuel Buddy", "writeToDB: Write failed "+e.toString());
        }
    }

    static DbItem readFromDB(Context ctx, String state){
        DbItem dbdata = null;
        Gson gson = new Gson();
        String fileName = convertStateName(state);

        File file = ctx.getFileStreamPath(fileName);
        if(!file.exists()) {
//            Log.d("Fuel", "readFromDB: file does not exist");
            return null;
        }

//        Log.d("Fuel", "readFromDB: file exists");
        try {
            InputStream inputStream = ctx.openFileInput(fileName);

            if (inputStream!=null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String output = "";

                StringBuilder stringBuilder = new StringBuilder();

                while ((output = bufferedReader.readLine())!=null){
                    stringBuilder.append(output);
                }

                inputStream.close();

                dbdata = gson.fromJson(stringBuilder.toString(),DbItem.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
//            Log.e("Fuel Buddy", "readFromDB: Read from db failed" );
        }
        return dbdata;
    }

    private static String convertStateName(String state){
        /*String newName = fileNameStart + state;
        if(newName.contains(" "))
           newName = newName.substring(0,newName.indexOf(" "));*/
        String newName = fileNameStart + state.toLowerCase().replace(' ','_');
        if(newName.length()>17)
            newName = newName.substring(0,16);
        newName += ".json";
        return newName;
    }
}
