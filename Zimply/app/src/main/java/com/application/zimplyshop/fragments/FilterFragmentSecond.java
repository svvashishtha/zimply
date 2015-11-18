package com.application.zimplyshop.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.FilterActivity;
import com.application.zimplyshop.objects.ZFilter;

public class FilterFragmentSecond extends BaseFragment implements OnClickListener {

    int width;
    SeekBar seekbar;
    String prefix = "My floor area is ", suffix = "meter";
    int progress_slider_option = 0;
    String selectedMetric;
    EditText cue;
    private View rootView;
    private Activity mActivity;
    private int budget = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.photo_filter_form_second, container,false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        width = mActivity.getWindowManager().getDefaultDisplay().getWidth();
        seekbar = (SeekBar) rootView.findViewById(R.id.seek_bar);
        seekbar.setMax(10000);
        ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
        thumb.getPaint().setColor(getResources().getColor(R.color.z_blue_color));
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getActivity().getResources().getDisplayMetrics());
        thumb.setIntrinsicHeight((int) px);
        thumb.setIntrinsicWidth((int) px);
        seekbar.setThumb(thumb);
        seekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.z_blue_color), PorterDuff.Mode.MULTIPLY);
        setListeners();
        cue = (EditText) rootView.findViewById(R.id.progress_text);
        cue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (Integer.parseInt(s.toString()) > seekbar.getMax()) {
                        Toast.makeText(getActivity(), "Maximum Floor Area is " + seekbar.getMax() + selectedMetric, Toast.LENGTH_SHORT).show();
                        cue.setText(seekbar.getMax() + "");
                        cue.setSelection(cue.getText().length());
                    }
                    seekbar.setProgress(Integer.parseInt(s.toString()));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        selectedMetric = getResources().getString(R.string.unit3_short);
        setCueText();
        // final ObjectAnimator visibilityAnimator = ObjectAnimator.ofFloat(cue,
        // View.ALPHA, 1f, 0f);
        // visibilityAnimator.setDuration(200);
        //	cue.setAlpha(0f);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                /*Rect thumbRect = seekBar.getThumb().getBounds();
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				p.setMargins(thumbRect.left, 0, 0, 0);
				cue.setLayoutParams(p);*/
                setCueText();
                progress_slider_option = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                cue.setAlpha(1f);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                changeToThirdFragment();
            }
        });
        fixSizes();
        setListeners();
    }


    public void setCueText() {
        cue.setText(seekbar.getProgress() + ""/* + selectedMetric*/);
        cue.setSelection(cue.getText().length());
    }


    @Override
    public void onResume() {
        checkBudgetId();
        setSelectedMetric();
        super.onResume();
    }
    public void checkBudgetId(){
        if(budget==1){
            budget = 1;
            rootView.findViewById(R.id.button1).setBackgroundResource(R.color.z_blue_color);
            rootView.findViewById(R.id.button1).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button2).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button2).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button3).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button3).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button4).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button4).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button5).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button5).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
        }else if(budget==2){
            rootView.findViewById(R.id.button2).setBackgroundResource(R.color.z_blue_color);
            rootView.findViewById(R.id.button2).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button1).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button1).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button3).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button3).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button4).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button4).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button5).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button5).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
        }else if(budget==3){
            rootView.findViewById(R.id.button3).setBackgroundResource(R.color.z_blue_color);
            rootView.findViewById(R.id.button3).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button2).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button2).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button1).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button1).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button4).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button4).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button5).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button5).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));

        }else if(budget==4){
            rootView.findViewById(R.id.button4).setBackgroundResource(R.color.z_blue_color);
            rootView.findViewById(R.id.button4).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button2).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button2).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button3).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button3).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button1).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button1).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button5).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button5).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));

        }else if(budget==5) {
            rootView.findViewById(R.id.button5).setBackgroundResource(R.color.z_blue_color);
            rootView.findViewById(R.id.button5).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button2).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button2).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button3).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button3).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button4).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button4).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
            rootView.findViewById(R.id.button1).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button1).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void changeToThirdFragment() {
        if (seekbar.getProgress() != 0) {
            if (budget != -1) {
                ZFilter filterObj = ((FilterActivity) mActivity).getFilter();
                filterObj.setArea(seekbar.getProgress() + "");
                int area_unit = (rootView.findViewById(R.id.button_mt).isSelected()) ? 1 : 2;
                filterObj.setArea_unit(area_unit);
                filterObj.setBudget(budget);
                ((FilterActivity) mActivity).setThirdFragment();
            } else {
                showToast("Please select your budget");
            }
        } else {
            showToast("Please add a floor area");

        }
    }

    public void setSelectedMetric(){
        if(selectedMetric.equalsIgnoreCase(getResources().getString(R.string.unit1_short))){
            rootView.findViewById(R.id.button_ft).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button_mt).setBackgroundResource(R.color.z_blue_color);
        }else{
            rootView.findViewById(R.id.button_mt).setBackgroundResource(R.color.feet_button_background);
            rootView.findViewById(R.id.button_ft).setBackgroundResource(R.color.z_blue_color);
        }
    }

    private void setListeners() {
        rootView.findViewById(R.id.next_bt).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        rootView.findViewById(R.id.button_mt).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMetric = getResources().getString(R.string.unit1_short);
                /*int paddingLeft = rootView.findViewById(R.id.button_ft).getPaddingLeft();
                int paddingTop = rootView.findViewById(R.id.button_ft).getPaddingTop();
                int paddingRight = rootView.findViewById(R.id.button_ft).getPaddingRight();
                int paddingBottom = rootView.findViewById(R.id.button_ft).getPaddingBottom();*/
                rootView.findViewById(R.id.button_ft).setBackgroundResource(R.color.feet_button_background);
                //rootView.findViewById(R.id.button_ft).setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

                //paddingLeft = rootView.findViewById(R.id.button_mt).getPaddingLeft();
                //paddingTop = rootView.findViewById(R.id.button_mt).getPaddingTop();
                //paddingRight = rootView.findViewById(R.id.button_mt).getPaddingRight();
                //paddingBottom = rootView.findViewById(R.id.button_mt).getPaddingBottom();
                rootView.findViewById(R.id.button_mt).setBackgroundResource(R.color.z_blue_color);
                //rootView.findViewById(R.id.button_mt).setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                setCueText();
            }
        });
        rootView.findViewById(R.id.button_ft).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMetric = getResources().getString(R.string.unit3_short);
                /*int paddingLeft = rootView.findViewById(R.id.button_mt).getPaddingLeft();
                int paddingTop = rootView.findViewById(R.id.button_mt).getPaddingTop();
                int paddingRight = rootView.findViewById(R.id.button_mt).getPaddingRight();
                int paddingBottom = rootView.findViewById(R.id.button_mt).getPaddingBottom();
                */
                rootView.findViewById(R.id.button_mt).setBackgroundResource(R.color.feet_button_background);
                //rootView.findViewById(R.id.button_mt).setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

                //paddingLeft = rootView.findViewById(R.id.button_ft).getPaddingLeft();
                //paddingTop = rootView.findViewById(R.id.button_ft).getPaddingTop();
                //paddingRight = rootView.findViewById(R.id.button_ft).getPaddingRight();
                //paddingBottom = rootView.findViewById(R.id.button_ft).getPaddingBottom();
                rootView.findViewById(R.id.button_ft).setBackgroundResource(R.color.z_blue_color);
                //rootView.findViewById(R.id.button_ft).setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                setCueText();
            }
        });

        rootView.findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                budget = 1;
                rootView.findViewById(R.id.button1).setBackgroundResource(R.color.z_blue_color);
                rootView.findViewById(R.id.button1).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button2).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button2).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button3).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button3).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button4).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button4).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button5).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button5).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                changeToThirdFragment();
            }
        });
        rootView.findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                budget = 2;
                rootView.findViewById(R.id.button2).setBackgroundResource(R.color.z_blue_color);
                rootView.findViewById(R.id.button2).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button1).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button1).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button3).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button3).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button4).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button4).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button5).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button5).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                changeToThirdFragment();
            }
        });
        rootView.findViewById(R.id.button3).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                budget = 3;
                rootView.findViewById(R.id.button3).setBackgroundResource(R.color.z_blue_color);
                rootView.findViewById(R.id.button3).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button2).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button2).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button1).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button1).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button4).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button4).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button5).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button5).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                changeToThirdFragment();
            }
        });
        rootView.findViewById(R.id.button4).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                budget = 4;
                rootView.findViewById(R.id.button4).setBackgroundResource(R.color.z_blue_color);
                rootView.findViewById(R.id.button4).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button2).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button2).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button3).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button3).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button1).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button1).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button5).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button5).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                changeToThirdFragment();
            }
        });
        rootView.findViewById(R.id.button5).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                budget = 5;
                rootView.findViewById(R.id.button5).setBackgroundResource(R.color.z_blue_color);
                rootView.findViewById(R.id.button5).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button2).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button2).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button3).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button3).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button4).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button4).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                rootView.findViewById(R.id.button1).setBackgroundResource(R.color.feet_button_background);
                rootView.findViewById(R.id.button1).setPadding((int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small), (int) getResources().getDimension(R.dimen.margin_small));
                changeToThirdFragment();
            }
        });
    }

    private void fixSizes() {
        //rootView.findViewById(R.id.progress_text).setPadding(width / 20, width / 20, width / 20, width / 40);
        /*rootView.findViewById(R.id.projects_label).setPadding(width / 20, width / 20, width / 20, width / 20);

		((LinearLayout.LayoutParams) rootView.findViewById(R.id.budget_label).getLayoutParams()).setMargins(0,
				width / 20, 0, 0);

		rootView.findViewById(R.id.budget_label).setPadding(width / 20, width / 20, width / 20, width / 20);*/

    }

}
