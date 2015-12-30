package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.objects.AllUsers;
import com.application.zimplyshop.widgets.CustomCheckBox;
import com.application.zimplyshop.widgets.CustomRadioButton;
import com.application.zimplyshop.widgets.CustomTextView;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 12/8/2015.
 */

//This adapter is used in NewAddressSelectionFragment and AddressListActivity
// The type 1 is for NewAddressSelectionFragment and type 2 for AddressSelectionActivity.
// There are only two differences in all of layout, So I have decided to reuse this class.

public class NewAdressSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<AddressObject> addressObjectArrayList;

    Context mContext;

    //this variable is for single CheckBox selection.
    int selectedPosition = 1;

    int TYPE_HEADER = 0;

    int TYPE_DATA = 1;
    int activityType;

    public NewAdressSelectionAdapter(Context context, int type) {
        this.mContext = context;
        addressObjectArrayList = new ArrayList<>();
        activityType = type;
    }

    public void addData(ArrayList<AddressObject> addressObjectArrayList) {
        this.addressObjectArrayList = new ArrayList<>();
        this.addressObjectArrayList.addAll(addressObjectArrayList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_selection_fragment, parent, false);
            holder = new HeaderHolderView(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_snippet, parent, false);
            holder = new AddressHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (getItemViewType(position) == TYPE_DATA) {
            ((AddressHolder) holder).address.setText(addressObjectArrayList.get(position - 1).getName() + ", "
                    + addressObjectArrayList.get(position - 1).getLine1() + ", "
                    + ((addressObjectArrayList.get(position - 1).getLine2() != null) ?
                    addressObjectArrayList.get(position - 1).getLine2() + ", " : "") +
                    addressObjectArrayList.get(position - 1).getCity() + "\nPincode-" +
                    addressObjectArrayList.get(position - 1).getPincode());
            if (position == selectedPosition) {
                ((AddressHolder) holder).useAddress.setChecked(true);
            } else {
                ((AddressHolder) holder).useAddress.setChecked(false);
            }
            ((AddressHolder) holder).phone.setText(addressObjectArrayList.get(position - 1).getPhone());
            ((AddressHolder) holder).useAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onAddressSelected(position - 1);
                    }
                }
            });

            ((AddressHolder) holder).editAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.editExistingAddress(position - 1);
                }
            });
            if (activityType == 1) {
                ((AddressHolder) holder).useAddress.setVisibility(View.VISIBLE);
                ((AddressHolder) holder).defaultAddress.setVisibility(View.GONE);
            } else {
                ((AddressHolder) holder).useAddress.setVisibility(View.GONE);
                ((AddressHolder) holder).defaultAddress.setVisibility(View.VISIBLE);

                //((AddressHolder) holder).defaultAddress.setOnCheckedChangeListener(null);
                if (position == selectedPosition) {
                    ((AddressHolder) holder).defaultAddress.setChecked(true);
                } else {
                    ((AddressHolder) holder).defaultAddress.setChecked(false);
                }
                ((AddressHolder) holder).defaultAddress.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (((CustomCheckBox) v).isChecked()) {

                            //if (mListener != null)
                            //mListener.onAddressSelected(position - 1);
                            AllUsers.getInstance().swapAddress1(selectedPosition - 1, position - 1);
                            selectedPosition = position;
                            notifyDataSetChanged();

                        }

                    }
                });
            }
        } else {
            ((HeaderHolderView) holder).newAddressHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.addNewAddress();
                }
            });
            if (activityType == 1) {
                ((HeaderHolderView) holder).addressTitle.setText("Choose address to deliver");
                ((HeaderHolderView) holder).addressTitle.setVisibility(View.VISIBLE);
            } else {
                ((HeaderHolderView) holder).addressTitle.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (addressObjectArrayList != null) {
            return addressObjectArrayList.size() + 1;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_DATA;
        }

    }

    public class AddressHolder extends RecyclerView.ViewHolder {
        CustomRadioButton useAddress;
        CustomCheckBox defaultAddress;
        CustomTextView address, phone;
        ImageView editAddress;

        public AddressHolder(View itemView) {
            super(itemView);
            useAddress = (CustomRadioButton) itemView.findViewById(R.id.use_this_address);
            address = (CustomTextView) itemView.findViewById(R.id.address);
            phone = (CustomTextView) itemView.findViewById(R.id.phone);
            editAddress = (ImageView) itemView.findViewById(R.id.edit);
            defaultAddress = (CustomCheckBox) itemView.findViewById(R.id.default_address);
        }
    }

    public class HeaderHolderView extends RecyclerView.ViewHolder {
        CustomTextView newAddressHeader;
        CustomTextView addressTitle;

        public HeaderHolderView(View itemView) {
            super(itemView);
            newAddressHeader = (CustomTextView) itemView.findViewById(R.id.add_new_address);
            addressTitle = (CustomTextView) itemView.findViewById(R.id.address_list_title);

        }
    }

    OnUseAdressClickListener mListener;

    public void setOnUseAdressClickListener(OnUseAdressClickListener listener) {
        this.mListener = listener;
    }


    public interface OnUseAdressClickListener {
        void onAddressSelected(int pos);

        void addNewAddress();

        void editExistingAddress(int pos);
    }
}
