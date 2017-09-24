package com.mayank7319gmail.fuelbuddy;

import java.util.ArrayList;

/**
 * Created by Mayank Gupta on 02-08-2017.
 */

public class DbItem {
    ArrayList<PriceItem> fuelList;
    String state, date;

    public DbItem(ArrayList<PriceItem> fuelList, String state, String date) {
        this.fuelList = fuelList;
        this.date = date;
        this.state = state;
    }

    public DbItem() {
    }
}
