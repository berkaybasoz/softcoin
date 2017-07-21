package com.audacityit.finder.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.audacityit.finder.R;
import com.audacityit.finder.model.User;
import com.audacityit.finder.util.Constants;
import com.audacityit.finder.util.FloatLabel;
import com.audacityit.finder.util.UtilMethods;

import org.json.JSONException;
import org.json.JSONObject;

import static com.audacityit.finder.util.Constants.INTENT_EXTRA;
import static com.audacityit.finder.util.Constants.JF_CONTACT_NUMBER;
import static com.audacityit.finder.util.Constants.JF_EMAIL;
import static com.audacityit.finder.util.Constants.JF_GENDER;
import static com.audacityit.finder.util.Constants.JF_NAME;
import static com.audacityit.finder.util.Constants.KEY_MOBILE_NUMBER;
import static com.audacityit.finder.util.Constants.MSG_PASSWORD_CHANGE_SUCCESS_2;
import static com.audacityit.finder.util.UtilMethods.hideSoftKeyboard;
import static com.audacityit.finder.util.UtilMethods.isConnectedToInternet;
import static com.audacityit.finder.util.UtilMethods.savePreference;
import static com.audacityit.finder.util.Validator.isInputted;

/**
 * @author Audacity IT Solutions Ltd.
 * @class VerificationActivity
 * @brief Activity for making user verified via sending code to the user mobile number
 */

public class VerificationActivity extends Activity implements View.OnClickListener,
        UtilMethods.InternetConnectionListener {

    private static final int VERIFICATION_REQUEST_ACTION = 1;
    private static final int RESEND_VERIFICATION_REQUEST_ACTION = 2;
    private static SignUpCompleteListener signUpCompleteListener;
    private FloatLabel etVerificationCode;
    private AlertDialog verificationDialog = null;
    private boolean isUserCanceled = false;
    private String signUpRecord = null;
    private JSONObject contentObject;
    private UtilMethods.InternetConnectionListener internetConnectionListener;

    public static void setListener(Context context) {
        signUpCompleteListener = (SignUpCompleteListener) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        findViewById(R.id.crossImgView).setOnClickListener(this);
        findViewById(R.id.btnSubmit).setOnClickListener(this);
        findViewById(R.id.btnResendCode).setOnClickListener(this);
        etVerificationCode = (FloatLabel) findViewById(R.id.etVerificationCode);
        etVerificationCode.getEditText().setTextColor(getResources().getColor(R.color.post_business_edit_text_color));

        if (getIntent().getExtras() != null) {
            signUpRecord = getIntent().getExtras().getString(INTENT_EXTRA);
            try {
                contentObject = new JSONObject(signUpRecord);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.crossImgView:
                hideSoftKeyboard(this);
                isUserCanceled = true;
                onPause();
                break;

            case R.id.btnSubmit:
                if (isInputted(VerificationActivity.this, etVerificationCode)) {
                    if (isConnectedToInternet(VerificationActivity.this)) {
                        verificationRequest();
                    } else {
                        internetConnectionListener = VerificationActivity.this;
                        UtilMethods.showNoInternetDialog(VerificationActivity.this, internetConnectionListener, getResources().getString(R.string.no_internet),
                                getResources().getString(R.string.no_internet_text),
                                getResources().getString(R.string.retry_string),
                                getResources().getString(R.string.cancel_string), VERIFICATION_REQUEST_ACTION);
                    }

                } else {
                    Toast.makeText(VerificationActivity.this, "Please input verification code", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnResendCode:
                if (isConnectedToInternet(VerificationActivity.this)) {
                    showVerificationConfirmDialog(VerificationActivity.this,
                            getResources().getString(R.string.code_resend_heading),
                            getResources().getString(R.string.code_resend_body),
                            getResources().getString(R.string.continue_string), 0);
                } else {
                    internetConnectionListener = VerificationActivity.this;
                    UtilMethods.showNoInternetDialog(VerificationActivity.this, internetConnectionListener, getResources().getString(R.string.no_internet),
                            getResources().getString(R.string.no_internet_text),
                            getResources().getString(R.string.retry_string),
                            getResources().getString(R.string.cancel_string), RESEND_VERIFICATION_REQUEST_ACTION);
                }
                break;
        }
    }

    private void verificationRequest() {
        saveUser();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showVerificationConfirmDialog(VerificationActivity.this,
                        getResources().getString(R.string.verification_heading),
                        getResources().getString(R.string.verification_body),
                        getResources().getString(R.string.continue_string), 1);
            }
        });
    }

    private void saveUser() {
        if (contentObject == null) {
            try {
                contentObject = new JSONObject(signUpRecord);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        User user = new User();
        user.setPhoneNumber(contentObject.optString(JF_CONTACT_NUMBER));
        user.setName(contentObject.optString(JF_NAME));
        user.setEmail(contentObject.optString(JF_EMAIL));
        user.setGenderId(contentObject.optString(JF_GENDER));
        savePreference(VerificationActivity.this, JF_CONTACT_NUMBER, user.getPhoneNumber());
        savePreference(VerificationActivity.this, JF_NAME, user.getName());
        savePreference(VerificationActivity.this, JF_EMAIL, user.getEmail());
        savePreference(VerificationActivity.this, JF_GENDER, user.getGenderId());
    }

    public void showVerificationConfirmDialog(final Context context, String headline, String body,
                                              String positiveString, final int type) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_dialog, null);
        ((TextView) view.findViewById(R.id.headlineTV)).setText(headline);
        ((TextView) view.findViewById(R.id.bodyTV)).setText(body);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setPositiveButton(positiveString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (type == 1) {
                            Constants.isSignedUp = true;
                            signUpCompleteListener.onSignUpComplete();
                            startActivity(new Intent(VerificationActivity.this, HomeActivity.class));
                            dialog.dismiss();
                            VerificationActivity.this.finish();
                        } else {
                            initResendRequest();
                            dialog.dismiss();
                        }
                    }
                })
                .setView(view)
                .setCancelable(false);

        verificationDialog = builder.create();
        verificationDialog.show();
    }

    private void initResendRequest() {
        ContentValues values = new ContentValues();
        values.put(KEY_MOBILE_NUMBER, contentObject.optString(JF_CONTACT_NUMBER));
        Toast.makeText(this, MSG_PASSWORD_CHANGE_SUCCESS_2, Toast.LENGTH_SHORT).show();
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
    public void onConnectionEstablished(int code) {
        if (code == VERIFICATION_REQUEST_ACTION) {
            verificationRequest();
        } else {
            showVerificationConfirmDialog(VerificationActivity.this,
                    getResources().getString(R.string.code_resend_heading),
                    getResources().getString(R.string.code_resend_body),
                    getResources().getString(R.string.continue_string), 0);
        }
    }

    @Override
    public void onUserCanceled(int code) {

    }

    public interface SignUpCompleteListener {
        void onSignUpComplete();
    }
}
