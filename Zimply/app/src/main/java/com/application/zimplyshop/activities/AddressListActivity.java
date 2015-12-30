package com.application.zimplyshop.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.NewAdressSelectionAdapter;
import com.application.zimplyshop.objects.AllUsers;
import com.application.zimplyshop.widgets.SpaceItemDecoration;

public class AddressListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        mContext = AddressListActivity.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.z_text_color_light));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Choose Address");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.category_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_medium)));

        setLoadingVariables();
        showView();
        setAdapterData();
    }


    public void setAdapterData() {
        if (recyclerView.getAdapter() == null) {
            final NewAdressSelectionAdapter adapter = new NewAdressSelectionAdapter(mContext, 2);
            recyclerView.setAdapter(adapter);
            adapter.setOnUseAdressClickListener(new NewAdressSelectionAdapter.OnUseAdressClickListener() {
                @Override
                public void onAddressSelected(int pos) {

                    AllUsers.getInstance().swapAddress(pos);

                    //AddressListActivity.this.finish();
                }

                @Override
                public void addNewAddress() {
                    Intent intent = new Intent(AddressListActivity.this, EditAddressActivity.class);
                    startActivity(intent);
                }

                @Override
                public void editExistingAddress(int pos) {

                    Intent intent = new Intent(AddressListActivity.this, EditAddressActivity.class);
                    intent.putExtra("edit_position",pos);
                    intent.putExtra("addressObject",AllUsers.getInstance().getObjs().get(pos));
                    startActivity(intent);
                }
            });
        }

        ((NewAdressSelectionAdapter) recyclerView.getAdapter()).addData(AllUsers.getInstance().getObjs());
    }

}
