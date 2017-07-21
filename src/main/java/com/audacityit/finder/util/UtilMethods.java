package com.audacityit.finder.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.audacityit.finder.R;
import com.audacityit.finder.activity.LandingActivity;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.audacityit.finder.util.Constants.FINDER_HOTLINE;
import static com.audacityit.finder.util.Constants.JF_CONTACT_NUMBER;
import static com.audacityit.finder.util.Constants.JF_EMAIL;
import static com.audacityit.finder.util.Constants.JF_ID;
import static com.audacityit.finder.util.Constants.JF_NAME;

/**
 * @author Audacity IT Solutions Ltd.
 * @class UtilMethods
 * @brief Methods used randomly through out the projects are described here
 */

public class UtilMethods {

    //! to activate internet checking set APP_TEST_MODE to false
    private static final boolean APP_TEST_MODE = true;
    //! to activate internet checking set APP_MAP_MODE to false
    public static boolean APP_MAP_MODE = false;
    private static AlertDialog dialog = null;

    /**
     * @param context
     * @return true or false mentioning the device is connected or not
     * @brief checking the internet connection on run time
     */
    public static boolean isConnectedToInternet(Context context) {
        if (APP_TEST_MODE && !APP_MAP_MODE) {
            return true;
        }
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
            }
        }
        return false;
    }

    /**
     * @param context the application context
     * @param key     variable in which the value will be stored to be retrieved later
     * @param value   the value to store
     * @brief save int value with shared preference
     */
    public static void savePreference(Context context, String key, int value) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * @param context the application context
     * @param key     variable from which the value will be retrieved
     * @return int
     * @brief retrieve int value from specific key
     */
    public static int getPreferenceInt(Context context, String key) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, 0);
    }

    /**
     * @param context the application context
     * @param key     variable in which the value will be stored to be retrieved later
     * @param value   the value to store
     * @brief save String value with shared preference
     */
    public static void savePreference(Context context, String key, String value) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * @param context the application context
     * @param key     variable from which the value will be retrieved
     * @return Sting
     * @brief retrieve String value from specific key
     */
    public static String getPreferenceString(Context context, String key) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, "");
    }

    /**
     * @param context the application context
     * @param url     the specified url to which the browser will be redirected
     * @brief methods for calling browser's intent with specified url
     */
    public static void browseUrl(Context context, String url) {
        if (!url.startsWith("http") && !url.startsWith("https"))
            url = "http://" + url;
        Intent openBrowserIntent = new Intent(Intent.ACTION_VIEW);
        openBrowserIntent.setData(Uri.parse(url));
        context.startActivity(openBrowserIntent);
    }

    /**
     * @param context the application context
     * @param number  the specified phone number
     * @brief methods for doing a phone call with specified phone number
     */
    public static void phoneCall(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        context.startActivity(intent);
    }

    /**
     * @param context the application context
     * @return true or false
     * @brief methods for identifying the device is supported for calling feature or not
     */
    public static boolean isDeviceCallSupported(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            Toast.makeText(context, context.getResources().getString(R.string.no_call_feature),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param context the application context
     * @param address the specified email address
     * @brief methods for sending a mail via mail intent
     */
    public static void mailTo(Context context, String address) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", address, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact with Amar Phonebook");
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    /**
     * @param context the application context
     * @param name    the subject of the mail to be sent
     * @param address the specified email address
     * @brief methods for sending a mail via mail intent
     */
    public static void mailTo(Context context, String name, String address) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", address, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact with " + name);
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    /**
     * @param activity the context of the activity
     * @brief methods for showing the soft keyboard by forced
     */
    public static void showSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(activity.getCurrentFocus(), 0);
        }
    }

    /**
     * @param activity the context of the activity
     * @brief methods for hiding the soft keyboard by forced
     */
    public static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity
                    .getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * @param context the application context
     * @return true or false
     * @brief methods for checking any user has already signed in or not
     */
    public static boolean isUserSignedIn(Context context) {
        if (!TextUtils.isEmpty(getPreferenceString(context, Constants.JF_CONTACT_NUMBER))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param context                    the application context
     * @param internetConnectionListener listener from which the method is called
     * @param headline                   headline text in String
     * @param body                       body text in String
     * @param positiveString             positive text in String
     * @param negativeString             negative text in String
     * @param code                       check flag for detecting the case when the class has multiple internet checking task
     * @brief methods for showing a custom no internet dialog
     */
    public static void showNoInternetDialog(final Context context, final InternetConnectionListener internetConnectionListener, final String headline, final String body,
                                            final String positiveString, final String negativeString, final int code) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_dialog, null);
        ((TextView) view.findViewById(R.id.headlineTV)).setText(headline);
        ((TextView) view.findViewById(R.id.bodyTV)).setText(body);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setPositiveButton(positiveString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (isConnectedToInternet(context)) {
                            internetConnectionListener.onConnectionEstablished(code);
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                            showNoInternetDialog(context, internetConnectionListener, headline, body, positiveString, negativeString, code);
                        }
                    }
                })
                .setNegativeButton(negativeString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        internetConnectionListener.onUserCanceled(code);
                        dialog.dismiss();
                    }
                })
                .setView(view)
                .setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    /**
     * @brief methods for showing a custom exit dialog
     * @param context        the application context
     * @param heading        the headline text in String
     * @param body           the body text in String
     * @param positiveString positive text in String
     * @param negativeString negative text in String
     */
    public static void showExitDialog(final Context context, final String heading, final String body, final String positiveString, final String negativeString) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_dialog, null);
        ((TextView) view.findViewById(R.id.headlineTV)).setText(heading);
        ((TextView) view.findViewById(R.id.bodyTV)).setText(body);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setPositiveButton(positiveString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser(context);
                        context.startActivity(new Intent(context, LandingActivity.class));
                        ((Activity) context).finish();
                    }
                })
                .setNegativeButton(negativeString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .setView(view)
                .setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    /**
     * @param context        the application context
     * @param heading        the headline text in String
     * @param body           the body text in String
     * @param positiveString positive text in String
     * @param negativeString positive text in String
     * @brief methods for showing a custom no gps dialog
     */
    public static void showNoGpsDialog(final Activity context, final String heading, final String body, final String positiveString, final String negativeString) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_dialog, null);
        ((TextView) view.findViewById(R.id.headlineTV)).setText(heading);
        ((TextView) view.findViewById(R.id.bodyTV)).setText(body);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setPositiveButton(positiveString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);

                    }
                })
                .setNegativeButton(negativeString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .setView(view)
                .setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    /**
     * @param context        the application context
     * @param heading        the headline text in String
     * @param body           the body text in String
     * @param positiveString positive text in String
     * @param negativeString positive text in String
     * @brief methods for showing a hotline calling dialog
     */
    public static void showHotLineCallDialog(final Activity context, final String heading, final String body, final String positiveString, final String negativeString) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_dialog, null);
        ((TextView) view.findViewById(R.id.headlineTV)).setText(heading);
        ((TextView) view.findViewById(R.id.bodyTV)).setText(body);
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setPositiveButton(positiveString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        phoneCall(context, FINDER_HOTLINE);

                    }
                })
                .setNegativeButton(negativeString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .setView(view)
                .setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    /**
     * @param context the application context
     * @return Point containing the width and height
     * @brief methods for getting device window height and width via Point object
     */
    public static Point getWindowSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     * @param context
     * @brief methods for delete the existing log in user by putting empty string to the shared
     * preference field
     */
    public static void deleteUser(Context context) {
        savePreference(context, JF_ID, "");
        savePreference(context, JF_CONTACT_NUMBER, "");
        savePreference(context, JF_NAME, "");
        savePreference(context, JF_EMAIL, "");
    }

    /**
     * @param context the application context
     * @param dp      the value in density pixel to be converted into pixel
     * @return pixel in int
     * @brief convert density pixel to standard pixel
     */
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /**
     * @param context the application context
     * @return true or false
     * @brief methods for checking the device's gps is enable or not
     */
    public static boolean isGpsEnable(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * @brief interface used by showNoInternetDialog() methods
     */
    public interface InternetConnectionListener {
        public void onConnectionEstablished(int code);

        public void onUserCanceled(int code);
    }

    /**
     * @param context  the application context
     * @param fileName name of the file from which the text will be loaded
     * @return json text in String
     * @brief methods for loading dummy JSON String from asset folder
     */
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("json/" + fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    /**
     * @param context   the application context
     * @param imageName the name of the image file
     * @return \c Uri object
     * @brief methods for getting \c Uri of a drawable from file name
     */
    public static String getDrawableFromFileName(Context context, String imageName) {
        return Uri.parse("android.resource://" + context.getPackageName() + "/drawable/" + imageName).toString();
    }

    public static String getFormattedDateSimple(Long dateTime) {
        SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy");
        return newFormat.format(new Date(dateTime));
    }
}
