package com.audacityit.finder.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.audacityit.finder.R;

/**
 * @author Audacity IT Solutions Ltd.
 * @class LandingActivity
 * @brief Activity for showing Sign up, Sign In and See first option
 */
public class LandingActivity extends AppCompatActivity implements View.OnClickListener,
        VerificationActivity.SignUpCompleteListener, SignInActivity.SignInCompleteListener {

    private AnimationDrawable anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);
        findViewById(R.id.btnSignUp).setOnClickListener(this);
        findViewById(R.id.btnSeeFirst).setOnClickListener(this);
        findViewById(R.id.btnHaveAccountTV).setOnClickListener(this);
        VerificationActivity.setListener(this);
        SignInActivity.setListener(this);
        RelativeLayout rv = (RelativeLayout) findViewById(R.id.bg);
        anim = (AnimationDrawable) rv.getBackground();
        anim.setEnterFadeDuration(2000);
        anim.setExitFadeDuration(2000);
    }

    // Starting animation:- start the animation on onResume.
    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null && !anim.isRunning())
            anim.start();
    }

    // Stopping animation:- stop the animation on onPause.
    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSeeFirst:
                startActivity(new Intent(LandingActivity.this, HomeActivity.class));
                finish();
                break;

            case R.id.btnSignUp:
                startActivity(new Intent(LandingActivity.this, SignUpActivity.class));
                break;

            case R.id.btnHaveAccountTV:
                startActivity(new Intent(LandingActivity.this, SignInActivity.class));
                break;
        }
    }

    @Override
    public void onSignUpComplete() {
        finish();
    }

    @Override
    public void onSignInComplete() {
        finish();
    }
}
