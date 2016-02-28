package com.essential.usdriving.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.essential.usdriving.R;


public abstract class BaseFragment extends tatteam.com.app_common.ui.fragment.BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (enableToolbar()) {
            getBaseActivity().getToolbar().setVisibility(View.VISIBLE);
            getBaseActivity().getSupportActionBar().setTitle(setTitle());
            getBaseActivity().getToolbar().setTitleTextColor(getResources().getColor(R.color.white));
            if (enableIndicator()) {
                getBaseActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                if (enableBackButton()) {

                    getBaseActivity().getToolbar().setNavigationIcon(R.drawable.ic_back);
                } else {
                    getBaseActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }

            }
        } else {
            getBaseActivity().getToolbar().setVisibility(View.GONE);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (enableBackButton()) {
                    onBackPressed();
                }
                break;


            case R.id.Result:
                if (enableResultButton()) {
                    defineButtonResult();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected int getLayoutResIdContentView() {
        return 0;
    }

    @Override
    protected void onCreateContentView(View rootView, Bundle savedInstanceState) {

    }


    protected String setTitle() {
        return getString(R.string.app_name);
    }

    protected boolean enableToolbar() {
        return true;
    }

    protected boolean enableIndicator() {
        return true;
    }

    protected boolean enableResultButton() {
        return false;
    }

    protected boolean enableBackButton() {
        return false;
    }

    public BaseActivity getBaseActivity() {

        return (BaseActivity) getActivity();
    }

    public abstract void defineButtonResult();
}
