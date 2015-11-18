package com.application.zimplyshop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.CategoryObject;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 9/23/2015.
 */
public class CitiesAdapter extends BaseAdapter {

    ArrayList<CategoryObject> objs;

    Context mContext;

    public CitiesAdapter(Context context , ArrayList<CategoryObject> objs){
            this.mContext = context;
        this.objs = objs;
    }
    @Override
    public int getCount() {
        return objs.size();
    }

    @Override
    public Object getItem(int position) {
        return objs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cities_list_item_layout ,null);
        }
        TextView textView = (TextView)convertView.findViewById(R.id.city_name);
        textView.setText(objs.get(position).getName());
        convertView.findViewById(R.id.selected_item).setVisibility(View.GONE);
        return convertView;
    }




}
