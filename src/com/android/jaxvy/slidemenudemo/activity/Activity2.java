package com.android.jaxvy.slidemenudemo.activity;

import com.android.jaxvy.slidemenudemo.R;
import com.android.jaxvy.slidemenudemo.util.SlideMenu;
import com.android.jaxvy.slidemenudemo.util.SlideMenuAnimationContainer;

import android.app.Activity;
import android.os.Bundle;

public class Activity2 extends Activity {

    private SlideMenuAnimationContainer slideMenuAnimationContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_layout);

        slideMenuAnimationContainer = (SlideMenuAnimationContainer) findViewById(R.id.slideMenuAnimationContainer);

        SlideMenu slideMenu = new SlideMenu(this, slideMenuAnimationContainer);
        slideMenuAnimationContainer.setListener(slideMenu);
        slideMenu.init();
    }

    @Override
    public void onBackPressed() {
        if (slideMenuAnimationContainer.isOpening()) {
            slideMenuAnimationContainer.closeSlideMenu();
        }
        else {
            finish();
        }
    }
}
