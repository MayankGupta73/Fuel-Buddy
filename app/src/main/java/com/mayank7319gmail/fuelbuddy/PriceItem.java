package com.mayank7319gmail.fuelbuddy;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mayank Gupta on 28-06-2017.
 */

public class PriceItem implements Parcelable{
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

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPetrolPrice(double petrolPrice) {
        this.petrolPrice = petrolPrice;
    }

    public void setDieselPrice(double dieselPrice) {
        this.dieselPrice = dieselPrice;
    }

    public PriceItem() {
    }

    protected PriceItem(Parcel in) {
        location = in.readString();
        petrolPrice = in.readDouble();
        dieselPrice = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(location);
        dest.writeDouble(petrolPrice);
        dest.writeDouble(dieselPrice);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PriceItem> CREATOR = new Parcelable.Creator<PriceItem>() {
        @Override
        public PriceItem createFromParcel(Parcel in) {
            return new PriceItem(in);
        }

        @Override
        public PriceItem[] newArray(int size) {
            return new PriceItem[size];
        }
    };

}
