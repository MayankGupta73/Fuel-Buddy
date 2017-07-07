package com.mayank7319gmail.fuelbuddy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mayank Gupta on 28-06-2017.
 */

public class PriceRecycler extends RecyclerView.Adapter<PriceRecycler.PriceHolder> {

    private ArrayList<PriceItem> itemList;
    private Context ctx;

    public PriceRecycler(ArrayList<PriceItem> itemList, Context ctx) {
        this.itemList = itemList;
        this.ctx = ctx;
    }

    @Override
    public PriceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = li.inflate(viewType == 1?R.layout.layout_price_item:R.layout.layout_price_item_2
                                    ,parent, false);
        return new PriceHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PriceHolder holder, int position) {
        PriceItem currentItem = itemList.get(position);
        holder.tvLocation.setText(currentItem.getLocation());
        holder.tvPetrol.setText(String.valueOf("\u20B9"+currentItem.getPetrolPrice()));
        holder.tvDiesel.setText(String.valueOf("\u20B9"+currentItem.getDieselPrice()));

        Log.d("Fuel Buddy", "onBindViewHolder: price in "+currentItem.getLocation()+" is "+currentItem.getPetrolPrice()+" "+currentItem.getDieselPrice());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position%2 == 1 ? 0:1;
    }

    public class PriceHolder extends RecyclerView.ViewHolder{
        TextView tvLocation, tvPetrol, tvDiesel;
        public PriceHolder(View itemView) {
            super(itemView);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            tvPetrol = (TextView) itemView.findViewById(R.id.tvPetrol);
            tvDiesel = (TextView) itemView.findViewById(R.id.tvDiesel);
        }
    }
}
