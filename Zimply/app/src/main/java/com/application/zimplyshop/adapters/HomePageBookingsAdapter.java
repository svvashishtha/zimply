package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.zimplyshop.R;

/**
 * Created by Umesh Lohani on 11/26/2015.
 */
public class HomePageBookingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context mContext;

    public HomePageBookingsAdapter(Context context){
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_booking_layout,parent,false);
        RecyclerView.ViewHolder holder = new BookingHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }

public class BookingHolder extends RecyclerView.ViewHolder{

    public BookingHolder(View itemView) {
        super(itemView);
    }
}
}
