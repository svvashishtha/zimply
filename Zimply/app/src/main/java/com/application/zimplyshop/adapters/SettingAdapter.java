package com.application.zimplyshop.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.application.zimplyshop.R;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.widgets.CustomButton;
import com.application.zimplyshop.widgets.CustomEdittext;
import com.application.zimplyshop.widgets.CustomTextView;
import com.application.zimplyshop.widgets.CustomTextViewBold;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Saurabh on 19-12-2015.
 */
public class SettingAdapter extends RecyclerView.Adapter {

    Context context;
    int counter = 1;
    ClickListeners mListener;
    private Timer timer;
    String otp;
    private boolean refreshView = false;

    public SettingAdapter(Context context) {
        this.context = context;
    }

    public void setCounter(int counter) {
        this.counter = counter;
        notifyDataSetChanged();
    }

    public void setClickListener(ClickListeners mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getItemViewType(int position) {
      /*  if (counter == 1) {
            return 1;
        } else if (counter == 2) {
            if (position == 0) {
                return 2;
            } else if (position == 1)
                return 1;
        } else if (counter == 3) {
            if (position == 0) {
                return 3;
            } else return 1;
        }
        if (counter == 4) {
            if (position == 1)
                return 4;
            else return 1;
        }
        return 1;*/
        if (position == 0)
            return 0;
        else return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*switch (viewType) {
            case 1:
                return new ItemHolderCase1(LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_number, parent, false));
            case 2:
                return new ItemHolderCase2(LayoutInflater.from(parent.getContext()).inflate(R.layout.change_number_layout, parent, false));
            case 3:
                return new ItemHolderCase3(LayoutInflater.from(parent.getContext()).inflate(R.layout.enter_otp_layout, parent, false));
            case 4:
                return new ItemHolderCase4(LayoutInflater.from(parent.getContext()).inflate(R.layout.password_change_layout, parent, false));
        }
        return new ItemHolderCase1(LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_number, parent, false));*/
        if (viewType == 0)
            return new ItemHolderPhone(LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_row, parent, false));
        else
            return new ItemHolderPassword(LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_row_password, parent, false));
    }

    public void setOTP(String otp) {
        this.otp = otp;

        notifyDataSetChanged();
    }

    public void resetTimer(boolean flag) {
        refreshView = flag;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        /*if (position == 0 && getItemViewType(position) == 1) {
            ItemHolderCase1 holderCase1 = (ItemHolderCase1) holder;
            holderCase1.title.setText("Phone Number");
            holderCase1.subTitle.setText(AppPreferences.getUserPhoneNumber(context));
            holderCase1.editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.editNumber();
                }
            });
        } else if (position == 1 && getItemViewType(position) == 1) {
            ItemHolderCase1 holderCase1 = (ItemHolderCase1) holder;
            holderCase1.title.setText("Password");
            holderCase1.subTitle.setText("***********");
            holderCase1.editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCounter(4);
                }
            });
        } else if (position == 0 && getItemViewType(position) == 2) {

            final ItemHolderCase2 holderCase2 = (ItemHolderCase2) holder;
            holderCase2.phoneNumber.setText(AppPreferences.getUserPhoneNumber(context));
            holderCase2.verifyProgressBar.setVisibility(View.GONE);
            holderCase2.buttonVerify.setVisibility(View.VISIBLE);
            holderCase2.buttonVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPhoneNumberVerify(holderCase2.phoneNumber.getText().toString());
                    try {
                        CommonLib.hideKeyBoard((Activity) context, holderCase2.phoneNumber);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    holderCase2.verifyProgressBar.setVisibility(View.VISIBLE);
                    holderCase2.buttonVerify.setVisibility(View.GONE);
                }
            });
            holderCase2.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPhoneNumberCancel();
                }
            });
        } else if (position == 0 && getItemViewType(position) == 3) {
            final ItemHolderCase3 holderCase3 = (ItemHolderCase3) holder;
            holderCase3.otp.setText("");
            if (refreshView) {
                seconds = 60;
                startTimer(holderCase3);
            }
            holderCase3.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onOtpVerifyCancel();
                }
            });
            holderCase3.editNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.editNumber();
                }
            });
            holderCase3.resend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.resendOtp();
                }
            });
            holderCase3.otp.addTextChangedListener(new TextWatcher() {
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
                            CommonLib.hideKeyBoard((Activity) context, holderCase3.otp);
                        } catch (Exception e) {
                        }
                        //CommonLib.hideKeyBoard(getActivity(), getView.findViewById(R.id.verification_code));
                        mListener.verifyOtp(s.toString());
                    }
                }
            });
            if (otp != null && otp.length() > 0) {
                holderCase3.otp.setText(otp);
            }
        } else if (position == 1 && getItemViewType(position) == 4) {
            ItemHolderCase4 holderCase4 = (ItemHolderCase4) holder;
            holderCase4.cancelVerifyOtp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onOtpVerifyCancel();
                }
            });
            holderCase4.sendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.sendPasswordRequest();
                }
            });
        }*/

        if (position == 0) {
            final ItemHolderPhone itemHolder = (ItemHolderPhone) holder;
            if (counter == 1 || counter == 4) {
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
                        mListener.onPhoneNumberCancel();
                    }
                });

            } else if (counter == 3) {
                itemHolder.container.findViewById(R.id.case1layout).setVisibility(View.GONE);
                itemHolder.container.findViewById(R.id.case2layout).setVisibility(View.GONE);
                itemHolder.container.findViewById(R.id.case3layout).setVisibility(View.VISIBLE);
               /* holderCase3.otp.setText("");*/

                seconds = 60;
                startTimer((CustomButton) itemHolder.container.findViewById(R.id.resend_code));

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
            ItemHolderPassword itemHolderPassword = (ItemHolderPassword) holder;
            if (counter == 1 || counter == 2 || counter == 3) {
                itemHolderPassword.container.findViewById(R.id.case1layout).setVisibility(View.VISIBLE);
                itemHolderPassword.container.findViewById(R.id.case4layout).setVisibility(View.GONE);
                itemHolderPassword.container.findViewById(R.id.edit_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setCounter(4);
                    }
                });

            } else if (counter == 4) {
                itemHolderPassword.container.findViewById(R.id.case1layout).setVisibility(View.GONE);
                itemHolderPassword.container.findViewById(R.id.case4layout).setVisibility(View.VISIBLE);
                itemHolderPassword.container.findViewById(R.id.cancel_verify).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setCounter(1);
                    }
                });
            }
        }


    }

    @Override
    public int getItemCount() {
        return 2;
    }

    //case 1: with both views minimised
    class ItemHolderCase1 extends RecyclerView.ViewHolder {
        CustomTextViewBold title, subTitle;
        ImageView editImage;

        public ItemHolderCase1(View itemView) {
            super(itemView);
            title = (CustomTextViewBold) itemView.findViewById(R.id.title);
            subTitle = (CustomTextViewBold) itemView.findViewById(R.id.number);
            editImage = (ImageView) itemView.findViewById(R.id.edit_image);
        }
    }

    //case 2 : enter phone number to verify. password minimised
    class ItemHolderCase2 extends RecyclerView.ViewHolder {
        CustomTextView cancel;
        CustomEdittext phoneNumber;
        CustomButton buttonVerify;
        ProgressBar verifyProgressBar;

        public ItemHolderCase2(View itemView) {
            super(itemView);
            cancel = (CustomTextView) itemView.findViewById(R.id.cancel_verify);
            buttonVerify = (CustomButton) itemView.findViewById(R.id.verify_button);
            buttonVerify.setVisibility(View.VISIBLE);
            phoneNumber = (CustomEdittext) itemView.findViewById(R.id.phone_number);
            verifyProgressBar = (ProgressBar) itemView.findViewById(R.id.verify_number_progress);
            verifyProgressBar.setVisibility(View.GONE);
        }
    }

    // case 3: verify otp. password minimised
    class ItemHolderCase3 extends RecyclerView.ViewHolder {
        CustomEdittext otp;
        CustomTextView messageNumber, cancel;
        CustomButton resend, editNumber;

        public ItemHolderCase3(View itemView) {
            super(itemView);
            otp = (CustomEdittext) itemView.findViewById(R.id.otp);
            cancel = (CustomTextView) itemView.findViewById(R.id.cancel_otp);
            messageNumber = (CustomTextView) itemView.findViewById(R.id.message_text);
            resend = (CustomButton) itemView.findViewById(R.id.resend_code);
            editNumber = (CustomButton) itemView.findViewById(R.id.edit_number);
        }
    }

    //case 4 : change password. phone number verify.
    class ItemHolderCase4 extends RecyclerView.ViewHolder {
        CustomTextView cancelVerifyOtp;
        CustomButton sendRequest;
        CustomEdittext oldPassword, newPassword, confirmPassword;

        public ItemHolderCase4(View itemView) {
            super(itemView);
            sendRequest = (CustomButton) itemView.findViewById(R.id.change_password);
            cancelVerifyOtp = (CustomTextView) itemView.findViewById(R.id.cancel_verify);
            oldPassword = (CustomEdittext) itemView.findViewById(R.id.old_password);
            newPassword = (CustomEdittext) itemView.findViewById(R.id.new_password);
            confirmPassword = (CustomEdittext) itemView.findViewById(R.id.confirm_password);
        }
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

        public ItemHolderPassword(View itemView) {
            super(itemView);
            container = (FrameLayout) itemView.findViewById(R.id.container);
        }
    }

    public interface ClickListeners {
        //send otp to this number
        void onPhoneNumberVerify(String number);

        void onPhoneNumberCancel();

        void onOtpVerifyCancel();

        void editNumber();

        void resendOtp();

        void sendPasswordRequest();

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
