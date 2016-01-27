package com.application.zimplyshop.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.NewAppPaymentOptionsActivity;
import com.application.zimplyshop.baseobjects.CartObject;
import com.application.zimplyshop.widgets.CustomEdittext;
import com.application.zimplyshop.widgets.CustomRadioButton;
import com.application.zimplyshop.widgets.ZNothingSelectedSpinnerAdapter;
import com.payu.india.Model.PaymentDetails;
import com.payu.india.Model.PayuResponse;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    int currentOpenPosition = 0, totalPrice;
    boolean isCodNotAvailable;
    int currentOpenSavedCardPos = 0;

    MyClickListener clickListener;
    boolean isOrderdetailShown;

    CartObject cartObject;

    private PayuUtils payuUtils;
    private String issuer;

    boolean toShowEmiItem = false;

    PayuResponse payuResponse;

    String[] bankCodesFixed = {"SBIB", "HDFB", "ICIB", "AXIB", "162B", "YESB"};

    int deleteItemPosition = -1;

    public NewAppPaymentOptionsActivityListAdapter(Context context, CartObject cartObject, PayuResponse payuResponse, int totalPrice, boolean isCodNotAvailable) {
        this.context = context;
        this.cartObject = cartObject;
        clickListener = new MyClickListener();
        payuUtils = new PayuUtils();
        this.payuResponse = payuResponse;
        this.totalPrice = totalPrice;
        this.isCodNotAvailable = isCodNotAvailable;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_SAVED_CARDS;
        } else if (position == 1) {
            return TYPE_NET_BANKING;
        } else if (position == 2) {
            return TYPE_DEBIT_CARD;
        } else if (position == 3) {
            return TYPE_CREDIT_CARD;
        } else {
            if (toShowEmiItem) {
                if (position == 4) {
                    return TYPE_EMI;
                } else if (position == 5) {
                    return TYPE_COD;
                }
            } else {
                return TYPE_COD;
            }
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
            SavedCardsHolder holder = (SavedCardsHolder) holderCom;
            if (currentOpenPosition == position) {
                holder.layoutHideContent.setVisibility(View.VISIBLE);
            } else {
                holder.layoutHideContent.setVisibility(View.GONE);
            }

            if (payuResponse.getStoredCards() == null || payuResponse.getStoredCards().size() == 0) {
                holder.mainSavedCardsBgLayout.setVisibility(View.GONE);
            } else {
                holder.mainSavedCardsBgLayout.setVisibility(View.VISIBLE);

                holder.savedCardsDynamicContainer.removeAllViews();
                for (int i = 0; i < payuResponse.getStoredCards().size(); i++) {
                    View view = LayoutInflater.from(context).inflate(R.layout.new_app_payment_options_saved_card_list_item, holder.savedCardsDynamicContainer, false);
                    CustomRadioButton radioButton = (CustomRadioButton) view.findViewById(R.id.radiobuttonsavedcard);
                    LinearLayout savedCardButtonLayout = (LinearLayout) view.findViewById(R.id.savecardbuttonlaut);
                    ImageView cardtypeImage = (ImageView) view.findViewById(R.id.cartNumberTypeImage);
                    LinearLayout deleteButton = (LinearLayout) view.findViewById(R.id.deletesavecard);
                    Button button = (Button) view.findViewById(R.id.paysecurelysavedcardl);
                    CustomEdittext edittextCvv = (CustomEdittext) view.findViewById(R.id.cvvEditText);

                    button.setTag(edittextCvv);
                    button.setOnClickListener(clickListener);

                    if (null == issuer)
                        issuer = payuUtils.getIssuer(payuResponse.getStoredCards().get(i).getMaskedCardNumber());
                    if (issuer != null && issuer.length() > 1) {
                        int image = getIssuerImage(issuer);
                        cardtypeImage.setImageResource(image);
                    }

                    radioButton.setText(payuResponse.getStoredCards().get(i).getMaskedCardNumber());
                    if (i == currentOpenSavedCardPos) {
                        radioButton.setChecked(true);
                        savedCardButtonLayout.setVisibility(View.VISIBLE);
                    } else {
                        radioButton.setChecked(false);
                        savedCardButtonLayout.setVisibility(View.GONE);
                    }

                    radioButton.setTag(i);
                    radioButton.setOnClickListener(clickListener);

                    deleteButton.setTag(i);
                    deleteButton.setOnClickListener(clickListener);

                    if (payuResponse.getStoredCards().size() == 1) {
                        edittextCvv.requestFocus();
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(edittextCvv, InputMethodManager.SHOW_IMPLICIT);
                    }

                    holder.savedCardsDynamicContainer.addView(view);
                }
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

            setDataForNetbankingBanks(holder);
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

            holder.skipCvvAndExpirytext.setVisibility(View.VISIBLE);

            addFunctionalityForCardNumberTextChanged(holder.cardNumber, holder.cartNumberTypeImage, holder.cvv);

            addFunctionalityForSpinners(holder.monthSpinner, holder.yearSpinner);
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

            holder.skipCvvAndExpirytext.setVisibility(View.GONE);

            addFunctionalityForCardNumberTextChanged(holder.cardNumber, holder.cartNumberTypeImage, holder.cvv);

            addFunctionalityForSpinners(holder.monthSpinner, holder.yearSpinner);
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

            if (isCodNotAvailable || totalPrice > 20000) {
                holder.codEligibleLayout.setVisibility(View.GONE);
                holder.codNotEligibleLayout.setVisibility(View.VISIBLE);
                holder.codNotEligibletext.setTypeface(holder.codNotEligibletext.getTypeface(), Typeface.ITALIC);

                if (isCodNotAvailable) {
                    holder.codNotEligibletext.setText(context.getResources().getString(R.string.cod_not_avail));
                } else {
                    holder.codNotEligibletext.setText(context.getResources().getString(R.string.one_or_more_items_not_have_cod));
                }
            } else {
                holder.codEligibleLayout.setVisibility(View.VISIBLE);
                holder.codNotEligibleLayout.setVisibility(View.GONE);
            }
        }
    }

    private void setDataForNetbankingBanks(final NetBankingHolder holder) {
        ArrayList<String> banks = new ArrayList<>();
        for (PaymentDetails details : payuResponse.getNetBanks()) {
            banks.add(details.getBankName());
        }

        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(context, R.layout.simple_spinner_item_custom, banks);
        adapterMonth.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_custom);

        ZNothingSelectedSpinnerAdapter adapterNothingSelectedMonth = new ZNothingSelectedSpinnerAdapter(
                adapterMonth, R.layout.spinner_item_nothing_selected, context,
                "Select Bank");
        holder.netBankingBanksList.setAdapter(adapterNothingSelectedMonth);
        holder.netBankingBanksList.setPrompt("Select Bank");

        final ArrayList<CustomRadioButton> listCheckBox = new ArrayList<>();
        listCheckBox.add(holder.checkSbi);
        listCheckBox.add(holder.checkHdfc);
        listCheckBox.add(holder.checkIcici);
        listCheckBox.add(holder.checkAxis);
        listCheckBox.add(holder.checkKotak);
        listCheckBox.add(holder.checkYes);

        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                CustomRadioButton radioButton = (CustomRadioButton) group.findViewById(id);
                if (!radioButton.isChecked())
                    return;
                switch (id) {
                    case R.id.statebankofindia:
                        selectSpinnerItemFromNetBank(holder, 0);
                        break;
                    case R.id.hdfcbank:
                        selectSpinnerItemFromNetBank(holder, 1);
                        break;
                    case R.id.icicibank:
                        selectSpinnerItemFromNetBank(holder, 2);
                        break;
                    case R.id.axisbank:
                        selectSpinnerItemFromNetBank(holder, 3);
                        break;
                    case R.id.kotakmahindrabank:
                        selectSpinnerItemFromNetBank(holder, 4);
                        break;
                    case R.id.yesbank:
                        selectSpinnerItemFromNetBank(holder, 5);
                        break;
                }
            }
        });

        holder.netBankingBanksList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String bankCodeSelected = payuResponse.getNetBanks().get(position - 1).getBankCode();
                    for (int i = 0; i < bankCodesFixed.length; i++) {
                        if (bankCodeSelected.equalsIgnoreCase(bankCodesFixed[i])) {
                            listCheckBox.get(i).setChecked(true);
                        } else {
                            listCheckBox.get(i).setChecked(false);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void selectSpinnerItemFromNetBank(NetBankingHolder holder, int pos) {
        int positionToSelect = -1;
        for (int i = 0; i < payuResponse.getNetBanks().size(); i++) {
            if (payuResponse.getNetBanks().get(i).getBankCode().equalsIgnoreCase(bankCodesFixed[pos])) {
                positionToSelect = i;
                break;
            }
        }
        holder.netBankingBanksList.setSelection(positionToSelect + 1);
    }

    private void addFunctionalityForSpinners(Spinner monthSpinner, Spinner yearSpinner) {
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        ArrayAdapter<String> adapterMonth = new ArrayAdapter<>(context, R.layout.simple_spinner_item_custom, months);
        adapterMonth.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_custom);

        ZNothingSelectedSpinnerAdapter adapterNothingSelectedMonth = new ZNothingSelectedSpinnerAdapter(
                adapterMonth, R.layout.spinner_item_nothing_selected, context,
                "MM");
        monthSpinner.setAdapter(adapterNothingSelectedMonth);
        monthSpinner.setPrompt("MM");

        List<Integer> yearsList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        for (int i = currentYear; i < currentYear + 50; i++) {
            yearsList.add(i);
        }
        ArrayAdapter<Integer> adapterYear = new ArrayAdapter<>(context, R.layout.simple_spinner_item_custom, yearsList);
        adapterYear.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_custom);

        ZNothingSelectedSpinnerAdapter adapterNothingSelectedYear = new ZNothingSelectedSpinnerAdapter(
                adapterYear, R.layout.spinner_item_nothing_selected, context,
                "YYYY");
        yearSpinner.setAdapter(adapterNothingSelectedYear);
        yearSpinner.setPrompt("YYYY");
    }

    private void addFunctionalityForCardNumberTextChanged(final EditText cardNumber, final ImageView cartNumberTypeImage, final EditText cvv) {
        cardNumber.addTextChangedListener(new TextWatcher() {
            int image;
            int cardLength = 20;
            private String ccNumber = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 6) {
                    // to confirm rupay card we need min 6 digit.
                    if (null == issuer)
                        issuer = payuUtils.getIssuer(charSequence.toString().replace(" ", ""));
                    if (issuer != null && issuer.length() > 1) {
                        image = getIssuerImage(issuer);
                        cartNumberTypeImage.setImageResource(image);
                        if (issuer == "AMEX")
                            cvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                        else
                            cvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

                        if (issuer == "SMAE" || issuer == "MAES") {
                            cardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(23)});
                            cardLength = 23;
                        } else if (issuer == "AMEX") {
                            cardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
                            cardLength = 18;
                        } else if (issuer == "DINR") {
                            cardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(17)});
                            cardLength = 17;
                        } else {
                            cardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
                            cardLength = 20;
                        }
                    }
                } else {
                    cardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(23)});
                    cardLength = 23;
                    issuer = null;
                    cartNumberTypeImage.setImageResource(R.drawable.icon_card);
                    cvv.getText().clear();
                    cvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                }

//                if (charSequence.length() == 7) {
//                    if (valueAddedHashMap.get(charSequence.toString().replace(" ", "")) != null) {
//                        int statusCode = valueAddedHashMap.get(charSequence.toString().replace(" ", "")).getStatusCode();
//
//                        if (statusCode == 0) {
//                            issuingBankDown.setVisibility(View.VISIBLE);
//                            issuingBankDown.setText(valueAddedHashMap.get(charSequence.toString().replace(" ", "")).getBankName() + " is temporarily down");
//                        } else {
//                            issuingBankDown.setVisibility(View.GONE);
//                        }
//                    }
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString();
                int flag = 0;
                if (ccNumber.length() < s.length()) {
                    switch (s.length()) {
                        case 5:
                            temp = s.toString().substring(0, 4) + " " + s.toString().substring(4);
                            flag = 1;
                            break;
                        case 10:
                            flag = 1;
                            temp = s.toString().substring(0, 9) + " " + s.toString().substring(9);
                            break;
                        case 15:
                            flag = 1;
                            temp = s.toString().substring(0, 14) + " " + s.toString().substring(14);
                            break;
                        case 17:
                            if (issuer == "DINR") {
                                cardValidation(cardNumber, cartNumberTypeImage, cvv);
                            }
                            break;
                        case 18:
                            if (issuer == "AMEX") {
                                cardValidation(cardNumber, cartNumberTypeImage, cvv);
                            }
                            break;
                        case 20:
                            temp = s.toString().substring(0, 19) + " " + s.toString().substring(19);
                            flag = 1;
                            break;
                        case 19:
                            cardValidation(cardNumber, cartNumberTypeImage, cvv);
                            break;
                        case 23:
                            cardValidation(cardNumber, cartNumberTypeImage, cvv);
                            break;
                    }
                } else if (ccNumber.length() >= s.length()) {
                    switch (s.length()) {
                        case 5:
                            temp = s.toString().substring(0, 4);
                            flag = 1;
                            break;
                        case 10:
                            temp = s.toString().substring(0, 9);
                            flag = 1;
                            break;
                        case 15:
                            temp = s.toString().substring(0, 14);
                            flag = 1;
                            break;
                        case 20:
                            temp = s.toString().substring(0, 19);
                            flag = 1;
                            break;
                    }
                }

                ccNumber = s.toString();

                if (flag == 1) {
                    flag = 0;
                    cardNumber.setText(temp);
                    cardNumber.setSelection(cardNumber.length());
                }
            }

        });
    }

    public void cardValidation(EditText cardNumber, ImageView cardNumberTypeImage, EditText cvv) {
        if (!(payuUtils.validateCardNumber(cardNumber.getText().toString().replace(" ", ""))) && cardNumber.length() > 0) {
            cardNumberTypeImage.setImageResource(R.drawable.error_icon);
//            isCardNumberValid = false;
        } else {
//            isCardNumberValid = true;
        }
    }

    public void deletedSavedCardSuccessfully(PayuResponse payuResponse) {
        this.payuResponse.getStoredCards().remove(deleteItemPosition);
        currentOpenSavedCardPos = 0;
        notifyItemChanged(0);
    }

    class SavedCardsHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutHideContent, paymentModeTextLayout, orderDetailsLayout;
        TextView viewOrderDetailsButton;
        TextView totalPayment, orderTotal, shipping, totalSum, discountValue;
        LinearLayout discountLayout;
        LinearLayout mainSavedCardsBgLayout, savedCardsDynamicContainer;

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
            discountValue = (TextView) v.findViewById(R.id.discount);
            mainSavedCardsBgLayout = (LinearLayout) v.findViewById(R.id.savedcardmainbglayout);
            savedCardsDynamicContainer = (LinearLayout) v.findViewById(R.id.savedcardsdynamic);
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
        CustomRadioButton checkHdfc, checkIcici, checkSbi, checkAxis, checkYes, checkKotak;
        Spinner netBankingBanksList;
        RadioGroup radioGroup;

        public NetBankingHolder(View v) {
            super(v);
            layoutHideContent = (LinearLayout) v.findViewById(R.id.hidethispaymentlayut);
            paymentModeTextLayout = (LinearLayout) v.findViewById(R.id.creditcardpaymentoptiontext);
            paySecurely = (Button) v.findViewById(R.id.paysecurely__codcod);
            netBankingBanksList = (Spinner) v.findViewById(R.id.netbankingbankslist);
            checkAxis = (CustomRadioButton) v.findViewById(R.id.axisbank);
            checkHdfc = (CustomRadioButton) v.findViewById(R.id.hdfcbank);
            checkIcici = (CustomRadioButton) v.findViewById(R.id.icicibank);
            checkSbi = (CustomRadioButton) v.findViewById(R.id.statebankofindia);
            checkYes = (CustomRadioButton) v.findViewById(R.id.yesbank);
            checkKotak = (CustomRadioButton) v.findViewById(R.id.kotakmahindrabank);
            radioGroup = (RadioGroup) v.findViewById(R.id.cardRadioGroup);
        }
    }

    class CODHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutHideContent, paymentModeTextLayout;
        TextView codAmountText;
        Button paySecurely;
        LinearLayout codEligibleLayout, codNotEligibleLayout;
        TextView codNotEligibletext;

        public CODHolder(View v) {
            super(v);
            codAmountText = (TextView) v.findViewById(R.id.codamountteztz);
            paySecurely = (Button) v.findViewById(R.id.paysecurely__codcod);
            layoutHideContent = (LinearLayout) v.findViewById(R.id.hidethispaymentlayut);
            paymentModeTextLayout = (LinearLayout) v.findViewById(R.id.creditcardpaymentoptiontext);
            codEligibleLayout = (LinearLayout) v.findViewById(R.id.cashdeleligible);
            codNotEligibleLayout = (LinearLayout) v.findViewById(R.id.cashondevlirtklnvxvklkl);
            codNotEligibletext = (TextView) v.findViewById(R.id.cashondevliverynotavailtext);
        }
    }

    class CreditCardHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutHideContent, paymentModeTextLayout;
        TextView creditCardOrDebitCard;
        ImageView crediTordebitImage;
        Button paySecurely;
        ImageView cartNumberTypeImage;
        CheckBox savecardCheck;
        EditText cardNumber, cardName, cvv;
        Spinner monthSpinner, yearSpinner;
        TextView skipCvvAndExpirytext;

        public CreditCardHolder(View v) {
            super(v);
            layoutHideContent = (LinearLayout) v.findViewById(R.id.hidethispaymentlayut);
            paymentModeTextLayout = (LinearLayout) v.findViewById(R.id.creditcardpaymentoptiontext);
            creditCardOrDebitCard = (TextView) v.findViewById(R.id.creditcardtext);
            paySecurely = (Button) v.findViewById(R.id.paysecurely__codcod);
            cardName = (EditText) v.findViewById(R.id.nameOnCardEditText);
            cardNumber = (EditText) v.findViewById(R.id.cardNumberEditText);
            cvv = (EditText) v.findViewById(R.id.cvvEditText);
            crediTordebitImage = (ImageView) v.findViewById(R.id.crefitcardimagel);
            cartNumberTypeImage = (ImageView) v.findViewById(R.id.cartNumberTypeImage);
            monthSpinner = (Spinner) v.findViewById(R.id.monthspinnercreditcard);
            yearSpinner = (Spinner) v.findViewById(R.id.yearspinnercreditcard);
            skipCvvAndExpirytext = (TextView) v.findViewById(R.id.maestrodebitcardskipcvv);
            savecardCheck = (CheckBox) v.findViewById(R.id.savecreditcard);
        }
    }

    @Override
    public int getItemCount() {
        if (toShowEmiItem)
            return 6;
        else return 5;
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
                        String cardNumber = holder.cardNumber.getText().toString().trim().replace(" ", "");
                        String expiryMonth = null;
                        if (holder.monthSpinner.getSelectedItemPosition() != 0)
                            expiryMonth = holder.monthSpinner.getSelectedItem().toString();
                        String expiryYear = null;
                        if (holder.yearSpinner.getSelectedItemPosition() != 0)
                            expiryYear = holder.yearSpinner.getSelectedItem().toString();
                        ((NewAppPaymentOptionsActivity) context).openPayUWebViewForCreditCard(cardNumber, holder.cardName.getText().toString().trim(), expiryMonth, expiryYear, holder.cvv.getText().toString().trim(), holder.savecardCheck.isChecked());
                    } else if (getItemViewType(pos) == TYPE_DEBIT_CARD) {
                        CreditCardHolder holder = (CreditCardHolder) v.getTag(R.integer.z_tag_holder_recycler_view);
                        String cardNumber = holder.cardNumber.getText().toString().trim().replace(" ", "");
                        String expiryMonth = null;
                        if (holder.monthSpinner.getSelectedItemPosition() != 0)
                            expiryMonth = holder.monthSpinner.getSelectedItem().toString();
                        String expiryYear = null;
                        if (holder.yearSpinner.getSelectedItemPosition() != 0)
                            expiryYear = holder.yearSpinner.getSelectedItem().toString();
                        ((NewAppPaymentOptionsActivity) context).openPayUWebViewForCreditCard(cardNumber, holder.cardName.getText().toString().trim(), expiryMonth, expiryYear, holder.cvv.getText().toString().trim(), holder.savecardCheck.isChecked());
                    } else if (getItemViewType(pos) == TYPE_NET_BANKING) {
                        NetBankingHolder holder = (NetBankingHolder) v.getTag(R.integer.z_tag_holder_recycler_view);
                        if (holder.netBankingBanksList.getSelectedItemPosition() == -1) {
                            Toast.makeText(context, "Please select a bank", Toast.LENGTH_SHORT).show();
                        } else {
                            String bankCode = payuResponse.getNetBanks().get(holder.netBankingBanksList.getSelectedItemPosition() - 1).getBankCode();
                            ((NewAppPaymentOptionsActivity) context).openPaymentUsingNetBanking(bankCode);
                        }
                    }
                    break;
                case R.id.deletesavecard:
                    pos = (int) v.getTag();
                    deleteItemPosition = pos;
                    ((NewAppPaymentOptionsActivity) context).deleteSavedCardAskForConfirmation(payuResponse.getStoredCards().get(deleteItemPosition));
                    break;
                case R.id.radiobuttonsavedcard:
                    pos = (int) v.getTag();
                    currentOpenSavedCardPos = pos;
                    notifyItemChanged(0);
                    break;
                case R.id.paysecurelysavedcardl:
                    CustomEdittext cvv = (CustomEdittext) v.getTag();
                    if (cvv.getText().toString().trim().length() == 0) {
                        Toast.makeText(context, "Enter CVV for saved card", Toast.LENGTH_SHORT).show();
                    } else
                        ((NewAppPaymentOptionsActivity) context).makePaymentUsingSavedCard(payuResponse.getStoredCards().get(currentOpenSavedCardPos), cvv.getText().toString().trim());
                    break;
            }
        }
    }

    private int getIssuerImage(String issuer) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            switch (issuer) {
                case PayuConstants.VISA:
                    return R.drawable.logo_visa;
                case PayuConstants.LASER:
                    return R.drawable.laser;
                case PayuConstants.DISCOVER:
                    return R.drawable.discover;
                case PayuConstants.MAES:
                    return R.drawable.mas_icon;
                case PayuConstants.MAST:
                    return R.drawable.mc_icon;
                case PayuConstants.AMEX:
                    return R.drawable.amex;
                case PayuConstants.DINR:
                    return R.drawable.diner;
                case PayuConstants.JCB:
                    return R.drawable.jcb;
                case PayuConstants.SMAE:
                    return R.drawable.maestro;
                case PayuConstants.RUPAY:
                    return R.drawable.rupay;
//                TODO ask Franklin for rupay regex
            }
            return 0;
        } else {

            switch (issuer) {
                case PayuConstants.VISA:
                    return R.drawable.logo_visa;
                case PayuConstants.LASER:
                    return R.drawable.laser;
                case PayuConstants.DISCOVER:
                    return R.drawable.discover;
                case PayuConstants.MAES:
                    return R.drawable.mas_icon;
                case PayuConstants.MAST:
                    return R.drawable.mc_icon;
                case PayuConstants.AMEX:
                    return R.drawable.amex;
                case PayuConstants.DINR:
                    return R.drawable.diner;
                case PayuConstants.JCB:
                    return R.drawable.jcb;
                case PayuConstants.SMAE:
                    return R.drawable.maestro;
                case PayuConstants.RUPAY:
                    return R.drawable.rupay;
                //TODO ask Franklin
            }
            return 0;
        }
    }
}
