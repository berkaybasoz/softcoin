package com.audacityit.finder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.audacityit.finder.R;
import com.audacityit.finder.util.FloatLabel;
import com.audacityit.finder.util.UtilMethods;

import org.json.JSONException;
import org.json.JSONObject;

import static com.audacityit.finder.util.Constants.INTENT_EXTRA;
import static com.audacityit.finder.util.Constants.KEY_SUCCESS;
import static com.audacityit.finder.util.Constants.MSG_SIGN_UP_FAILURE;
import static com.audacityit.finder.util.Constants.MSG_SIGN_UP_SUCCESS;
import static com.audacityit.finder.util.UtilMethods.hideSoftKeyboard;
import static com.audacityit.finder.util.UtilMethods.isConnectedToInternet;
import static com.audacityit.finder.util.UtilMethods.loadJSONFromAsset;
import static com.audacityit.finder.util.Validator.isInputted;
import static com.audacityit.finder.util.Validator.isMobileNumberValid;
import static com.audacityit.finder.util.Validator.isPasswordMatched;
import static com.audacityit.finder.util.Validator.isPasswordValid;
import static com.audacityit.finder.util.Validator.isValidEmail;
import static com.audacityit.finder.util.Validator.setPhoneCodeListener;

/**
 * @author Audacity IT Solutions Ltd.
 * @class SignUpActivity
 * @brief Responsible for creating new user
 */

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener, UtilMethods.InternetConnectionListener {

    private final int SIGNED_UP_ACTION = 1;
    private FloatLabel etMobileNumber;
    private FloatLabel etFullName;
    private FloatLabel etEmail;
    private RadioGroup sexGroup;
    private FloatLabel etPassword;
    private FloatLabel etRetypePassword;
    private boolean isUserCanceled = false;
    private UtilMethods.InternetConnectionListener internetConnectionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViewById(R.id.crossImgView).setOnClickListener(this);
        findViewById(R.id.btnHaveAccountTV).setOnClickListener(this);
        findViewById(R.id.showPasswordImg).setOnTouchListener(this);
        findViewById(R.id.showRetypePasswordImg).setOnTouchListener(this);
        findViewById(R.id.btnSignUp).setOnClickListener(this);
        etMobileNumber = (FloatLabel) findViewById(R.id.etMobileNumber);
        etFullName = (FloatLabel) findViewById(R.id.etFullName);
        etFullName.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        etEmail = (FloatLabel) findViewById(R.id.etFullEmail);
        etPassword = (FloatLabel) findViewById(R.id.etPassword);
        etRetypePassword = (FloatLabel) findViewById(R.id.etRetypePassword);
        etPassword.getEditText().setTransformationMethod(new PasswordTransformationMethod());
        etRetypePassword.getEditText().setTransformationMethod(new PasswordTransformationMethod());
        sexGroup = (RadioGroup) findViewById(R.id.radioGroup);
        etMobileNumber.getEditText().setOnFocusChangeListener(setPhoneCodeListener(this));
        etMobileNumber.getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (etMobileNumber.getEditText().getText().length() <=
                            getResources().getText(R.string.mobile_country_code).length()) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isUserCanceled) {
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            finish();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnHaveAccountTV:
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                isUserCanceled = true;
                onPause();
                break;

            case R.id.crossImgView:
                hideSoftKeyboard(this);
                isUserCanceled = true;
                onPause();
                break;

            case R.id.btnSignUp:
                if (isInputValid()) {
                    if (isConnectedToInternet(SignUpActivity.this)) {
                        //TODO: network call
                        initRequest();
                    } else {
                        internetConnectionListener = SignUpActivity.this;
                        UtilMethods.showNoInternetDialog(SignUpActivity.this, internetConnectionListener, getResources().getString(R.string.no_internet),
                                getResources().getString(R.string.no_internet_text),
                                getResources().getString(R.string.retry_string),
                                getResources().getString(R.string.cancel_string), SIGNED_UP_ACTION);
                    }
                }
                break;
        }
    }

    private void initRequest() {
        String jsonString = loadJSONFromAsset(this, "sign_up");
        try {
            final JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.getString(KEY_SUCCESS).equals(MSG_SIGN_UP_SUCCESS)) {
                JSONObject contentObject = jsonObject.getJSONObject("Contact");
                isUserCanceled = true;
                Intent verificationIntent = new Intent(SignUpActivity.this, VerificationActivity.class);
                verificationIntent.putExtra(INTENT_EXTRA, contentObject.toString());
                startActivity(verificationIntent);

            } else {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SignUpActivity.this, MSG_SIGN_UP_FAILURE, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (JSONException e) {

        }
    }

    private boolean isInputValid() {

        if (!isInputted(this, etMobileNumber)) {
            return false;
        }

        if (!isMobileNumberValid(this, etMobileNumber)) {
            return false;
        }

        if (!isInputted(this, etFullName)) {
            return false;
        }

        if (!isInputted(this, etEmail)) {
            return false;
        }

        if (!isValidEmail(this, etEmail)) {
            return false;
        }

        if (!isInputted(this, etPassword)) {
            return false;
        }

        if (!isPasswordValid(this, etPassword)) {
            return false;
        }

        if (!isPasswordMatched(this, etPassword, etRetypePassword)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.showPasswordImg) {
            if (!TextUtils.isEmpty(etPassword.getEditText().getText())) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    etPassword.getEditText().setTransformationMethod(null);
                    etPassword.getEditText().setSelection(etPassword.getEditText().getText().length());
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    etPassword.getEditText().setTransformationMethod(new PasswordTransformationMethod());
                    etPassword.getEditText().setSelection(etPassword.getEditText().getText().length());
                }
            }

        } else {
            if (!TextUtils.isEmpty(etRetypePassword.getEditText().getText())) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    etRetypePassword.getEditText().setTransformationMethod(null);
                    etRetypePassword.getEditText().setSelection(etRetypePassword.getEditText().getText().length());
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    etRetypePassword.getEditText().setTransformationMethod(new PasswordTransformationMethod());
                    etRetypePassword.getEditText().setSelection(etRetypePassword.getEditText().getText().length());
                }
            }
        }
        return false;
    }

    @Override
    public void onConnectionEstablished(int code) {
        if (code == SIGNED_UP_ACTION) {
            initRequest();
        }
    }

    @Override
    public void onUserCanceled(int code) {

    }

}

