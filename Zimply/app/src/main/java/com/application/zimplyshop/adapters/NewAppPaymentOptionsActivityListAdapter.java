package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;

/**
 * Created by Ashish Goel on 1/18/2016.
 */
public class NewAppPaymentOptionsActivityListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final int TYPE_SAVED_CARDS = 0;
    final int TYPE_NET_BANKING = 1;
    final int TYPE_CREDIT_CARD = 2;
    final int TYPE_DEBIT_CARD = 3;
    final int TYPE_EMI = 4;
    final int TYPE_COD = 5;

    private Context context;
    int currentOpenPosition = 0;

    MyClickListener clickListener;
    boolean isOrderdetailShown;

    public NewAppPaymentOptionsActivityListAdapter(Context context) {
        this.context = context;
        clickListener = new MyClickListener();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_SAVED_CARDS;
        } else if (position == 1) {
            return TYPE_NET_BANKING;
        } else if (position == 2) {
            return TYPE_CREDIT_CARD;
        } else if (position == 3) {
            return TYPE_DEBIT_CARD;
        } else if (position == 4) {
            return TYPE_EMI;
        } else if (position == 5) {
            return TYPE_COD;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SAVED_CARDS) {
            View v = LayoutInflater.from(context).inflate(R.layout.new_app_payment_options_list_item_header, parent, false);
            SavedCardsHolder holder = new SavedCardsHolder(v);
            return holder;
        } else if (viewType == TYPE_NET_BANKING) {
            View v = LayoutInflater.from(context).inflate(R.layout.new_app_payment_options_list_item_net_banking, parent, false);
            NetBankingHolder holder = new NetBankingHolder(v);
            return holder;
        } else if (viewType == TYPE_CREDIT_CARD) {
            View v = LayoutInflater.from(context).inflate(R.layout.new_app_payment_options_list_credit_card, parent, false);
            CreditCardHolder holder = new CreditCardHolder(v);
            return holder;
        } else if (viewType == TYPE_DEBIT_CARD) {
            View v = LayoutInflater.from(context).inflate(R.layout.new_app_payment_options_list_credit_card, parent, false);
            CreditCardHolder holder = new CreditCardHolder(v);
            return holder;
        } else if (viewType == TYPE_EMI) {
            View v = LayoutInflater.from(context).inflate(R.layout.new_app_payment_options_list_item_emi, parent, false);
            EmiHolder holder = new EmiHolder(v);
            return holder;
        } else if (viewType == TYPE_COD) {
            View v = LayoutInflater.from(context).inflate(R.layout.new_app_payment_options_list_item_cod, parent, false);
            CODHolder holder = new CODHolder(v);
            return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderCom, int position) {
        if (getItemViewType(position) == TYPE_SAVED_CARDS) {
            // make this value -1 if there is no saved card
//            currentOpenPosition = -1;

            SavedCardsHolder holder = (SavedCardsHolder) holderCom;
            if (currentOpenPosition == position) {
                holder.layoutHideContent.setVisibility(View.VISIBLE);
            } else {
                holder.layoutHideContent.setVisibility(View.GONE);
            }

            holder.paymentModeTextLayout.setTag(position);
            holder.paymentModeTextLayout.setOnClickListener(clickListener);

            if (isOrderdetailShown)
                holder.orderDetailsLayout.setVisibility(View.VISIBLE);
            else
                holder.orderDetailsLayout.setVisibility(View.GONE);

            holder.viewOrderDetailsButton.setOnClickListener(clickListener);
        } else if (getItemViewType(position) == TYPE_NET_BANKING) {
            NetBankingHolder holder = (NetBankingHolder) holderCom;
            if (currentOpenPosition == position) {
                holder.layoutHideContent.setVisibility(View.VISIBLE);
            } else {
                holder.layoutHideContent.setVisibility(View.GONE);
            }

            holder.paymentModeTextLayout.setTag(position);
            holder.paymentModeTextLayout.setOnClickListener(clickListener);
        } else if (getItemViewType(position) == TYPE_DEBIT_CARD) {
            CreditCardHolder holder = (CreditCardHolder) holderCom;
            if (currentOpenPosition == position) {
                holder.layoutHideContent.setVisibility(View.VISIBLE);
            } else {
                holder.layoutHideContent.setVisibility(View.GONE);
            }

            holder.paymentModeTextLayout.setTag(position);
            holder.paymentModeTextLayout.setOnClickListener(clickListener);
        } else if (getItemViewType(position) == TYPE_CREDIT_CARD) {
            CreditCardHolder holder = (CreditCardHolder) holderCom;
            if (currentOpenPosition == position) {
                holder.layoutHideContent.setVisibility(View.VISIBLE);
            } else {
                holder.layoutHideContent.setVisibility(View.GONE);
            }

            holder.paymentModeTextLayout.setTag(position);
            holder.paymentModeTextLayout.setOnClickListener(clickListener);
        } else if (getItemViewType(position) == TYPE_EMI) {
            EmiHolder holder = (EmiHolder) holderCom;
            if (currentOpenPosition == position) {
                holder.layoutHideContent.setVisibility(View.VISIBLE);
            } else {
                holder.layoutHideContent.setVisibility(View.GONE);
            }

            holder.paymentModeTextLayout.setTag(position);
            holder.paymentModeTextLayout.setOnClickListener(clickListener);
        } else if (getItemViewType(position) == TYPE_COD) {
            CODHolder holder = (CODHolder) holderCom;
            if (currentOpenPosition == position) {
                holder.layoutHideContent.setVisibility(View.VISIBLE);
            } else {
                holder.layoutHideContent.setVisibility(View.GONE);
            }

            holder.paymentModeTextLayout.setTag(position);
            holder.paymentModeTextLayout.setOnClickListener(clickListener);
        }
    }

    class SavedCardsHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutHideContent, paymentModeTextLayout, orderDetailsLayout;
        TextView viewOrderDetailsButton;

        public SavedCardsHolder(View v) {
            super(v);
            layoutHideContent = (LinearLayout) v.findViewById(R.id.hidethispaymentlayut);
            paymentModeTextLayout = (LinearLayout) v.findViewById(R.id.creditcardpaymentoptiontext);
            viewOrderDetailsButton = (TextView) v.findViewById(R.id.view_orderdetailsbutton);
            orderDetailsLayout = (LinearLayout) v.findViewById(R.id.orderdetailslauout);
        }
    }

    class EmiHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutHideContent, paymentModeTextLayout;

        public EmiHolder(View v) {
            super(v);
            layoutHideContent = (LinearLayout) v.findViewById(R.id.hidethispaymentlayut);
            paymentModeTextLayout = (LinearLayout) v.findViewById(R.id.creditcardpaymentoptiontext);
        }
    }

    class NetBankingHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutHideContent, paymentModeTextLayout;

        public NetBankingHolder(View v) {
            super(v);
            layoutHideContent = (LinearLayout) v.findViewById(R.id.hidethispaymentlayut);
            paymentModeTextLayout = (LinearLayout) v.findViewById(R.id.creditcardpaymentoptiontext);
        }
    }

    class CODHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutHideContent, paymentModeTextLayout;

        public CODHolder(View v) {
            super(v);
            layoutHideContent = (LinearLayout) v.findViewById(R.id.hidethispaymentlayut);
            paymentModeTextLayout = (LinearLayout) v.findViewById(R.id.creditcardpaymentoptiontext);
        }
    }

    class CreditCardHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutHideContent, paymentModeTextLayout;

        public CreditCardHolder(View v) {
            super(v);
            layoutHideContent = (LinearLayout) v.findViewById(R.id.hidethispaymentlayut);
            paymentModeTextLayout = (LinearLayout) v.findViewById(R.id.creditcardpaymentoptiontext);
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.creditcardpaymentoptiontext:
                    int pos = (int) v.getTag();
                    if (currentOpenPosition == pos) {
                        currentOpenPosition = -1;
                        notifyItemChanged(pos);
                    } else if (currentOpenPosition == -1) {
                        currentOpenPosition = pos;
                        notifyItemChanged(pos);
                    } else {
                        int temp = currentOpenPosition;
                        currentOpenPosition = -1;
                        notifyItemChanged(temp);
                        currentOpenPosition = pos;
                        notifyItemChanged(pos);
                    }
                    break;
                case R.id.view_orderdetailsbutton:
                    isOrderdetailShown = !isOrderdetailShown;
                    notifyItemChanged(0);
                    break;
            }
        }
    }
}
