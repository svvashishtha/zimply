package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.objects.NotificationListObj;
import com.application.zimplyshop.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 11/19/2015.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context mContext;

    ArrayList<NotificationListObj> objs;

    boolean isFooterRemoved;

    int height;

    OnItemClickListener mListener;

    public NotificationsAdapter(Context context , int height){
        this.mContext = context;
        this.height = height;
    }

    public void addData(ArrayList<NotificationListObj> objs) {
        ArrayList<NotificationListObj> newObjs = new ArrayList<NotificationListObj>(objs);
        this.objs.addAll(this.objs.size(), newObjs);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder ;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_layout,parent,false);
        holder = new NotificationsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((NotificationsViewHolder)holder).notifDate.setText(TimeUtils.getTimeStampDate(objs.get(position).getCreated_on(),TimeUtils.DATE_TYPE_DAY_MON_DD_YYYY));
        ((NotificationsViewHolder)holder).notifText.setText(objs.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder{

        TextView notifText,notifDate;

        ImageView notifImage;

        public NotificationsViewHolder(View view) {
            super(view);
            notifText = (TextView )view.findViewById(R.id.notif_text);
            notifDate = (TextView)view.findViewById(R.id.notif_date);
            notifImage = (ImageView)view.findViewById(R.id.notif_img);
        }
    }

    public void removePreviousData() {
        objs.clear();
        isFooterRemoved = false;
        notifyDataSetChanged();
    }

    public void removeItem() {
        isFooterRemoved = true;
        notifyItemRemoved(objs.size());
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(int pos);
    }
}
