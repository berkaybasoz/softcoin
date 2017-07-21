package com.audacityit.finder.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.audacityit.finder.R;
import com.audacityit.finder.util.FloatLabel;
import com.audacityit.finder.util.UtilMethods;
import com.audacityit.finder.util.UtilMethods.InternetConnectionListener;

import static com.audacityit.finder.util.Constants.MSG_PASSWORD_CHANGE_SUCCESS_2;
import static com.audacityit.finder.util.UtilMethods.isConnectedToInternet;
import static com.audacityit.finder.util.Validator.isInputted;
import static com.audacityit.finder.util.Validator.isMobileNumberValid;
import static com.audacityit.finder.util.Validator.setPhoneCodeListener;

/**
 * @author Audacity IT Solutions Ltd.
 * @class ForgetPasswordActivity
 * @brief Activity for sending the password to the user in case of forget
 */

public class ForgetPasswordActivity extends Activity implements View.OnClickListener, InternetConnectionListener {

    private final int FORGET_PASSWORD_ACTION = 1;
    private FloatLabel etMobileNumber;
    private AlertDialog resetDialog = null;
    private boolean isUserCanceled = false;
    private InternetConnectionListener internetConnectionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        findViewById(R.id.btnReset).setOnClickListener(this);
        findViewById(R.id.crossImgView).setOnClickListener(this);
        etMobileNumber = (FloatLabel) findViewById(R.id.etMobileNumber);
        etMobileNumber.getEditText().setTextColor(getResources().getColor(R.color.post_business_edit_text_color));
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReset:
                if (isInputValid()) {
                    if (isConnectedToInternet(this)) {
                        showVerificationConfirmDialog(ForgetPasswordActivity.this,
                                getResources().getString(R.string.password_reset_heading),
                                getResources().getString(R.string.password_reset_body),
                                getResources().getString(R.string.continue_string));
                    } else {
                        internetConnectionListener = ForgetPasswordActivity.this;
                        UtilMethods.showNoInternetDialog(ForgetPasswordActivity.this, internetConnectionListener, getResources().getString(R.string.no_internet),
                                getResources().getString(R.string.no_internet_text),
                                getResources().getString(R.string.retry_string),
                                getResources().getString(R.string.cancel_string), FORGET_PASSWORD_ACTION);
                    }
                }
                break;

            case R.id.crossImgView:
                isUserCanceled = true;
                onPause();
                break;

        }
    }

    private boolean isInputValid() {

        if (!isInputted(this, etMobileNumber)) {
            return false;
        }

        if (!isMobileNumberValid(this, etMobileNumber)) {
            return false;
        }

        return true;
    }

    public void showVerificationConfirmDialog(final Context context, String headline, String body,
                                              String positiveString) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_dialog, null);
        ((TextView) view.findViewById(R.id.headlineTV)).setText(headline);
        ((TextView) view.findViewById(R.id.bodyTV)).setText(body);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setPositiveButton(positiveString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        doRequestForPassword();
                        dialog.dismiss();
                        isUserCanceled = true;
                        onPause();
                    }
                })
                .setView(view)
                .setCancelable(false);

        resetDialog = builder.create();
        resetDialog.show();
    }

    private void doRequestForPassword() {
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
        if (code == FORGET_PASSWORD_ACTION) {
            showVerificationConfirmDialog(ForgetPasswordActivity.this,
                    getResources().getString(R.string.password_reset_heading),
                    getResources().getString(R.string.password_reset_body),
                    getResources().getString(R.string.continue_string));
        }
    }

    @Override
    public void onUserCanceled(int code) {
        isUserCanceled = true;
        onPause();
    }
}
