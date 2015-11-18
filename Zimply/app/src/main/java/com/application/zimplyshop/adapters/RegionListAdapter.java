package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.CategoryObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Umesh Lohani on 10/5/2015.
 */
public class RegionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable{

    ArrayList<CategoryObject> objs;

    ArrayList<CategoryObject> filtered;

    OnItemClickListener mListener;

    Context mContext;

    String selectedId;

    private Filter filter;
    public RegionListAdapter(Context context, ArrayList<CategoryObject> objs){
        mContext = context;
        this.objs = objs;
        filtered = new ArrayList<>(objs);
    }
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CityFilter();
        }
        return filter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cities_list_item_layout,parent,false);
        RecyclerView.ViewHolder holder  = new RegionListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((RegionListHolder)holder).textView.setText(filtered.get(position).getName());
        ((RegionListHolder)holder).parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClicked(filtered.get(position));
            }
        });

        if(selectedId!=null && selectedId.equalsIgnoreCase(filtered.get(position).getId())){
            ((RegionListHolder)holder).selectedBtn.setChecked(true);
        }else{
            ((RegionListHolder)holder).selectedBtn.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    public class RegionListHolder extends RecyclerView.ViewHolder{
        TextView textView;
        FrameLayout parentLayout;
        RadioButton selectedBtn;
        public RegionListHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.city_name);
            selectedBtn = (RadioButton)itemView.findViewById(R.id.selected_item);
            parentLayout = (FrameLayout)itemView.findViewById(R.id.parent);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClicked(filtered.get(getAdapterPosition()));
                }
            });
        }
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
    public interface OnItemClickListener{
        void onItemClicked(CategoryObject obj);
    }

    private class CityFilter extends Filter {
        @Override
        protected android.widget.Filter.FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase(Locale.getDefault());

            FilterResults result = new FilterResults();

            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<CategoryObject> filt = new ArrayList<CategoryObject>();
                ArrayList<CategoryObject> first = new ArrayList<CategoryObject>();
                ArrayList<CategoryObject> lItems = new ArrayList<CategoryObject>();
                synchronized (this) {
                    lItems.addAll(objs);
                }

                CategoryObject r;
                String name;
                int relevent_count = 0;

                if (constraint.toString().contains(" ")) {

                    for (int i = 0, l = lItems.size(); i < l; i++) {
                        r = lItems.get(i);
                        name = r.getName();

                        if (name.toLowerCase(Locale.getDefault()).startsWith((String) constraint)) {
                            first.add(r);
                        }
                    }

                } else {
                    for (int i = 0, l = lItems.size(); i < l; i++) {
                        r = lItems.get(i);
                        name = r.getName();

                        String[] nameTag = name.split(" ");
                        if (nameTag != null && nameTag.length > 0) {

                            for (int j = 0; j < nameTag.length; j++) {
                                if (nameTag[j].toLowerCase(Locale.getDefault()).startsWith((String) constraint)) {
                                    if (j == 0)
                                        first.add(relevent_count++, r);
                                    else
                                        first.add(r);
                                    break;
                                }
                            }
                        } else {
                            if (name.toLowerCase(Locale.getDefault()).startsWith((String) constraint)) {
                                first.add(relevent_count++, r);
                            }
                        }
                    }
                }
                filt.addAll(first);


                result.count = filt.size();
                result.values = filt;


            } else {
                synchronized (this) {
                    result.values = objs;
                    result.count = objs.size();
                }
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, android.widget.Filter.FilterResults results) {
            filtered = (ArrayList<CategoryObject>) results.values;
            if (constraint.toString().length() == 0 && filtered.size() == 0) {
                filtered  = objs;

            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetChanged();

            }
        }

    }
}
