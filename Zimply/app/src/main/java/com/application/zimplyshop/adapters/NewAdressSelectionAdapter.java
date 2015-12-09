package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.widgets.CustomCheckBox;
import com.application.zimplyshop.widgets.CustomTextView;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 12/8/2015.
 */
public class NewAdressSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<AddressObject> addressObjectArrayList;

    Context mContext;


    int TYPE_HEADER;

    int TYPE_DATA;

    public NewAdressSelectionAdapter(Context context){
        this.mContext = context;
        addressObjectArrayList = new ArrayList<>();
    }
    public void addData(ArrayList<AddressObject> addressObjectArrayList){
        this.addressObjectArrayList.addAll(addressObjectArrayList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder ;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_snippet,parent,false);
        holder = new AddressHolder(view);
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if(addressObjectArrayList!=null) {
            return addressObjectArrayList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return TYPE_HEADER;
        }else{
            return  TYPE_DATA;
        }

    }

    public class AddressHolder extends RecyclerView.ViewHolder{
        CustomCheckBox useAddress;
        CustomTextView address,phone;
        ImageView editAddress;
        public AddressHolder(View itemView) {
            super(itemView);
            useAddress = (CustomCheckBox)itemView.findViewById(R.id.use_this_address);
            address = (CustomTextView)itemView.findViewById(R.id.address);
            phone = (CustomTextView)itemView.findViewById(R.id.phone);
            editAddress =  (ImageView)itemView.findViewById(R.id.edit);
        }
    }

    public class HeaderHolderView extends RecyclerView.ViewHolder{
        CustomTextView newAddressHeader;
        public HeaderHolderView(View itemView) {
            super(itemView);
            newAddressHeader = (CustomTextView)itemView.findViewById(R.id.add_new_address);
        }
    }
}
