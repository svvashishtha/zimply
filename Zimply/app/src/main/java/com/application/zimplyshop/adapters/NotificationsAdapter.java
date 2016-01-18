package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.NotificationsActivity;
import com.application.zimplyshop.managers.ImageLoaderManager;
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

    int height,width;

    OnItemClickListener mListener;

    int TYPE_DATA = 0;

    int TYPE_LOADER = 1;

    int TYPE_COLLAPSE_NOTIF = 2;

    public NotificationsAdapter(Context context , int height , int width){
        this.mContext = context;
        this.height = height;
        this.width = width;
        objs = new ArrayList<NotificationListObj>();
    }

    public void addData(ArrayList<NotificationListObj> objs) {
        ArrayList<NotificationListObj> newObjs = new ArrayList<NotificationListObj>(objs);
        this.objs.addAll(this.objs.size(), newObjs);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        RecyclerView.ViewHolder holder ;
        if (itemType == TYPE_DATA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_layout, parent, false);
            holder = new NotificationsViewHolder(view);
        }else if(itemType == TYPE_COLLAPSE_NOTIF){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collapse_notif_layout, parent,
                    false);
            holder = new CollapseNotificationsViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_footer_layout, parent,
                    false);
            holder = new LoadingViewHolder(view);
        }
        return holder;
    }


    public NotificationListObj getItem(int pos){
        return objs.get(pos);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if(getItemViewType(position) == TYPE_DATA) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);

                ((NotificationsViewHolder) holder).notifImage.setLayoutParams(lp);

                ((NotificationsViewHolder) holder).notifDate.setText(TimeUtils.getTimeStampDate(objs.get(position).getCreated_on(), TimeUtils.DATE_TYPE_DAY_MON_DD_YYYY));
                ((NotificationsViewHolder) holder).notifText.setText(objs.get(position).getMessage());
                if(objs.get(position).getSubtext()!=null && objs.get(position).getSubtext().length()>0){
                    ((NotificationsViewHolder) holder).notifSubText.setVisibility(View.VISIBLE);
                    ((NotificationsViewHolder) holder).notifSubText.setText(Html.fromHtml(objs.get(position).getSubtext()));
                }else{
                    ((NotificationsViewHolder) holder).notifSubText.setVisibility(View.GONE);
                }
                new ImageLoaderManager((NotificationsActivity) mContext).setImageFromUrl(objs.get(position).getImage(), ((NotificationsViewHolder) holder).notifImage, "users", width, height, false, false);
                ((NotificationsViewHolder) holder).parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onItemClick(position);
                        }
                    }
                });

            }else if(getItemViewType(position) == TYPE_COLLAPSE_NOTIF){
                ((CollapseNotificationsViewHolder) holder).notifDate.setText(TimeUtils.getTimeStampDate(objs.get(position).getCreated_on(), TimeUtils.DATE_TYPE_DAY_MON_DD_YYYY));
                ((CollapseNotificationsViewHolder) holder).notifText.setText(objs.get(position).getMessage());
                ((CollapseNotificationsViewHolder) holder).notifTitle.setText(objs.get(position).getTitle());
                if(objs.get(position).getSubtext()!=null && objs.get(position).getSubtext().length()>0){
                    ((CollapseNotificationsViewHolder) holder).notifSubText.setVisibility(View.VISIBLE);
                    System.getProperty("line.separator");
                    ((CollapseNotificationsViewHolder) holder).notifSubText.setText(Html.fromHtml(objs.get(position).getSubtext()));
                }else{
                    ((CollapseNotificationsViewHolder) holder).notifSubText.setVisibility(View.GONE);
                }

                ((CollapseNotificationsViewHolder) holder).parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onItemClick(position);
                        }
                    }
                });
            }else{

            }

    }

    @Override
    public int getItemCount() {
        if (objs != null) {
            if (isFooterRemoved) {
                return objs.size();
            } else {
                return objs.size() + 1;
            }
        }
        return 0;
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder{

        TextView notifText,notifDate,notifSubText;

        ImageView notifImage;

        LinearLayout parent ;
        public NotificationsViewHolder(View view) {
            super(view);
            notifText = (TextView )view.findViewById(R.id.notif_text);
            notifDate = (TextView)view.findViewById(R.id.notif_date);
            notifImage = (ImageView)view.findViewById(R.id.notif_img);
            parent = (LinearLayout)view.findViewById(R.id.parent);
            notifSubText = (TextView)view.findViewById(R.id.notif_subtext);
        }
    }

    public class CollapseNotificationsViewHolder extends RecyclerView.ViewHolder{

        TextView notifText,notifDate,notifTitle,notifSubText;
        LinearLayout parent;
        public CollapseNotificationsViewHolder(View view) {
            super(view);
            notifText = (TextView )view.findViewById(R.id.notification_text);
            notifDate = (TextView)view.findViewById(R.id.notification_time);
            notifTitle = (TextView)view.findViewById(R.id.notification_title);
            parent = (LinearLayout)view.findViewById(R.id.parent);
            notifSubText = (TextView)view.findViewById(R.id.notif_subtext);
        }
    }

    public void removePreviousData() {
        if(objs!=null)
            objs.clear();
        isFooterRemoved = false;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == objs.size()) {
            return TYPE_LOADER;
        } else {
            if(objs.get(position).getExpand() == 2){
                return TYPE_DATA;
            }else {
                return TYPE_COLLAPSE_NOTIF;
            }
        }

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

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(View view) {
            super(view);

        }

    }
}
