package com.application.zimplyshop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.utils.CommonLib;

public class MenuAdapter extends BaseAdapter {

    Context mContext;
    private String[] subItems = {"Change Location","My Bookings" ,"My Orders","Wishlist", "Feedback", "Support","Rate us on Playstore",  "About", "Logout"};
    private int[] mainIcons = {R.drawable.ic_location_nav_drawer, R.drawable.ic_fav,R.drawable.order_history,R.drawable.ic_wishlist,  R.drawable.ic_feedback, R.drawable.ic_action_support,R.drawable.ic_rate_us,  R.drawable.ic_aboutus, R.drawable.ic_logout};

	public MenuAdapter(Context context) {
		this.mContext = context;
	}

    @Override
    public int getCount() {

        if(AppPreferences.isUserLogIn(mContext)){
            return subItems.length;
        }else{
            return subItems.length-1;
        }

    }

    @Override
    public Object getItem(int position) {
        return subItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list_item_layout, null);
        }

        // parentLayout.setBackgroundResource(R.drawable.grey_card_without_border);
        TextView textView = (TextView) convertView.findViewById(R.id.text_item);
        textView.setText(subItems[position]);
        ImageView img = (ImageView) convertView.findViewById(R.id.icon);
        img.setImageBitmap(CommonLib.getBitmap(mContext, mainIcons[position],
                mContext.getResources().getDimensionPixelSize(R.dimen.height48),
                mContext.getResources().getDimensionPixelSize(R.dimen.height48)));
        TextView expandView = (TextView) convertView.findViewById(R.id.text_expand);
        expandView.setVisibility(View.GONE);
        return convertView;
    }

}
