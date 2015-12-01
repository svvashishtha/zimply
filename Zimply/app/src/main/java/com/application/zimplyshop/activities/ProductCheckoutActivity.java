package com.application.zimplyshop.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.fragments.AddressSelectionFragment;
import com.application.zimplyshop.fragments.BaseFragment;
import com.application.zimplyshop.fragments.EditAddressFragment;
import com.application.zimplyshop.fragments.MyCartFragment;
import com.application.zimplyshop.fragments.OrderSummaryFragment;
import com.application.zimplyshop.fragments.ZFragment;

public class ProductCheckoutActivity extends BaseActivity {

    public Toolbar toolbar;
    TextView titleText;
    BaseFragment orderSummaryFrag, addressSelectionFrag, myCartFrag, editAddressFragment;
    String ADDRESS_SELECTION = "addressSelection", ORDER_SUMMARY = "orderSummary", EDIT_ADDRESS = "editAddress", CART_TAG = "mycart";

    @Override
    protected void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);

        setContentView(R.layout.single_fragment_container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {

            if (bundle.getBoolean("OrderSummaryFragment")) {
                if (onSaveInstanceState == null) {
                    setOrderSummaryFragmentWithoutBackstack(bundle);
                }
            } else {
                setMyCartFragment(bundle);
            }
        }
    }


    //Without BackStack
    private void setMyCartFragment(Bundle savedInstanceState) {
        myCartFrag = MyCartFragment.newInstance(savedInstanceState);
        titleText.setText("My Cart");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myCartFrag, CART_TAG).commit();
    }

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, toolbar, false);
        titleText = (TextView) view.findViewById(R.id.title_textview);
        toolbar.addView(view);
    }

    public void onFragmentResult(Bundle bundle) {
        if (bundle.containsKey("action") && bundle.getString("action").equals("ADD_ADDRESS")) {
            setResult(RESULT_OK, new Intent().putExtras(bundle));
            finish();
            return;
        }

        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStackImmediate();
            ZFragment zf = (ZFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (zf != null) {
                boolean consumed = zf.onFragmentResult(bundle);
                if (!consumed) {
                    onFragmentResult(bundle);
                }
            } else {
                setResult(Activity.RESULT_OK, new Intent().putExtras(bundle));
                finish();
            }
        } else {
            setResult(Activity.RESULT_OK, new Intent().putExtras(bundle));
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("n", 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public TextView getTitleText() {
        return titleText;
    }

    public void setTitleText(String text) {
        this.titleText.setText(text);
    }

    @Override
    public void onBackPressed() {

        ZFragment zf = (ZFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (zf != null) {
            boolean consumed = zf.onBackPressed();
            if (!consumed) {
                if (getFragmentManager().getBackStackEntryCount() == 0) {
                    super.onBackPressed();
                } else {
                    getFragmentManager().popBackStack();
                    getFragmentManager().executePendingTransactions();
                }
            }

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Bundle b = intent.getExtras();
        if (b != null) {

            if (b.containsKey("TabDetailsFragment") && b.getBoolean("TabDetailsFragment", false)) {
                int tabId = b.getInt("tabId", 0);
                ZFragment zf = (ZFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            }
        }
        super.onNewIntent(intent);
    }

    //Without backstack
    public void setOrderSummaryFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("from_selection")) {
            getSupportFragmentManager().popBackStackImmediate();
            getSupportFragmentManager().executePendingTransactions();
            if (orderSummaryFrag == null) {
                orderSummaryFrag = OrderSummaryFragment.newInstance(savedInstanceState);
            } else
                ((OrderSummaryFragment) orderSummaryFrag).onActivityCreated(savedInstanceState);
            titleText.setText("Order Summary");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, orderSummaryFrag, ORDER_SUMMARY).commit();
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count == 3) {
                getSupportFragmentManager().popBackStackImmediate();
                getSupportFragmentManager().popBackStackImmediate();
                getSupportFragmentManager().executePendingTransactions();
                if (savedInstanceState.containsKey("address")) {
                    Bundle bundle = savedInstanceState;
                    bundle.putSerializable("addressShipping", (AddressObject) savedInstanceState.getSerializable("address"));

                    if (orderSummaryFrag == null) {
                        orderSummaryFrag = OrderSummaryFragment.newInstance(bundle);
                    } else
                        ((OrderSummaryFragment) orderSummaryFrag).onActivityCreated(bundle);

                    titleText.setText("Order Summary");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, orderSummaryFrag, ORDER_SUMMARY).addToBackStack(null).commit();
                }
            } else {
                if (count == 2) {
                    getSupportFragmentManager().popBackStackImmediate();
                    Bundle bundle = savedInstanceState;
                    bundle.putSerializable("addressShipping", (AddressObject) savedInstanceState.getSerializable("address"));

                    if (orderSummaryFrag == null) {
                        orderSummaryFrag = OrderSummaryFragment.newInstance(bundle);
                    } else
                        ((OrderSummaryFragment) orderSummaryFrag).onActivityCreated(bundle);

                    titleText.setText("Order Summary");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, orderSummaryFrag, ORDER_SUMMARY).commit();


                } else {
                    if (count == 0 || count==1) {
                        Bundle bundle = savedInstanceState;
                        bundle.putSerializable("addressShipping", (AddressObject) savedInstanceState.getSerializable("address"));

                        if (orderSummaryFrag == null) {
                            orderSummaryFrag = OrderSummaryFragment.newInstance(bundle);
                        } else {
                            ((OrderSummaryFragment) orderSummaryFrag).onActivityCreated(bundle);
                        }
                        titleText.setText("Order Summary");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, orderSummaryFrag, ORDER_SUMMARY).commit();
                    }
                }
            }
        }
    }

    public void setOrderSummaryFragmentWithBackstack(Bundle savedInstanceState) {
        orderSummaryFrag = OrderSummaryFragment.newInstance(savedInstanceState);
        titleText.setText("Order Summary");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, orderSummaryFrag, ORDER_SUMMARY).addToBackStack(null).commit();
    }

    public void setOrderSummaryFragmentWithoutBackstack(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("from_checkout", true);
        orderSummaryFrag = OrderSummaryFragment.newInstance(savedInstanceState);
        titleText.setText("Order Summary");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, orderSummaryFrag, ORDER_SUMMARY).commit();
    }

    public void setAddressSelectionFragmentWithBackstack(Bundle savedInstanceState) {
        addressSelectionFrag = AddressSelectionFragment.newInstance(savedInstanceState);
        titleText.setText("Address Selection");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, addressSelectionFrag, ADDRESS_SELECTION).addToBackStack(null).commitAllowingStateLoss();
    }

    public void setEditAddressSelectionFragmentWithoutStack(Bundle savedInstanceState) {
        editAddressFragment = EditAddressFragment.newInstance(savedInstanceState);
        titleText.setText("Address Selection");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, editAddressFragment, EDIT_ADDRESS).commit();
    }

    public void setEditAddressFragmentWithBackstack(Bundle savedInstanceState) {
        editAddressFragment = EditAddressFragment.newInstance(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getSerializable("addressObject") != null)
            titleText.setText("Edit Address");
        else
            titleText.setText("Add Address");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, editAddressFragment, EDIT_ADDRESS).addToBackStack(null).commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
       /* if (requestCode == PayU.RESULT) {
            if (orderSummaryFrag != null) {
                orderSummaryFrag.onActivityResult(requestCode, resultCode, intent);
            }
        }*/

        super.onActivityResult(requestCode, resultCode, intent);
    }
}
