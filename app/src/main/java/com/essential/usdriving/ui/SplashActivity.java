package com.essential.usdriving.ui;

import android.content.Intent;

import com.essential.usdriving.app.DMVActivity;

import tatteam.com.app_common.AppCommon;
import tatteam.com.app_common.sqlite.DatabaseLoader;
import tatteam.com.app_common.ui.activity.EssentialSplashActivity;
import tatteam.com.app_common.util.AppConstant;

public class SplashActivity extends EssentialSplashActivity {


    @Override
    protected void onCreateContentView() {
        super.onCreateContentView();
    }

    @Override
    protected void onInitAppCommon() {
        AppCommon.getInstance().initIfNeeded(getApplicationContext());
        AppCommon.getInstance().increaseLaunchTime();
        AppCommon.getInstance().syncAdsIfNeeded(AppConstant.AdsType.SMALL_BANNER_DRIVING_TEST, AppConstant.AdsType.BIG_BANNER_DRIVING_TEST);
        DatabaseLoader.getInstance().createIfNeeded(getApplicationContext(), "usdriving.db");
    }

    @Override
    protected void onFinishInitAppCommon() {
        switchToChooseTargetActivity();
    }
    private void switchToChooseTargetActivity() {
        Intent intent = new Intent(SplashActivity.this, DMVActivity.class);
        startActivity(intent);
        finish();
    }
}
