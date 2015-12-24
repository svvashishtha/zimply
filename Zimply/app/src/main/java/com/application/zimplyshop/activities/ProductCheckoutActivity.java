package com.application.zimplyshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.fragments.BaseFragment;
import com.application.zimplyshop.fragments.EditAddressFragment;
import com.application.zimplyshop.fragments.MyCartFragment;
import com.application.zimplyshop.fragments.NewAddressSelectionFragment;
import com.application.zimplyshop.fragments.OrderSummaryFragment;

public class ProductCheckoutActivity extends BaseActivity {

    public Toolbar toolbar;
    TextView titleText;
    BaseFragment orderSummaryFrag, addressSelectionFrag, myCartFrag, editAddressFragment;
    String ADDRESS_SELECTION = "addressSelection", ORDER_SUMMARY = "orderSummary", EDIT_ADDRESS = "editAddress", CART_TAG = "mycart";



    public static int MY_CART_FRAGMENT = 1,ORDER_SUMMARY_FRAGMENT = 2,ADDRESS_SELECTION_FRAGMENT=3,EDIT_ADDRESS_FRAGMENT=4,NEW_EDIT_ADDRESS_FRAGMENT=5,ADD_FIRST_ADDRESS_FRAGMENT=6;
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
                    newOrderSummaryFragmentWithoutBackStack(bundle);
                }
            } else {
                setMyCartFragment(bundle);
            }
        }
    }





    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, toolbar, false);
        titleText = (TextView) view.findViewById(R.id.title_textview);
        toolbar.addView(view);
    }

    /*public void onFragmentResult(Bundle bundle) {
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
*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
      //  outState.putInt("n", 1);
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


    boolean isBackPressed;

    public boolean isBackPressed() {
        boolean isResult = isBackPressed;
        if(isBackPressed){
            isBackPressed = false;
        }
        return isResult;
    }

    @Override
    public void onBackPressed() {
        isBackPressed = true;
        if(isAddingFirstAddress){

            if(getSupportFragmentManager().getBackStackEntryCount()==2) {
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().popBackStack();
            }else{
                getSupportFragmentManager().popBackStack();
                this.finish();
            }
            isAddingFirstAddress=false;
        }else {
            super.onBackPressed();
        }
      /*  ZFragment zf = (ZFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
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

        }*/
    }

   /* @Override
    protected void onNewIntent(Intent intent) {
        Bundle b = intent.getExtras();
        if (b != null) {

            if (b.containsKey("TabDetailsFragment") && b.getBoolean("TabDetailsFragment", false)) {
                int tabId = b.getInt("tabId", 0);
                ZFragment zf = (ZFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            }
        }
        super.onNewIntent(intent);
    }*/

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
        if(orderSummaryFrag!=null)
            orderSummaryFrag = OrderSummaryFragment.newInstance(savedInstanceState);
        titleText.setText("Order Summary");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, orderSummaryFrag, ORDER_SUMMARY).commit();
    }


    boolean isAddingFirstAddress;

    public void setEditAddressSelectionFragmentWithoutStack(Bundle savedInstanceState) {
        editAddressFragment = EditAddressFragment.newInstance(savedInstanceState);
        isAddingFirstAddress = savedInstanceState.getBoolean("adding_first_fragment");
        // titleText.setText("Address Selection");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, editAddressFragment, EDIT_ADDRESS).addToBackStack(null).commit();
    }

    public void setEditAddressFragmentWithBackstack(Bundle savedInstanceState) {
        editAddressFragment = EditAddressFragment.newInstance(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getSerializable("addressObject") != null)
            titleText.setText("Edit Address");
        else
            titleText.setText("Add Address");
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, editAddressFragment, EDIT_ADDRESS).addToBackStack(null).commit();

    }

    /**
     * Add Order Summary fragment as the first fragment and donot add anything in backstack
     * @param bundle
     */
    public void newOrderSummaryFragmentWithoutBackStack(Bundle bundle){
        if(orderSummaryFrag==null){
            orderSummaryFrag = OrderSummaryFragment.newInstance(bundle);
        }
        // titleText.setText("");
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, orderSummaryFrag, ORDER_SUMMARY).commit();
    }

    /**
     * Add Order Summary fragment as the  fragment and add previos fragment in backstack
     * @param bundle
     */
    public void newOrderSummaryFragmentWithBackStack(Bundle bundle){
        if(orderSummaryFrag==null){
            orderSummaryFrag = OrderSummaryFragment.newInstance(bundle);
        }
      //  titleText.setText("Order Summary");
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, orderSummaryFrag, ORDER_SUMMARY).addToBackStack(null).commit();
    }

    /**
     * MyCart fragment is always added as the first fragment. Nothing is added in the backstack
     * @param savedInstanceState
     */
    private void setMyCartFragment(Bundle savedInstanceState) {
        if(myCartFrag==null)
            myCartFrag = MyCartFragment.newInstance(savedInstanceState);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, myCartFrag, CART_TAG).commit();
    }

    /**
     * Address Selection fragment is added with a backstack
     * @param savedInstanceState
     */
    public void setAddressSelectionFragmentWithBackstack(Bundle savedInstanceState) {
        //   if(addressSelectionFrag==null)
        addressSelectionFrag = NewAddressSelectionFragment.newInstance(savedInstanceState);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, addressSelectionFrag, ADDRESS_SELECTION).addToBackStack(null).commit();
    }

    /**
     * Pop fragment from the backstack
     */
    public void popFromBackStack(int comingFragment){


        if(comingFragment==ORDER_SUMMARY_FRAGMENT){
            getSupportFragmentManager().popBackStack();
            ((OrderSummaryFragment)orderSummaryFrag).updateAddress();
        }else if(comingFragment == EDIT_ADDRESS_FRAGMENT){

            getSupportFragmentManager().popBackStack();
            ((NewAddressSelectionFragment)addressSelectionFrag).updateAddress();
        }else if(comingFragment == NEW_EDIT_ADDRESS_FRAGMENT){
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().popBackStack();
            ((OrderSummaryFragment)orderSummaryFrag).updateAddress();
        }else if(comingFragment == ADD_FIRST_ADDRESS_FRAGMENT){
            isAddingFirstAddress=false;
            getSupportFragmentManager().popBackStack();
            ((OrderSummaryFragment)orderSummaryFrag).updateAddress();
        }
    }

    public void newEditAddressFragmentWithBackstack(Bundle savedInstanceState) {
        editAddressFragment = EditAddressFragment.newInstance(savedInstanceState);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, editAddressFragment, EDIT_ADDRESS).addToBackStack(null).commit();

    }
}
