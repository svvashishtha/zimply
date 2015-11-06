package com.application.zimply.adapters;

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

import com.application.zimply.R;
import com.application.zimply.baseobjects.CategoryObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Saurabh on 05-10-2015.
 */
public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.ItemHolder> implements Filterable {
    ArrayList<CategoryObject> objs;

    ArrayList<CategoryObject> filtered;

    OnItemClickListener mListener;
    Context mContext;
    String selectedId;

    public CityListAdapter(Context context, ArrayList<CategoryObject> objs) {
        mContext = context;
        this.objs = objs;
        filtered = new ArrayList<>(objs);
    }

    private Filter filter;

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CityFilter();
        }
        return filter;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cities_list_item_layout, parent,false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, final int position) {
        holder.textView.setText(filtered.get(position).getName());
        holder.itemParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClicked(filtered.get(position));
            }
        });

        if(selectedId!=null && selectedId.equalsIgnoreCase(filtered.get(position).getId())){
            holder.selectedBtn.setChecked(true);
        }else{
            holder.selectedBtn.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RadioButton selectedBtn;
        FrameLayout itemParent;
        public ItemHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.city_name);
            selectedBtn = (RadioButton)itemView.findViewById(R.id.selected_item);
            itemParent = (FrameLayout)itemView.findViewById(R.id.parent);

        }
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
                filtered = objs;

            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetChanged();

            }
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(CategoryObject obj);
    }
}
