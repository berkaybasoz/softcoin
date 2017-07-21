package com.audacityit.finder.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.audacityit.finder.R;
import com.audacityit.finder.activity.MapActivity;
import com.audacityit.finder.adapter.ImagePagerAdapter;
import com.audacityit.finder.model.Comment;
import com.audacityit.finder.model.Item;
import com.audacityit.finder.util.CustomRatingBar;
import com.audacityit.finder.util.ExpandableTextView;
import com.audacityit.finder.util.PhoneCallDialog;
import com.audacityit.finder.util.UtilMethods.InternetConnectionListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static com.audacityit.finder.util.Constants.JF_DATE;
import static com.audacityit.finder.util.Constants.JF_NAME;
import static com.audacityit.finder.util.Constants.JF_RATING_ARRAY;
import static com.audacityit.finder.util.Constants.JF_REVIEW;
import static com.audacityit.finder.util.Constants.JF_USER_RATING;
import static com.audacityit.finder.util.Constants.MSG_RATING_SUCCESSFUL;
import static com.audacityit.finder.util.Constants.NO_DATA_FOUND;
import static com.audacityit.finder.util.Constants.NULL_LOCATION;
import static com.audacityit.finder.util.UtilMethods.APP_MAP_MODE;
import static com.audacityit.finder.util.UtilMethods.browseUrl;
import static com.audacityit.finder.util.UtilMethods.getPreferenceString;
import static com.audacityit.finder.util.UtilMethods.isConnectedToInternet;
import static com.audacityit.finder.util.UtilMethods.isDeviceCallSupported;
import static com.audacityit.finder.util.UtilMethods.isGpsEnable;
import static com.audacityit.finder.util.UtilMethods.isUserSignedIn;
import static com.audacityit.finder.util.UtilMethods.loadJSONFromAsset;
import static com.audacityit.finder.util.UtilMethods.mailTo;
import static com.audacityit.finder.util.UtilMethods.phoneCall;
import static com.audacityit.finder.util.UtilMethods.showNoGpsDialog;
import static com.audacityit.finder.util.UtilMethods.showNoInternetDialog;

/**
 * @author Audacity IT Solutions Ltd.
 * @class DetailViewFragment
 * @brief Fragment for showing business in detail view with user comments, rating and gallery view
 */
public class DetailViewFragment extends Fragment implements InternetConnectionListener {


    public static Item itemDetails;
    private static AlertDialog dialog = null;
    private final int BROWSER_ACTION = 1;
    private final int MAP_ACTION = 2;
    private final int RATE_NOW_ACTION = 3;
    SimpleDateFormat appViewFormat;
    SimpleDateFormat serverFormat;
    private ViewPager imagePager;
    private ImageView prevImgView;
    private ImageView nextImgView;
    private InternetConnectionListener internetConnectionListener;
    private int googlePlayServiceStatus;
    private ArrayList<Comment> commentList;
    private TextView countRatingTV;
    private TextView allRatingTV;
    private LayoutInflater inflater;
    private LinearLayout commentLayout;
    private String phoneString = null;


    public DetailViewFragment() {

    }

    public static DetailViewFragment newInstance(Item item) {
        DetailViewFragment fragment = new DetailViewFragment();
        itemDetails = item;
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_view, container, false);
        commentLayout = (LinearLayout) rootView.findViewById(R.id.commentLayout);
        if (itemDetails != null) {
            imagePager = ((ViewPager) rootView.findViewById(R.id.detailHeadingImageViewPager));
            prevImgView = (ImageView) rootView.findViewById(R.id.prevImgView);
            nextImgView = (ImageView) rootView.findViewById(R.id.nextImgView);
            countRatingTV = (TextView) rootView.findViewById(R.id.countRatingTV);
            allRatingTV = (TextView) rootView.findViewById(R.id.allRatingTV);

            //! viewpager to show images with horizontal scrolling.
            imagePager.setAdapter(new ImagePagerAdapter(getActivity(), itemDetails.getImageLargeUrls()));

            //! hide previous and next arrow if adapter size is less then 2
            if (imagePager.getAdapter().getCount() <= 1) {
                prevImgView.setVisibility(View.INVISIBLE);
                nextImgView.setVisibility(View.INVISIBLE);
            }


            ((ViewPager) rootView.findViewById(R.id.detailHeadingImageViewPager)).addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    if (imagePager.getAdapter().getCount() > 1) {
                        if (position == 0) {
                            nextImgView.setVisibility(View.VISIBLE);
                            prevImgView.setVisibility(View.INVISIBLE);
                        } else if (position == imagePager.getAdapter().getCount() - 1) {
                            prevImgView.setVisibility(View.VISIBLE);
                            nextImgView.setVisibility(View.INVISIBLE);
                        } else {
                            prevImgView.setVisibility(View.VISIBLE);
                            nextImgView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            if (itemDetails.isVerified()) {
                rootView.findViewById(R.id.verificationImgView).setVisibility(View.VISIBLE);
            } else {
                rootView.findViewById(R.id.verificationImgView).setVisibility(View.INVISIBLE);
            }
            if (!TextUtils.isEmpty(itemDetails.getTitle())) {
                ((TextView) rootView.findViewById(R.id.itemNameTV)).setText(itemDetails.getTitle());
            }
            if (!TextUtils.isEmpty(itemDetails.getMobileNumber()) &&
                    !itemDetails.getMobileNumber().equals(NO_DATA_FOUND)) {
                phoneString = itemDetails.getMobileNumber();
            }
            if (!TextUtils.isEmpty(itemDetails.getTelephoneNumber()) &&
                    !itemDetails.getTelephoneNumber().equals(NO_DATA_FOUND)) {
                if (!TextUtils.isEmpty(phoneString)) {
                    phoneString += ",";
                    phoneString += itemDetails.getTelephoneNumber();
                } else {
                    phoneString = itemDetails.getTelephoneNumber();
                }
            }
            if (!TextUtils.isEmpty(itemDetails.getContactPhoneNumber()) &&
                    !itemDetails.getContactPhoneNumber().equals(NO_DATA_FOUND)) {
                if (!TextUtils.isEmpty(phoneString)) {
                    phoneString += ",";
                    phoneString += itemDetails.getContactPhoneNumber();
                } else {
                    phoneString = itemDetails.getContactPhoneNumber();
                }
            }
            if (!TextUtils.isEmpty(phoneString))
                ((TextView) rootView.findViewById(R.id.itemPhoneTV)).setText(phoneString);
            if (!TextUtils.isEmpty(itemDetails.getAddress()))
                ((TextView) rootView.findViewById(R.id.itemLocationTV)).setText(itemDetails.getAddress());
            if (!TextUtils.isEmpty(itemDetails.getTag()))
                ((ExpandableTextView) rootView.findViewById(R.id.itemTagTV)).setText(itemDetails.getTag());
            ((CustomRatingBar) rootView.findViewById(R.id.ratingBar)).setScore(itemDetails.getRating());
            ((TextView) rootView.findViewById(R.id.btnWeb)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(itemDetails.getWebUrl())) {
                        if (isConnectedToInternet(getActivity())) {
                            browseUrl(getActivity(), itemDetails.getWebUrl());
                        } else {
                            internetConnectionListener = (InternetConnectionListener) DetailViewFragment.this;
                            showNoInternetDialog(getActivity(), internetConnectionListener, getResources().getString(R.string.no_internet),
                                    getResources().getString(R.string.no_internet_text),
                                    getResources().getString(R.string.retry_string),
                                    getResources().getString(R.string.cancel_string), BROWSER_ACTION);
                        }

                    } else
                        Toast.makeText(getActivity(), getResources().getString(R.string.no_website), Toast.LENGTH_SHORT).show();
                }
            });

            ((TextView) rootView.findViewById(R.id.btnEmail)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(itemDetails.getEmailAddress()))
                        mailTo(getActivity(), itemDetails.getTitle(), itemDetails.getEmailAddress());
                    else
                        Toast.makeText(getActivity(), getResources().getString(R.string.no_email), Toast.LENGTH_SHORT).show();

                }
            });

            ((TextView) rootView.findViewById(R.id.btnMap)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMap();
                }
            });

            ((TextView) rootView.findViewById(R.id.btnRateNow)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isUserSignedIn(getActivity())) {
                        if (isConnectedToInternet(getActivity())) {
                            showRatingDialog(getActivity(), itemDetails.getTitle(), getResources().getString(R.string.submit_string),
                                    getResources().getString(R.string.cancel_string));

                        } else {
                            internetConnectionListener = (InternetConnectionListener) DetailViewFragment.this;
                            showNoInternetDialog(getActivity(), internetConnectionListener, getResources().getString(R.string.no_internet),
                                    getResources().getString(R.string.no_internet_text),
                                    getResources().getString(R.string.retry_string),
                                    getResources().getString(R.string.cancel_string), RATE_NOW_ACTION);
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.sign_in_text), Toast.LENGTH_SHORT).show();
                    }


                }
            });

            ((TextView) rootView.findViewById(R.id.itemPhoneTV)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(phoneString)) {
                        if (isDeviceCallSupported(getActivity())) {
                            if (phoneString.contains(",")) {
                                PhoneCallDialog.showPhoneCallDialog(getActivity(),
                                        phoneString.split(","));
                            } else {
                                phoneCall(getActivity(), phoneString);
                            }
                        }
                    }
                }
            });

            ((TextView) rootView.findViewById(R.id.itemLocationTV)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMap();
                }
            });

        }
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {
            menu.findItem(R.id.action_filter).setVisible(false);
            menu.findItem(R.id.action_search).setVisible(false);
        }
    }

    private void showMap() {
        if (itemDetails.getLatitude() != NULL_LOCATION && itemDetails.getLongitude() != NULL_LOCATION) {
            /** set APP_MAP_MODE to true to enable internet checking
            * because map needs internet connection
            * to  show user and business location as well as their distance
            */
            APP_MAP_MODE = true;
            if (isConnectedToInternet(getActivity())) {
                if (isGooglePlayServicesAvailable()) {
                    if (isGpsEnable(getActivity())) {
                        startActivity(new Intent(getActivity(), MapActivity.class));
                    } else {
                        showNoGpsDialog(getActivity(), getResources().getString(R.string.no_gps),
                                getResources().getString(R.string.no_gps_message),
                                getResources().getString(R.string.no_gps_positive_text),
                                getResources().getString(R.string.no_gps_negative_text));
                    }
                } else {
                    showGooglePlayServiceUnavailableDialog();
                }
            } else {
                internetConnectionListener = (InternetConnectionListener) DetailViewFragment.this;
                showNoInternetDialog(getActivity(), internetConnectionListener, getResources().getString(R.string.no_internet),
                        getResources().getString(R.string.no_internet_text),
                        getResources().getString(R.string.retry_string),
                        getResources().getString(R.string.cancel_string), MAP_ACTION);
            }

        } else
            Toast.makeText(getActivity(), getResources().getString(R.string.location_not_found), Toast.LENGTH_SHORT).show();
    }


    /**
     * @brief custom dialog for showing rating dialog
     * @param context application context
     * @param headline headline in String
     * @param positiveString positive text in String
     * @param negativeString negative text in String
     */
    private void showRatingDialog(final Context context, String headline,
                                  String positiveString, String negativeString) {
        final EditText etComment;
        final CustomRatingBar ratingBar;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_rating_dialog, null);
        ((TextView) view.findViewById(R.id.headlineTV)).setText(headline);
        etComment = (EditText) view.findViewById(R.id.etComment);
        ratingBar = (CustomRatingBar) view.findViewById(R.id.ratingBar);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setPositiveButton(positiveString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (ratingBar.getScore() <= 0) {
                            Toast.makeText(getActivity(), getResources().
                                            getString(R.string.no_rating_error_string),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            initUserRating(getActivity(), etComment, ratingBar);
                            dialog.dismiss();
                        }
                    }
                }).setNegativeButton(negativeString, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setView(view)
                .setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    private void initUserRating(Context context, EditText etComment, CustomRatingBar ratingBar) {
        getUserComment(etComment, ratingBar);
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserComment();
    }

    //* get all comments for the specific business
    private void getUserComment() {
        //* call api here to get all comments
        String jsonString = loadJSONFromAsset(getActivity(), "get_user_comments");
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray ratingArray = jsonObject.getJSONArray(JF_RATING_ARRAY);
            commentList = new ArrayList<Comment>();
            for (int i = 0; i < ratingArray.length(); i++) {

                Comment comment = new Comment();
                if (!ratingArray.getJSONObject(i).getString(JF_NAME).equals("null")) {
                    comment.setUserName(ratingArray.getJSONObject(i).getString(JF_NAME));
                }

                try {
                    comment.setRating(Float.parseFloat(ratingArray.getJSONObject(i).
                            optString(JF_USER_RATING, NO_DATA_FOUND)));
                } catch (NumberFormatException e) {
                    comment.setRating(0.0f);
                }

                if (!ratingArray.getJSONObject(i).getString(JF_REVIEW).equals("null")) {
                    comment.setText(ratingArray.getJSONObject(i).getString(JF_REVIEW));
                }

                comment.setDate(dateTimeFormatter(ratingArray.getJSONObject(i).
                        getString(JF_DATE)));
                commentList.add(comment);
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (commentList.size() > 0) {
                        allRatingTV.setVisibility(View.VISIBLE);
                        Collections.reverse(commentList);
                        countRatingTV.setText(commentList.size() + " Rating(s)");
                        addCommentsToView(commentList);
                    } else {
//                            allRatingTV.setVisibility(View.GONE);
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getUserComment(EditText etComment, CustomRatingBar ratingBar) {
        //* make api call here to send user comment to server
        Toast.makeText(getActivity(), MSG_RATING_SUCCESSFUL, Toast.LENGTH_SHORT).show();
        Collections.reverse(commentList);
        commentList.add(new Comment(getPreferenceString(getActivity(), JF_NAME),
                ratingBar.getScore(), etComment.getText().toString(),
                appViewFormat.format(new Date())));

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (commentList.size() > 0) {
                    allRatingTV.setVisibility(View.VISIBLE);
                    Collections.reverse(commentList);
                    countRatingTV.setText(commentList.size() + " Rating(s)");
                    addCommentsToView(commentList);
                } else {
//                            allRatingTV.setVisibility(View.GONE);
                }
            }
        });
    }

    private String dateTimeFormatter(String serverDate) {
        appViewFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a");
        serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return appViewFormat.format(serverFormat.parse(serverDate));
        } catch (ParseException e) {
            return serverDate;
        }
    }

    @Override
    public void onConnectionEstablished(int code) {
        if (code == BROWSER_ACTION) {
            browseUrl(getActivity(), itemDetails.getWebUrl());
        }
        if (code == MAP_ACTION) {
            APP_MAP_MODE = false;
            startActivity(new Intent(getActivity(), MapActivity.class));
        } else if (code == RATE_NOW_ACTION) {
            showRatingDialog(getActivity(), itemDetails.getTitle(),
                    getResources().getString(R.string.submit_string),
                    getResources().getString(R.string.cancel_string));
        }
    }

    @Override
    public void onUserCanceled(int code) {
        if (code == RATE_NOW_ACTION) {

        }
        if (code == MAP_ACTION) {
            APP_MAP_MODE = false;
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        googlePlayServiceStatus = GooglePlayServicesUtil.
                isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS == googlePlayServiceStatus) {
            return true;
        } else {
            return false;
        }
    }

    private void showGooglePlayServiceUnavailableDialog() {
        GooglePlayServicesUtil.getErrorDialog(googlePlayServiceStatus, getActivity(), 0).show();
    }

    /**
     * @brief methods to add all comments to comment view
     * @param commentList collection of comment to make updates of comment view
     */
    private void addCommentsToView(ArrayList<Comment> commentList) {
        commentLayout.removeAllViews();
        for (Comment comment : commentList) {
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //* change the comment layout from res > layout > layout_comment
            RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.layout_comment, null);

            if (TextUtils.isEmpty(comment.getUserName())) {
                layout.findViewById(R.id.userNameTV).setVisibility(View.GONE);
            } else {
                ((TextView) layout.findViewById(R.id.userNameTV)).setText(comment.getUserName());
            }

            ((TextView) layout.findViewById(R.id.dateTV)).setText(comment.getDate());
            ((CustomRatingBar) layout.findViewById(R.id.commentRatingBar)).setScore(comment.getRating());
            if (TextUtils.isEmpty(comment.getText())) {
                layout.findViewById(R.id.commentTV).setVisibility(View.GONE);
            } else {
                ((TextView) layout.findViewById(R.id.commentTV)).setText(comment.getText());
            }

            commentLayout.addView(layout, commentLayout.getChildCount());
        }
    }
}
