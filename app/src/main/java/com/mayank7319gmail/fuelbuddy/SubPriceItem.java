package com.mayank7319gmail.fuelbuddy;

/**
 * Created by Mayank Gupta on 29-06-2017.
 */

public class SubPriceItem {
    String city ;
    double price,change;

    public String getCity() {
        return city;
    }

    public double getPrice() {
        return price;
    }

    public double getChange() {
        return change;
    }

    public SubPriceItem(String city, double price, double change) {

        this.city = city;
        this.price = price;
        this.change = change;
    }

    public SubPriceItem() {
    }
}
