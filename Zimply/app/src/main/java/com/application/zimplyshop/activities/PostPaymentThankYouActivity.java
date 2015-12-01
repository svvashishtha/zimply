package com.application.zimplyshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.AddressObject;


/**
 * Thanking Page on Completion of Successful Order
 *
 * @author Umesh
 */
public class PostPaymentThankYouActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_post_payment_thank_you);

        Bundle bundle = getIntent().getExtras();

        AddressObject addressObject = (AddressObject) bundle.getSerializable("address");

        String addressString = addressObject.getName() + ", " + addressObject.getLine1() +
                ", " + addressObject.getLine2() + ", " + addressObject.getCity() + ", " + addressObject.getPincode();

        TextView priceText = (TextView) findViewById(R.id.amount_paid);

        int paymentType = getIntent().getIntExtra("payment_type",2);

        if(paymentType == 4){
            ((TextView)findViewById(R.id.view_order)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PostPaymentThankYouActivity.this,
                            PurchaseListActivity.class);
                    intent.putExtra("backstack_removed",true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(intent);

                }
            });
        }else{
            ((TextView)findViewById(R.id.view_order)).setVisibility(View.GONE);
        }

        priceText.setText(getString(R.string.Rs)
                + " "
                + (Double.parseDouble(bundle.getString("billing_amount"))));

        TextView name = (TextView) findViewById(R.id.address_name);
        name.setText(addressObject.getName());
        TextView address = (TextView) findViewById(R.id.address);
        address.setText(addressString);
        TextView phnNum = (TextView) findViewById(R.id.phone);
        phnNum.setText("Ph:" + addressObject.getPhone());
        Button continueBtn = (Button) findViewById(R.id.continue_btn);
        continueBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostPaymentThankYouActivity.this,
                        HomeActivity.class);
                finish();
                startActivity(intent);
            }
        });
        // AllProducts.getInstance().setCartCount(0);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        super.onBackPressed();
    }
}
