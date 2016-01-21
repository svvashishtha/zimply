package com.application.zimplyshop.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.NewAppPaymentOptionsActivity;
import com.application.zimplyshop.baseobjects.CartObject;

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

    CartObject cartObject;

    public NewAppPaymentOptionsActivityListAdapter(Context context, CartObject cartObject) {
        this.context = context;
        this.cartObject = cartObject;
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

            if (isOrderdetailShown) {
                holder.orderDetailsLayout.setVisibility(View.VISIBLE);
                holder.viewOrderDetailsButton.setText("Hide Details");
            } else {
                holder.orderDetailsLayout.setVisibility(View.GONE);
                holder.viewOrderDetailsButton.setText("Show Details");
            }

            holder.viewOrderDetailsButton.setOnClickListener(clickListener);

            float totalPrice = cartObject.getCart().getTotal_price();
            float price = cartObject.getCart().getPrice();
            holder.discountLayout.setVisibility(View.GONE);
            if (totalPrice == 0) {
                holder.totalSum.setText(context.getResources().getString(R.string.Rs) + " " + cartObject.getCart().getPrice());
                holder.totalPayment.setText(context.getResources().getString(R.string.Rs) + " " + cartObject.getCart().getTotal_price());
            }
            holder.orderTotal.setText(context.getResources().getString(R.string.Rs) + " " + Math.round(totalPrice));
            holder.totalSum.setText(context.getResources().getString(R.string.Rs) + " " + Math.round(price));
            holder.totalPayment.setText(context.getResources().getString(R.string.Rs) + " " + Math.round(totalPrice));
            holder.shipping.setText((cartObject.getCart().getTotal_shipping() == 0) ? "Free" : (context.getResources().getString(R.string.Rs) + " " + cartObject.getCart().getTotal_shipping()));

            holder.paySecurely.setTag(R.integer.z_tag_position_recycler_view, position);
            holder.paySecurely.setTag(R.integer.z_tag_holder_recycler_view, holder);
            holder.paySecurely.setOnClickListener(clickListener);
        } else if (getItemViewType(position) == TYPE_NET_BANKING) {
            NetBankingHolder holder = (NetBankingHolder) holderCom;
            if (currentOpenPosition == position) {
                holder.layoutHideContent.setVisibility(View.VISIBLE);
            } else {
                holder.layoutHideContent.setVisibility(View.GONE);
            }

            holder.paymentModeTextLayout.setTag(position);
            holder.paymentModeTextLayout.setOnClickListener(clickListener);

            holder.paySecurely.setTag(R.integer.z_tag_position_recycler_view, position);
            holder.paySecurely.setTag(R.integer.z_tag_holder_recycler_view, holder);
            holder.paySecurely.setOnClickListener(clickListener);
        } else if (getItemViewType(position) == TYPE_DEBIT_CARD) {
            CreditCardHolder holder = (CreditCardHolder) holderCom;
            if (currentOpenPosition == position) {
                holder.layoutHideContent.setVisibility(View.VISIBLE);
            } else {
                holder.layoutHideContent.setVisibility(View.GONE);
            }

            holder.paymentModeTextLayout.setTag(position);
            holder.paymentModeTextLayout.setOnClickListener(clickListener);

            holder.creditCardOrDebitCard.setText("Debit Card");
            holder.crediTordebitImage.setImageResource(R.drawable.ic_debit_card);

            holder.paySecurely.setTag(R.integer.z_tag_position_recycler_view, position);
            holder.paySecurely.setTag(R.integer.z_tag_holder_recycler_view, holder);
            holder.paySecurely.setOnClickListener(clickListener);
        } else if (getItemViewType(position) == TYPE_CREDIT_CARD) {
            CreditCardHolder holder = (CreditCardHolder) holderCom;
            if (currentOpenPosition == position) {
                holder.layoutHideContent.setVisibility(View.VISIBLE);
            } else {
                holder.layoutHideContent.setVisibility(View.GONE);
            }

            holder.paymentModeTextLayout.setTag(position);
            holder.paymentModeTextLayout.setOnClickListener(clickListener);

            holder.creditCardOrDebitCard.setText("Credit Card");
            holder.crediTordebitImage.setImageResource(R.drawable.ic_credit_card);

            holder.paySecurely.setTag(R.integer.z_tag_position_recycler_view, position);
            holder.paySecurely.setTag(R.integer.z_tag_holder_recycler_view, holder);
            holder.paySecurely.setOnClickListener(clickListener);
        } else if (getItemViewType(position) == TYPE_EMI) {
            EmiHolder holder = (EmiHolder) holderCom;
            if (currentOpenPosition == position) {
                holder.layoutHideContent.setVisibility(View.VISIBLE);
            } else {
                holder.layoutHideContent.setVisibility(View.GONE);
            }

            holder.paymentModeTextLayout.setTag(position);
            holder.paymentModeTextLayout.setOnClickListener(clickListener);
            holder.paySecurely.setTag(R.integer.z_tag_position_recycler_view, position);
            holder.paySecurely.setTag(R.integer.z_tag_holder_recycler_view, holder);
            holder.paySecurely.setOnClickListener(clickListener);
        } else if (getItemViewType(position) == TYPE_COD) {
            CODHolder holder = (CODHolder) holderCom;
            if (currentOpenPosition == position) {
                holder.layoutHideContent.setVisibility(View.VISIBLE);
            } else {
                holder.layoutHideContent.setVisibility(View.GONE);
            }

            holder.paymentModeTextLayout.setTag(position);
            holder.paymentModeTextLayout.setOnClickListener(clickListener);

            holder.codAmountText.setText("Pay ₹ " + cartObject.getCart().getTotal_price() + " at the time of delivery");
            holder.paySecurely.setTag(R.integer.z_tag_position_recycler_view, position);
            holder.paySecurely.setTag(R.integer.z_tag_holder_recycler_view, holder);
            holder.paySecurely.setOnClickListener(clickListener);
        }
    }

    class SavedCardsHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutHideContent, paymentModeTextLayout, orderDetailsLayout;
        TextView viewOrderDetailsButton;
        TextView totalPayment, orderTotal, shipping, totalSum, discountValue;
        LinearLayout discountLayout;
        Button paySecurely;

        public SavedCardsHolder(View v) {
            super(v);
            layoutHideContent = (LinearLayout) v.findViewById(R.id.hidethispaymentlayut);
            paymentModeTextLayout = (LinearLayout) v.findViewById(R.id.creditcardpaymentoptiontext);
            viewOrderDetailsButton = (TextView) v.findViewById(R.id.view_orderdetailsbutton);
            orderDetailsLayout = (LinearLayout) v.findViewById(R.id.orderdetailslauout);
            totalPayment = (TextView) v.findViewById(R.id.total_payment);
            shipping = (TextView) v.findViewById(R.id.shipping_charges);
            totalSum = (TextView) v.findViewById(R.id.total_sum);
            orderTotal = (TextView) v.findViewById(R.id.paymentheaderpricetext);
            discountLayout = (LinearLayout) v.findViewById(R.id.applied_coupon_discount);
            paySecurely = (Button) v.findViewById(R.id.paysecurely__codcod);
            discountValue = (TextView) v.findViewById(R.id.discount);
        }
    }

    class EmiHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutHideContent, paymentModeTextLayout;
        Button paySecurely;

        public EmiHolder(View v) {
            super(v);
            layoutHideContent = (LinearLayout) v.findViewById(R.id.hidethispaymentlayut);
            paymentModeTextLayout = (LinearLayout) v.findViewById(R.id.creditcardpaymentoptiontext);
            paySecurely = (Button) v.findViewById(R.id.paysecurely__codcod);
        }
    }

    class NetBankingHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutHideContent, paymentModeTextLayout;
        Button paySecurely;

        public NetBankingHolder(View v) {
            super(v);
            layoutHideContent = (LinearLayout) v.findViewById(R.id.hidethispaymentlayut);
            paymentModeTextLayout = (LinearLayout) v.findViewById(R.id.creditcardpaymentoptiontext);
            paySecurely = (Button) v.findViewById(R.id.paysecurely__codcod);
        }
    }

    class CODHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutHideContent, paymentModeTextLayout;
        TextView codAmountText;
        Button paySecurely;

        public CODHolder(View v) {
            super(v);
            codAmountText = (TextView) v.findViewById(R.id.codamountteztz);
            paySecurely = (Button) v.findViewById(R.id.paysecurely__codcod);
            layoutHideContent = (LinearLayout) v.findViewById(R.id.hidethispaymentlayut);
            paymentModeTextLayout = (LinearLayout) v.findViewById(R.id.creditcardpaymentoptiontext);
        }
    }

    class CreditCardHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutHideContent, paymentModeTextLayout;
        TextView creditCardOrDebitCard;ImageView crediTordebitImage;
        Button paySecurely;

        EditText cardNumber, cardName, cvv;

        public CreditCardHolder(View v) {
            super(v);
            layoutHideContent = (LinearLayout) v.findViewById(R.id.hidethispaymentlayut);
            paymentModeTextLayout = (LinearLayout) v.findViewById(R.id.creditcardpaymentoptiontext);
            creditCardOrDebitCard = (TextView) v.findViewById(R.id.creditcardtext);
            paySecurely = (Button) v.findViewById(R.id.paysecurely__codcod);
            cardName = (EditText) v.findViewById(R.id.nameOnCardEditText);
            cardNumber = (EditText) v.findViewById(R.id.cardNumberEditText);
            cvv = (EditText) v.findViewById(R.id.cvvEditText);
            crediTordebitImage=(ImageView)v.findViewById(R.id.crefitcardimagel);
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
                case R.id.paysecurely__codcod:
                    pos = (int) v.getTag(R.integer.z_tag_position_recycler_view);
                    if (getItemViewType(pos) == TYPE_COD) {
                        ((NewAppPaymentOptionsActivity) context).sendPaymentSuccessFullCashRequest();
                    } else if (getItemViewType(pos) == TYPE_CREDIT_CARD) {
                        CreditCardHolder holder = (CreditCardHolder) v.getTag(R.integer.z_tag_holder_recycler_view);
                        ((NewAppPaymentOptionsActivity) context).openPayUWebViewForCreditCard(holder.cardNumber.getText().toString().trim(), holder.cardName.getText().toString().trim(), "06", "2022", holder.cvv.getText().toString().trim());
                    }
                    break;
            }
        }
    }
}
