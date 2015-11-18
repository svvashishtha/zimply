package com.application.zimplyshop.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.application.zimplyshop.R;
import com.application.zimplyshop.extras.InputFilterMinMax;
import com.application.zimplyshop.widgets.RangeSeekBar;

/**
 * Created by Umesh Lohani on 10/12/2015.
 */
public class ProductPriceFilterFragment extends BaseFragment {

    RangeSeekBar seekBar;

    int sortById;

    int fromPrice,toPrice;

    public static ProductPriceFilterFragment newInstance(Bundle bundle) {
        ProductPriceFilterFragment fragment = new ProductPriceFilterFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.product_price_filter_layout, container, false);
        int sortById = getArguments().getInt("sort_id");
        final int priceHigh = getArguments().getInt("price_high");
        final int priceLow = getArguments().getInt("price_low");

        //sortById = 1;
        if (sortById == 1) {
            view.findViewById(R.id.low_to_high).setSelected(true);
        } else {
            view.findViewById(R.id.high_to_low).setSelected(true);
        }

        ((EditText) view.findViewById(R.id.from_price)).setText(priceLow + "");
        ((EditText) view.findViewById(R.id.to_price)).setText(priceHigh + "");
        setSortByClicks();
        /*((EditText) view.findViewById(R.id.from_price)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {
                    int number = -1;
                    try {
                        number = Integer.parseInt(s.toString());
                    } catch(NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if ( number > priceHigh ) {
                        Toast.makeText(getActivity(), "Maximum price is Rs." + priceHigh, Toast.LENGTH_SHORT).show();
                        ((EditText) view.findViewById(R.id.from_price)).setText(priceLow + "");
                        ((EditText) view.findViewById(R.id.from_price)).setSelection(((EditText) view.findViewById(R.id.from_price)).getText().length());
                    }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        ((EditText) view.findViewById(R.id.to_price)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        if (Double.parseDouble(((EditText) view.findViewById(R.id.to_price)).getText().toString()) < Double.parseDouble(((EditText) view.findViewById(R.id.from_price)).getText().toString())) {
                            ((EditText) view.findViewById(R.id.to_price)).setText(((EditText) view.findViewById(R.id.from_price)).getText());
                        }
                    } catch (Exception e) {
                        ((EditText) view.findViewById(R.id.to_price)).setText("50000");
                    }
                }
            }
        });


        ((EditText) view.findViewById(R.id.from_price)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    try{
                        if(Double.parseDouble(((EditText) view.findViewById(R.id.from_price)).getText().toString())>Double.parseDouble(((EditText) view.findViewById(R.id.to_price)).getText().toString())){
                            ((EditText) view.findViewById(R.id.from_price)).setText(((EditText) view.findViewById(R.id.to_price)).getText());
                        }
                    }catch(Exception e){
                        ((EditText) view.findViewById(R.id.from_price)).setText("1");
                    }
                }
            }
        });


   /*     ((EditText) view.findViewById(R.id.to_price)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Float.parseInt(((EditText) view.findViewById(R.id.from_price)).getText())))
            }
        });*/

        seekBar = (RangeSeekBar<Integer>) view.findViewById(R.id.range_seekbar);
        seekBar.setNotifyWhileDragging(true);
        seekBar.setSelectedMaxValue(priceHigh);
        seekBar.setSelectedMinValue(priceLow);
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                setMinEditTextValue(minValue);
                setMaxEdittextValue(maxValue);
            }
        });
        //addMinMaxValueCheckForMinValue();

        return view;
    }

    public void setSortByClicks() {
        view.findViewById(R.id.low_to_high).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortById = 1;
                view.findViewById(R.id.low_to_high).setSelected(true);
                view.findViewById(R.id.high_to_low).setSelected(false);
            }
        });
        view.findViewById(R.id.high_to_low).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortById = 2;
                view.findViewById(R.id.low_to_high).setSelected(false);
                view.findViewById(R.id.high_to_low).setSelected(true);
            }
        });
    }

    public void addMinMaxValueCheckForMinValue() {
        int minValue = Integer.parseInt(((EditText) view.findViewById(R.id.from_price)).getText().toString().length() > 0 ? ((EditText) view.findViewById(R.id.from_price)).getText().toString() : "1");
        int maxValue = Integer.parseInt((((EditText) view.findViewById(R.id.to_price)).getText().toString().length() > 0) ? ((EditText) view.findViewById(R.id.to_price)).getText().toString() : "50000");
        // ((EditText) view.findViewById(R.id.from_price)).setFilters(new InputFilter[]{new InputFilterMinMax("0", (maxValue - 1) + "")});
        ((EditText) view.findViewById(R.id.to_price)).setFilters(new InputFilter[]{new InputFilterMinMax((minValue + 1) + "", "50000")});
    }

    public void setMinEditTextValue(int value) {
        ((EditText) view.findViewById(R.id.from_price)).setText(value + "");

    }

    public void setMaxEdittextValue(int value) {
        ((EditText) view.findViewById(R.id.to_price)).setText(value + "");
    }

    public String getSelectedMinValue() {
        return ((EditText) view.findViewById(R.id.from_price)).getText().toString();
    }

    public String getSelectedMaxValue() {
        return ((EditText) view.findViewById(R.id.to_price)).getText().toString();
    }

    public int getSortById() {
        return sortById;
    }


    public void changeSelectedValues() {
        sortById = 1;
        view.findViewById(R.id.low_to_high).setSelected(true);
        view.findViewById(R.id.high_to_low).setSelected(false);
        seekBar.setSelectedMaxValue(50000);
        seekBar.setSelectedMinValue(1);
        ((EditText) view.findViewById(R.id.from_price)).setText(1 + "");
        ((EditText) view.findViewById(R.id.to_price)).setText(50000 + "");
    }
}
