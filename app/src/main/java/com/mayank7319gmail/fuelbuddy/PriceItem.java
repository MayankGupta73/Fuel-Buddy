package com.mayank7319gmail.fuelbuddy;

/**
 * Created by Mayank Gupta on 28-06-2017.
 */

public class PriceItem {
    private String location;
    private double petrolPrice, dieselPrice;

    public PriceItem(String location, double petrolPrice, double dieselPrice) {
        this.location = location;
        this.petrolPrice = petrolPrice;
        this.dieselPrice = dieselPrice;
    }

    public String getLocation() {
        return location;
    }

    public double getPetrolPrice() {
        return petrolPrice;
    }

    public double getDieselPrice() {
        return dieselPrice;
    }
}
