package com.audacityit.finder.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.audacityit.finder.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Audacity IT Solutions Ltd.
 * @class Validator
 * @brief class used for all kind of input validation
 */

public class Validator {

    /**
     * @param context application context
     * @param label   FloatLabel object
     * @return true or false
     * @brief methods for checking input field is null or not
     */
    public static boolean isInputted(Context context, FloatLabel label) {
        if (TextUtils.isEmpty(label.getEditText().getText())) {
            label.getEditText().setError(context.getResources().getString(R.string.empty_filed));
            return false;
        } else {
            label.getEditText().setError(null);
            return true;
        }
    }

    /**
     * @param context application context
     * @param label   FloatLabel object
     * @return true or false
     * @brief methods for checking mobile number is valid or not
     */
    public static boolean isMobileNumberValid(Context context, FloatLabel label) {
        if (label.getEditText().getText().length() != 13) {
            label.getEditText().setError(context.getResources().getString(R.string.invalid_mobile_number));
            return false;
        } else {
            label.getEditText().setError(null);
            return true;
        }
    }

    /**
     * @param context application context
     * @param label   FloatLabel object
     * @return true or false
     * @brief methods for checking email is valid or not
     */
    public static boolean isValidEmail(Context context, FloatLabel label) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(label.getEditText().getText());

        if (matcher.matches()) {
            label.getEditText().setError(null);
        } else {
            label.getEditText().setError(context.getResources().getString(R.string.invalid_email_address));
        }
        return matcher.matches();
    }

    /**
     * @param context application context
     * @param label   FloatLabel object
     * @return true or false
     * @brief methods for checking password is valid or not
     */
    public static boolean isPasswordValid(Context context, FloatLabel label) {
        if (label.getEditText().getText().length() < 6) {
            label.getEditText().setError(context.getResources().getString(R.string.invalid_password));
            return false;
        } else {
            label.getEditText().setError(null);
            return true;
        }
    }

    /**
     * @param context application context
     * @param label1  FloatLabel object
     * @param label2  FloatLabel object retype password
     * @return true or false
     * @brief methods for checking mobile number is valid or not
     */
    public static boolean isPasswordMatched(Context context, FloatLabel label1, FloatLabel label2) {
        if (label1.getEditText().getText().toString().equals(label2.getEditText().getText().toString())) {
            label2.getEditText().setError(null);
            return true;
        } else {
            label2.getEditText().setError(context.getResources().getString(R.string.password_mismatch));
            return false;
        }
    }

    public static View.OnFocusChangeListener setPhoneCodeListener(final Context context) {
        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (((EditText) v).getText().length() < 4) {
                        ((EditText) v).setText(context.getResources().getString(R.string.mobile_country_code));
                        ((EditText) v).setSelection(((EditText) v).length());
                    }
                }
            }
        };

        return listener;
    }

}
