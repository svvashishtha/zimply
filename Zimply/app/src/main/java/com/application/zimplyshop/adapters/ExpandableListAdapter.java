package com.application.zimplyshop.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.CategoriesObject;

import java.util.ArrayList;

/**
 * Created by Saurabh on 22-08-2015.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
    ArrayList<CategoriesObject> mData;
    Context context;
    selectedCategoryListener mListener;

    int selectedGrpPosition=-1,selectedChildPosition=-1;

    boolean isVerifyGroup;

    public ExpandableListAdapter(Context context, ArrayList<CategoriesObject> mData) {
        this.context = context;
        this.mData = mData;
    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    public void setSelectedCategoryListener(selectedCategoryListener listener) {
        mListener = listener;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mData.get(groupPosition).getSubCategories().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ItemHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expandable_list_row_item, parent, false);
            holder = new ItemHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (ItemHolder) convertView.getTag();

        holder.parentLayout.setBackgroundResource(R.drawable.white_card_without_border);
        holder.item.setTypeface(null, Typeface.BOLD);
        holder.item.setText(mData.get(groupPosition).getName());

        if(selectedGrpPosition == groupPosition && isVerifyGroup){
            holder.item.setTextColor(context.getResources().getColor(R.color.z_blue_color));
        }else{
            holder.item.setTextColor(context.getResources().getColor(R.color.text_color1));
        }
        if(getChildrenCount(groupPosition)>0) {
            if(isExpanded){
                addDrawableRight(holder.item, R.drawable.ic_up_black);
            }else {
                addDrawableRight(holder.item, R.drawable.ic_down_black);
            }
        }else{
            holder.item.setCompoundDrawables(null,null,null,null);
        }
       /* convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.get(groupPosition).getSubCategories().size() > 0) {
                    // do nothing here
                } else {
                    if (mListener != null)
                        mListener.setSelectedCategory(mData.get(groupPosition).getName());
                }
            }
        });*/
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ItemHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expandable_list_row_item, parent, false);
            holder = new ItemHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (ItemHolder) convertView.getTag();
        holder.parentLayout.setBackgroundResource(R.drawable.grey_card_without_border);
        holder.item.setText(mData.get(groupPosition).getSubCategories().get(childPosition));
        holder.item.setPadding((int) context.getResources().getDimension(R.dimen.z_margin_xlarge),
                (int) context.getResources().getDimension(R.dimen.z_margin_medium),
                (int) context.getResources().getDimension(R.dimen.z_margin_medium),
                (int) context.getResources().getDimension(R.dimen.z_margin_medium));

        /*lp.setMargins((int) context.getResources().getDimension(R.dimen.z_margin_large),
                (int) context.getResources().getDimension(R.dimen.z_margin_medium),
                (int) context.getResources().getDimension(R.dimen.z_margin_medium),
                (int) context.getResources().getDimension(R.dimen.z_margin_medium));*/
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.setSelectedCategory(groupPosition,childPosition,mData.get(groupPosition).getSubCategories().get(childPosition));
            }
        });
        if(selectedChildPosition == childPosition && selectedGrpPosition == groupPosition){
            holder.item.setTextColor(context.getResources().getColor(R.color.z_blue_color));
        }else{
            holder.item.setTextColor(context.getResources().getColor(R.color.text_color1));
        }
        return convertView;
    }


    public void setSelectedChildPosition(int selectedChildPosition,int selectedGroupPos) {
        isVerifyGroup = false;
        this.selectedGrpPosition = selectedGroupPos;
        this.selectedChildPosition = selectedChildPosition;
        notifyDataSetChanged();
    }

    public void setSelectedGrpPosition(int selectedGrpPosition) {
        isVerifyGroup = true;
        selectedChildPosition = -1;
        this.selectedGrpPosition = selectedGrpPosition;
        notifyDataSetChanged();
    }

    public void addDrawableRight(TextView textView , int resId){
        Drawable d = context.getResources().getDrawable(resId);
        d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
        textView.setCompoundDrawables(null,null,d,null);
    }


    public void addDrawableLeft(TextView textView , int resId){
        Drawable d = context.getResources().getDrawable(resId);
        d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
                textView.setCompoundDrawables(d,null,null,null);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ItemHolder {
        TextView item;
        LinearLayout parentLayout;

        public ItemHolder(View view) {
            item = (TextView) view.findViewById(R.id.text_item);
            parentLayout = (LinearLayout)view.findViewById(R.id.parent_layout);
        }
    }

    public interface selectedCategoryListener {
        void setSelectedCategory(int groupPos,int pos,String categoryName);
    }

    public String getCategoryName(int grpPos){
        return  mData.get(grpPos).getName();
    }
}