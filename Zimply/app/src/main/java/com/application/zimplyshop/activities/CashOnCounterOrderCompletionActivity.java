package com.application.zimplyshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.CocOrderCompletionAdapter;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.baseobjects.CartObject;
import com.application.zimplyshop.widgets.OrderCompletionItemDecoration;

/**
 * Created by Umesh Lohani on 12/10/2015.
 */
public class CashOnCounterOrderCompletionActivity extends BaseActivity{

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_toolbar_layout);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView)findViewById(R.id.categories_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new OrderCompletionItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_medium)));
        setLoadingVariables();
        showView();
        CartObject cartObj =(CartObject)  getIntent().getSerializableExtra("cart_obj");
        String orderId = getIntent().getStringExtra("order_id");
        AddressObject addressObject = (AddressObject)getIntent().getSerializableExtra("address_obj");
        boolean isCoc = getIntent().getBooleanExtra("is_coc",false);
        CocOrderCompletionAdapter adapter = new CocOrderCompletionAdapter(this,cartObj,orderId,isCoc,addressObject);
        recyclerView.setAdapter(adapter);
    }

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, toolbar, false);
        TextView titleText = (TextView) view.findViewById(R.id.title_textview);
        titleText.setText("Order Summary");
        toolbar.addView(view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            moveToHomeActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    public void moveToHomeActivity(){
        Intent intent = new Intent(this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        this.finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        moveToHomeActivity();
        super.onBackPressed();
    }
}
