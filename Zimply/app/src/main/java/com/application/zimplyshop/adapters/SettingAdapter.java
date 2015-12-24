package com.application.zimplyshop.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.BaseLoginSignupActivity;
import com.application.zimplyshop.objects.AllProducts;
import com.application.zimplyshop.objects.AllUsers;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.ZTracker;
import com.application.zimplyshop.widgets.CustomButton;
import com.application.zimplyshop.widgets.CustomEdittext;
import com.application.zimplyshop.widgets.CustomTextViewBold;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Saurabh on 19-12-2015.
 */
public class SettingAdapter extends RecyclerView.Adapter {

    Context context;
    int counter = 1, prevCounter = 1;
    ClickListeners mListener;
    private Timer timer;
    String otp;
    private boolean refreshView = false;

    public SettingAdapter(Context context) {
        this.context = context;
    }

    public void setCounter(int counter) {
        prevCounter = this.counter;
        this.counter = counter;
        //notifyDataSetChanged();
    }

    public void setClickListener(ClickListeners mListener) {
        this.mListener = mListener;
    }

    // case 1 when all views are minimised
    // case 2 when number is enetered
    // case 3 when otp is enetered
    // case 4 when password view is visible
    @Override
    public int getItemViewType(int position) {
        if (position == 1)
            return 1;
        else return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new ItemHolderPhone(LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_row, parent, false));
        else
            return new ItemHolderPassword(LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_row_password, parent, false));
    }

    public void setOTP(String otp) {
        this.otp = otp;
        refreshView = false;
        notifyDataSetChanged();
    }

    public void resetTimer(boolean flag) {
        refreshView = flag;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            final ItemHolderPhone itemHolder = (ItemHolderPhone) holder;
            //this view is visible in case 1(all minimised) and case 4(password view is visible)
            if (counter == 1 || counter == 4) {
                prevCounter = 1;
                itemHolder.container.findViewById(R.id.title).setVisibility(View.VISIBLE);
                itemHolder.container.findViewById(R.id.edit_image).setVisibility(View.VISIBLE);
                itemHolder.container.findViewById(R.id.case1layout).setVisibility(View.VISIBLE);
                itemHolder.container.findViewById(R.id.case2layout).setVisibility(View.GONE);
                itemHolder.container.findViewById(R.id.case3layout).setVisibility(View.GONE);
                ((CustomTextViewBold) itemHolder.container.findViewById(R.id.title)).setText("Phone Number");
                ((CustomTextViewBold) itemHolder.container.findViewById(R.id.number)).setText(AppPreferences.getUserPhoneNumber(context));
                ((ImageView) itemHolder.container.findViewById(R.id.edit_image)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.editNumber();
                    }
                });
                // this view is visible when edit option for phone number is selected
            } else if (counter == 2) {
                itemHolder.container.findViewById(R.id.case2layout).setVisibility(View.VISIBLE);
                itemHolder.container.findViewById(R.id.case1layout).setVisibility(View.GONE);
                itemHolder.container.findViewById(R.id.case3layout).setVisibility(View.GONE);
                ((CustomEdittext) itemHolder.container.findViewById(R.id.phone_number)).setText(AppPreferences.getUserPhoneNumber(context));
                itemHolder.container.findViewById(R.id.verify_number_progress).setVisibility(View.GONE);
                itemHolder.container.findViewById(R.id.verify_button).setVisibility(View.VISIBLE);
                itemHolder.container.findViewById(R.id.verify_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onPhoneNumberVerify(((CustomEdittext) itemHolder.container.findViewById(R.id.phone_number))
                                .getText().toString());
                        try {
                            CommonLib.hideKeyBoard((Activity) context, ((CustomEdittext) itemHolder.container.findViewById(R.id.phone_number)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        itemHolder.container.findViewById(R.id.verify_number_progress).setVisibility(View.VISIBLE);
                        itemHolder.container.findViewById(R.id.verify_button).setVisibility(View.GONE);
                    }
                });

                itemHolder.container.findViewById(R.id.cancel_verify).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            CommonLib.hideKeyBoard((Activity) context, ((CustomEdittext) itemHolder.container.findViewById(R.id.phone_number)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mListener.onPhoneNumberCancel();
                    }
                });
                android.os.Handler handler = new android.os.Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((CustomEdittext) itemHolder.container.findViewById(R.id.phone_number)).requestFocus();
                        ((CustomEdittext) itemHolder.container.findViewById(R.id.phone_number)).setSelection(((CustomEdittext) itemHolder.container.findViewById(R.id.phone_number)).getText().length());
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(((CustomEdittext) itemHolder.container.findViewById(R.id.phone_number)), InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 500);
                //// this view is visible when otp is to be verified
            } else if (counter == 3) {

                itemHolder.container.findViewById(R.id.case1layout).setVisibility(View.GONE);
                itemHolder.container.findViewById(R.id.case2layout).setVisibility(View.GONE);
                itemHolder.container.findViewById(R.id.case3layout).setVisibility(View.VISIBLE);
                ((CustomEdittext) itemHolder.container.findViewById(R.id.otp)).setText("");
                if (refreshView) {
                    CommonLib.ZLog("SettingAdapter", "timer started");
                    seconds = 60;
                    startTimer((CustomButton) itemHolder.container.findViewById(R.id.resend_code));
                }
                itemHolder.container.findViewById(R.id.cancel_otp).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onOtpVerifyCancel();
                    }
                });
                itemHolder.container.findViewById(R.id.edit_number).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.editNumber();
                    }
                });
                itemHolder.container.findViewById(R.id.resend_code).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.resendOtp();
                    }
                });
                ((CustomEdittext) itemHolder.container.findViewById(R.id.otp)).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().trim().length() == 6) {
                            try {
                                //((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mVerificationCodeEditText.getRootView().getWindowToken(), 0);
                                CommonLib.hideKeyBoard((Activity) context, ((CustomEdittext) itemHolder.container.findViewById(R.id.otp)));
                            } catch (Exception e) {
                            }
                            //CommonLib.hideKeyBoard(getActivity(), getView.findViewById(R.id.verification_code));
                            mListener.verifyOtp(s.toString());
                        }
                    }
                });
                if (otp != null && otp.length() > 0) {
                    ((CustomEdittext) itemHolder.container.findViewById(R.id.otp)).setText(otp);
                    otp = "";
                }
            }

        } else if (position == 1) {
            final ItemHolderPassword itemHolderPassword = (ItemHolderPassword) holder;
            // this view is visible in all of cases 1 2 and 3
            // all of these are phone number related views
            if (counter == 1 || counter == 2 || counter == 3) {
                itemHolderPassword.container.findViewById(R.id.case1layout).setVisibility(View.VISIBLE);
                itemHolderPassword.container.findViewById(R.id.case4layout).setVisibility(View.GONE);
                itemHolderPassword.container.findViewById(R.id.edit_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setCounter(4);
                        notifyItemChanged(1);
                        notifyItemChanged(0);
                    }
                });

            } else if (counter == 4) {
                // when edit password is selected

                itemHolderPassword.container.findViewById(R.id.case1layout).setVisibility(View.GONE);
                itemHolderPassword.container.findViewById(R.id.case4layout).setVisibility(View.VISIBLE);
                itemHolderPassword.newPassword.setText("");
                itemHolderPassword.oldpassword.setText("");
                itemHolderPassword.confirmPassword.setText("");
                itemHolderPassword.container.findViewById(R.id.cancel_verify).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setCounter(1);
                        notifyItemChanged(1);
                        try {
                            CommonLib.hideKeyBoard((Activity) context, itemHolderPassword.oldpassword);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            CommonLib.hideKeyBoard((Activity) context, itemHolderPassword.newPassword);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            CommonLib.hideKeyBoard((Activity) context, itemHolderPassword.newPassword);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                itemHolderPassword.container.findViewById(R.id.change_password).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (itemHolderPassword.newPassword.getText().toString().trim().length() > 0 &&
                                itemHolderPassword.oldpassword.getText().toString().trim().length() > 0 &&
                                itemHolderPassword.confirmPassword.getText().toString().trim().length() > 0) {

                            if (itemHolderPassword.newPassword.getText().toString()
                                    .equalsIgnoreCase(itemHolderPassword.confirmPassword.getText().toString())) {
                                try {
                                    CommonLib.hideKeyBoard((Activity) context, itemHolderPassword.oldpassword);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    CommonLib.hideKeyBoard((Activity) context, itemHolderPassword.newPassword);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    CommonLib.hideKeyBoard((Activity) context, itemHolderPassword.newPassword);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                //send request via activity
                                mListener.sendPasswordRequest(itemHolderPassword.newPassword.getText().toString(),
                                        itemHolderPassword.oldpassword.getText().toString());
                            } else {
                                Toast.makeText(context, "Passwords must match", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "All feilds are important", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } else if (position == 2) {
            //this view is constant. all other views will be minimised when this is selected
            final ItemHolderPhone itemHolder = (ItemHolderPhone) holder;
            itemHolder.container.findViewById(R.id.case1layout).setVisibility(View.VISIBLE);
            itemHolder.container.findViewById(R.id.case2layout).setVisibility(View.GONE);
            itemHolder.container.findViewById(R.id.case3layout).setVisibility(View.GONE);

            itemHolder.container.findViewById(R.id.title).setVisibility(View.GONE);
            itemHolder.container.findViewById(R.id.edit_image).setVisibility(View.GONE);
            ((CustomTextViewBold) itemHolder.container.findViewById(R.id.number)).setText("Logout");
            itemHolder.container.findViewById(R.id.number).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCounter(1);
                    notifyItemChanged(0);
                    notifyItemChanged(1);
                    ZTracker.logGAEvent(context, "Settings", "Logout", "");
                    final AlertDialog logoutDialog;
                    logoutDialog = new AlertDialog.Builder(context)
                            .setTitle(context.getResources().getString(R.string.logout))
                            .setMessage(context.getResources().getString(R.string.logout_confirm))
                            .setPositiveButton(context.getResources().getString(R.string.logout),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            AppPreferences.setIsUserLogin(context, false);
                                            AppPreferences.setUserID(context, "");
                                            AllProducts.getInstance().setCartCount(0);
                                            AllProducts.getInstance().setCartObjs(null);
                                            AllUsers.getInstance().setObjs(null);
                                            AllProducts.getInstance().setHomeProCatNBookingObj(null);
                                            AllProducts.getInstance().getVendorIds().clear();
                                            Intent loginIntent = new Intent(context, BaseLoginSignupActivity.class);
                                            loginIntent.putExtra("is_logout", true);

                                            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            //loginIntent.putExtra("inside", true);
                                            context.startActivity(loginIntent);
                                        }
                                    }).setNegativeButton(context.getResources().getString(R.string.dialog_cancel),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create();
                    logoutDialog.show();
                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class ItemHolderPhone extends RecyclerView.ViewHolder {
        FrameLayout container;

        public ItemHolderPhone(View itemView) {
            super(itemView);
            container = (FrameLayout) itemView.findViewById(R.id.frame_container);
        }
    }

    class ItemHolderPassword extends RecyclerView.ViewHolder {
        FrameLayout container;
        CustomEdittext oldpassword, newPassword, confirmPassword;

        public ItemHolderPassword(View itemView) {
            super(itemView);
            container = (FrameLayout) itemView.findViewById(R.id.container);
            oldpassword = (CustomEdittext) itemView.findViewById(R.id.old_password);
            newPassword = (CustomEdittext) itemView.findViewById(R.id.new_password);
            confirmPassword = (CustomEdittext) itemView.findViewById(R.id.confirm_password);
        }
    }

    public interface ClickListeners {
        //send otp to this number
        void onPhoneNumberVerify(String number);

        void onPhoneNumberCancel();

        void onOtpVerifyCancel();

        void editNumber();

        void resendOtp();

        void sendPasswordRequest(String newPassword, String oldPassword);

        void verifyOtp(String otp);
    }

    int seconds = 60;

    private void startTimer(final CustomButton resend) {
        if (resend == null)
            return;

        resend.setBackgroundResource(R.drawable.round_corner_borders_zhl);
        resend.setClickable(false);
        resend.setTextColor(context.getResources().getColor(R.color.zhl_darker));
        resend.setText(context.getResources().getString(R.string.retry_in, seconds));
        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (context != null) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            seconds -= 1;
                            if (seconds <= 0) {
                                seconds = 60;
                                resend.setText(context.getResources().getString(R.string.resend_code));
                                resend.setBackgroundResource(R.drawable.z_blue_btn_without_rad_bg);
                                resend.setClickable(true);
                                resend.setTextColor(context.getResources().getColor(R.color.white));
                                timer.cancel();
                            } else {
                                resend.setText(context.getResources().getString(R.string.retry_in, seconds));
                            }


                        }
                    });
                } else {
                    timer.cancel();
                }
            }
        }, 0, 1000);
    }
}
