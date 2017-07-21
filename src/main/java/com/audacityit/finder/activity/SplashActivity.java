package com.audacityit.finder.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.audacityit.finder.R;
import com.audacityit.finder.util.UtilMethods;

/**
 * @author Audacity IT Solutions Ltd.
 * @mainpage SplashActivity
 * @class SplashActivity
 * @brief Activity for showing the splash screen
 */

public class SplashActivity extends AppCompatActivity {

    private ScrollView promoScrollView;
    private ImageView promoView;
    Animation translateAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        promoScrollView = (ScrollView) findViewById(R.id.promoScrollView);
        promoView = (ImageView) findViewById(R.id.promoImageView);
        Point point = UtilMethods.getWindowSize(this);
        ScrollView.LayoutParams params = new ScrollView.LayoutParams(point.x * 2,
                ScrollView.LayoutParams.MATCH_PARENT);
        promoView.setLayoutParams(params);
        findViewById(R.id.btnSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this, TermsConditionActivity.class));
            }
        });

        promoScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, -point.x, TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f);
        translateAnimation.setDuration(8000);
        translateAnimation.setRepeatCount(Animation.INFINITE);
        translateAnimation.setRepeatMode(Animation.REVERSE);
        translateAnimation.setInterpolator(new LinearInterpolator());
        promoView.startAnimation(translateAnimation);
        promoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        promoScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
