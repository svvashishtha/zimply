package com.application.zimplyshop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.ShopSubCategoryObj;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 12/16/2015.
 */
public class SubCategoryAdapter extends BaseAdapter{

    Context mContext;

    ArrayList<ShopSubCategoryObj> objs;

    ArrayList<Integer> subCategorIds;



    public SubCategoryAdapter(Context context,ArrayList<ShopSubCategoryObj> objs,ArrayList<Integer> subCategoryId){
        this.mContext = context;
        this.objs = new ArrayList<>(objs);
        this.subCategorIds = new ArrayList<>(subCategoryId);

    }


    public void clearAll(){
        subCategorIds.clear();
        notifyDataSetChanged();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.checkbox_layout,parent,false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder)convertView.getTag();
        holder.name.setText(objs.get(position).getName());
        if(subCategorIds.contains(objs.get(position).getId())){
            holder.name.setChecked(true);
        }else{
            holder.name.setChecked(false);
        }
       /* holder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });*/
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subCategorIds.contains(objs.get(position).getId())){
                    holder.name.setChecked(false);
                    subCategorIds.remove((Integer)objs.get(position).getId());
                }else{
                    holder.name.setChecked(true);
                    subCategorIds.add((Integer)objs.get(position).getId());
                }
            }
        });
        return convertView;
    }

    public ArrayList<Integer> getSubCategorIds() {
        return subCategorIds;
    }

    public class ViewHolder{
        CheckBox name;
        public ViewHolder(View view){
            name = (CheckBox)view.findViewById(R.id.sub_cat_name);
        }
    }
}
