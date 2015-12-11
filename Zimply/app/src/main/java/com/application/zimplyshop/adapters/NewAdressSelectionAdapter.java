package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.widgets.CustomRadioButton;
import com.application.zimplyshop.widgets.CustomTextView;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 12/8/2015.
 */
public class NewAdressSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<AddressObject> addressObjectArrayList;

    Context mContext;


    int TYPE_HEADER=0;

    int TYPE_DATA=1;

    public NewAdressSelectionAdapter(Context context){
        this.mContext = context;
        addressObjectArrayList = new ArrayList<>();
    }
    public void addData(ArrayList<AddressObject> addressObjectArrayList){
        this.addressObjectArrayList = new ArrayList<>();
        this.addressObjectArrayList.addAll(addressObjectArrayList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder ;
        if(viewType == TYPE_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_selection_fragment, parent, false);
            holder = new HeaderHolderView(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_snippet, parent, false);
            holder = new AddressHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(getItemViewType(position) == TYPE_DATA){
            ((AddressHolder)holder).address.setText(addressObjectArrayList.get(position-1).getName()+", "
                    +addressObjectArrayList.get(position-1).getLine1()+", "
                    +((addressObjectArrayList.get(position-1).getLine2()!=null)?
                    addressObjectArrayList.get(position-1).getLine2()+", ":"")+
                    addressObjectArrayList.get(position-1).getCity()+"\nPincode-"+
                    addressObjectArrayList.get(position-1).getPincode());
            if(position == 1) {
                ((AddressHolder) holder).useAddress.setChecked(true);
            }else{
                ((AddressHolder) holder).useAddress.setChecked(false);
            }
            ((AddressHolder)holder).phone.setText(addressObjectArrayList.get(position-1).getPhone());
            ((AddressHolder)holder).useAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onAddressSelected(position - 1);
                    }
                }
            });
            ((AddressHolder)holder).editAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.editExistingAddress(position-1);
                }
            });
        }else{
            ((HeaderHolderView)holder).newAddressHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.addNewAddress();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(addressObjectArrayList!=null) {
            return addressObjectArrayList.size()+1;
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
        CustomRadioButton useAddress;
        CustomTextView address,phone;
        ImageView editAddress;
        public AddressHolder(View itemView) {
            super(itemView);
            useAddress = (CustomRadioButton)itemView.findViewById(R.id.use_this_address);
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

    OnUseAdressClickListener mListener;

    public void setOnUseAdressClickListener(OnUseAdressClickListener listener){
        this.mListener = listener;
    }


    public interface OnUseAdressClickListener{
        void onAddressSelected(int pos);
        void addNewAddress();
        void editExistingAddress(int pos);
    }
}
