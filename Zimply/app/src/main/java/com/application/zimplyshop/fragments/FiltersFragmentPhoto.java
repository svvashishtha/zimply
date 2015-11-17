package com.application.zimplyshop.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.zimply.R;

/**
 * Created by Umesh Lohani on 10/8/2015.
 */
public class FiltersFragmentPhoto  extends BaseFragment {


    int sizeId=-1,styleId=-1,budgetId=-1;


    public void setSizeId(int sizeId) {
        this.sizeId = sizeId;
    }

    public void setStyleId(int styleId) {
        this.styleId = styleId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    public static FiltersFragmentPhoto newInstance(Bundle bundle) {
        FiltersFragmentPhoto fragment = new FiltersFragmentPhoto();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.photo_filter_activity_layout,container,false);
        setViewClicks();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setViewClicks(){
        sizeId = getArguments().getInt("size_id");
        styleId = getArguments().getInt("style_id");
        budgetId = getArguments().getInt("budget_id");
        setSizeViewClicks();
        setStyleViewClicks();
        setBudgetViewClicks();
        changeSelectedValues();
    }

    public int getStyleId() {
        return styleId;
    }

    public int getSizeId() {
        return sizeId;
    }

    public int getBudgetId() {
        return budgetId;
    }

    public void resetAllValues(){
        view.findViewById(R.id.budget1).setSelected(false);
        view.findViewById(R.id.budget2).setSelected(false);
        view.findViewById(R.id.budget3).setSelected(false);
        view.findViewById(R.id.budget4).setSelected(false);


        view.findViewById(R.id.style1).setSelected(false);
        view.findViewById(R.id.style2).setSelected(false);
        view.findViewById(R.id.style3).setSelected(false);
        view.findViewById(R.id.style4).setSelected(false);

        view.findViewById(R.id.size1).setSelected(false);
        view.findViewById(R.id.size2).setSelected(false);
        view.findViewById(R.id.size3).setSelected(false);
        view.findViewById(R.id.size4).setSelected(false);

    }
    public void changeSelectedValues(){
        switch (budgetId){
            case 1:
                view.findViewById(R.id.budget1).setSelected(true);
                break;
            case 2:
                view.findViewById(R.id.budget2).setSelected(true);
                break;
            case 3:
                view.findViewById(R.id.budget3).setSelected(true);
                break;
            case 4:
                view.findViewById(R.id.budget4).setSelected(true);
                break;
        }

        switch (styleId){
            case 1:
                view.findViewById(R.id.style1).setSelected(true);
                break;
            case 2:
                view.findViewById(R.id.style2).setSelected(true);
                break;
            case 3:
                view.findViewById(R.id.style3).setSelected(true);
                break;
            case 4:
                view.findViewById(R.id.style4).setSelected(true);
                break;
        }

        switch (sizeId){
            case 1:
                view.findViewById(R.id.size1).setSelected(true);
                break;
            case 2:
                view.findViewById(R.id.size2).setSelected(true);
                break;
            case 3:
                view.findViewById(R.id.size3).setSelected(true);
                break;
            case 4:
                view.findViewById(R.id.size4).setSelected(true);
                break;
        }

    }

    public void setBudgetViewClicks(){
        ((TextView)view.findViewById(R.id.budget1)).setText(getString(R.string.rs_text));
        ((TextView)view.findViewById(R.id.budget2)).setText(getString(R.string.rs_text)+getString(R.string.rs_text));
        ((TextView)view.findViewById(R.id.budget3)).setText(getString(R.string.rs_text)+getString(R.string.rs_text)+getString(R.string.rs_text));
        ((TextView)view.findViewById(R.id.budget4)).setText(getString(R.string.rs_text)+getString(R.string.rs_text)+getString(R.string.rs_text)+getString(R.string.rs_text));

        view.findViewById(R.id.budget1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.findViewById(R.id.budget1).isSelected()) {
                    budgetId = -1;
                    view.findViewById(R.id.budget1).setSelected(false);
                } else {
                    budgetId = 1;
                    view.findViewById(R.id.budget1).setSelected(true);
                }

                view.findViewById(R.id.budget2).setSelected(false);
                view.findViewById(R.id.budget3).setSelected(false);
                view.findViewById(R.id.budget4).setSelected(false);

            }
        });
        view.findViewById(R.id.budget2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.findViewById(R.id.budget2).isSelected()) {
                    budgetId = -1;
                    view.findViewById(R.id.budget2).setSelected(false);
                } else {
                    budgetId = 2;
                    view.findViewById(R.id.budget2).setSelected(true);
                }

                view.findViewById(R.id.budget1).setSelected(false);
                view.findViewById(R.id.budget3).setSelected(false);
                view.findViewById(R.id.budget4).setSelected(false);

            }
        });
        view.findViewById(R.id.budget3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.findViewById(R.id.budget3).isSelected()) {
                    budgetId = -1;
                    view.findViewById(R.id.budget3).setSelected(false);
                } else {
                    budgetId = 3;
                    view.findViewById(R.id.budget3).setSelected(true);
                }

                view.findViewById(R.id.budget2).setSelected(false);
                view.findViewById(R.id.budget1).setSelected(false);
                view.findViewById(R.id.budget4).setSelected(false);

            }
        });

        view.findViewById(R.id.budget4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.findViewById(R.id.budget4).isSelected()) {
                    budgetId = -1;
                    view.findViewById(R.id.budget4).setSelected(false);
                } else {
                    budgetId = 4;
                    view.findViewById(R.id.budget4).setSelected(true);
                }

                view.findViewById(R.id.budget2).setSelected(false);
                view.findViewById(R.id.budget3).setSelected(false);
                view.findViewById(R.id.budget1).setSelected(false);
            }
        });
    }

    public void setStyleViewClicks(){

        view.findViewById(R.id.style1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.findViewById(R.id.style1).isSelected()) {
                    styleId = -1;
                    view.findViewById(R.id.style1).setSelected(false);
                } else {
                    view.findViewById(R.id.style1).setSelected(true);
                    styleId = 1;
                }

                view.findViewById(R.id.style2).setSelected(false);
                view.findViewById(R.id.style3).setSelected(false);
                view.findViewById(R.id.style4).setSelected(false);

            }
        });
        view.findViewById(R.id.style2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.findViewById(R.id.style2).isSelected()) {
                    styleId = -1;
                    view.findViewById(R.id.style2).setSelected(false);
                } else {
                    view.findViewById(R.id.style2).setSelected(true);
                    styleId = 2;
                }

                view.findViewById(R.id.style1).setSelected(false);
                view.findViewById(R.id.style3).setSelected(false);
                view.findViewById(R.id.style4).setSelected(false);

            }
        });
        view.findViewById(R.id.style3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.findViewById(R.id.style3).isSelected()) {
                    styleId = -1;
                    view.findViewById(R.id.style3).setSelected(false);
                } else {
                    view.findViewById(R.id.style3).setSelected(true);
                    styleId = 3;
                }
                view.findViewById(R.id.style1).setSelected(false);
                view.findViewById(R.id.style2).setSelected(false);
                view.findViewById(R.id.style4).setSelected(false);

            }
        });
        view.findViewById(R.id.style4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.findViewById(R.id.style4).isSelected()) {
                    styleId = -1;
                    view.findViewById(R.id.style4).setSelected(false);
                } else {
                    view.findViewById(R.id.style4).setSelected(true);
                    styleId = 4;
                }

                view.findViewById(R.id.style2).setSelected(false);
                view.findViewById(R.id.style3).setSelected(false);
                view.findViewById(R.id.style1).setSelected(false);

            }
        });

    }

    public void setSizeViewClicks(){

        view.findViewById(R.id.size1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.findViewById(R.id.size1).isSelected()) {
                    sizeId = -1;
                    view.findViewById(R.id.size1).setSelected(false);
                } else {
                    sizeId = 1;
                    view.findViewById(R.id.size1).setSelected(true);
                }

                view.findViewById(R.id.size2).setSelected(false);
                view.findViewById(R.id.size3).setSelected(false);
                view.findViewById(R.id.size4).setSelected(false);

            }
        });
        view.findViewById(R.id.size2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.findViewById(R.id.size2).isSelected()) {
                    sizeId = -1;
                    view.findViewById(R.id.size2).setSelected(false);
                } else {
                    sizeId = 2;
                    view.findViewById(R.id.size2).setSelected(true);
                }

                view.findViewById(R.id.size1).setSelected(false);
                view.findViewById(R.id.size3).setSelected(false);
                view.findViewById(R.id.size4).setSelected(false);

            }
        });
        view.findViewById(R.id.size3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.findViewById(R.id.size3).isSelected()) {
                    sizeId = -1;
                    view.findViewById(R.id.size3).setSelected(false);
                } else {
                    sizeId = 3;
                    view.findViewById(R.id.size3).setSelected(true);
                }

                view.findViewById(R.id.size1).setSelected(false);
                view.findViewById(R.id.size2).setSelected(false);
                view.findViewById(R.id.size4).setSelected(false);

            }
        });
        view.findViewById(R.id.size4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.findViewById(R.id.size4).isSelected()) {
                    sizeId = -1;
                    view.findViewById(R.id.size4).setSelected(false);
                } else {
                    sizeId = 4;
                    view.findViewById(R.id.size4).setSelected(true);
                }
                view.findViewById(R.id.size1).setSelected(false);
                view.findViewById(R.id.size2).setSelected(false);
                view.findViewById(R.id.size3).setSelected(false);

            }
        });
    }
}
