package com.audacityit.finder.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.audacityit.finder.R;
import com.audacityit.finder.adapter.ResultListAdapter;
import com.audacityit.finder.model.Item;
import com.audacityit.finder.util.LocationChangeListener;
import com.audacityit.finder.util.UtilMethods;
import com.audacityit.finder.util.UtilMethods.InternetConnectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import static com.audacityit.finder.util.Constants.ITEM_TYPE_COMPANY;
import static com.audacityit.finder.util.Constants.JF_AREA;
import static com.audacityit.finder.util.Constants.JF_CATEGORY;
import static com.audacityit.finder.util.Constants.JF_CATEGORY_ID;
import static com.audacityit.finder.util.Constants.JF_COMPANY_LIST;
import static com.audacityit.finder.util.Constants.JF_CONTACT_PHONE;
import static com.audacityit.finder.util.Constants.JF_DATA;
import static com.audacityit.finder.util.Constants.JF_DESCRIPTION;
import static com.audacityit.finder.util.Constants.JF_DISTRICT_ID;
import static com.audacityit.finder.util.Constants.JF_DISTRICT_INFO;
import static com.audacityit.finder.util.Constants.JF_DISTRICT_TITLE;
import static com.audacityit.finder.util.Constants.JF_EMAIL;
import static com.audacityit.finder.util.Constants.JF_ENTRY;
import static com.audacityit.finder.util.Constants.JF_ENTRY_TO_CATEGORY;
import static com.audacityit.finder.util.Constants.JF_FACEBOOK;
import static com.audacityit.finder.util.Constants.JF_GPLUS;
import static com.audacityit.finder.util.Constants.JF_HOLDING_NO;
import static com.audacityit.finder.util.Constants.JF_HOT_LINE;
import static com.audacityit.finder.util.Constants.JF_ID;
import static com.audacityit.finder.util.Constants.JF_IMAGES;
import static com.audacityit.finder.util.Constants.JF_ITEM_TYPE;
import static com.audacityit.finder.util.Constants.JF_LARGE_IMAGE_PATH;
import static com.audacityit.finder.util.Constants.JF_LATITUDE;
import static com.audacityit.finder.util.Constants.JF_LINKED_IN;
import static com.audacityit.finder.util.Constants.JF_LONGITUDE;
import static com.audacityit.finder.util.Constants.JF_MOBILE;
import static com.audacityit.finder.util.Constants.JF_RATING;
import static com.audacityit.finder.util.Constants.JF_RATING_COUNT;
import static com.audacityit.finder.util.Constants.JF_ROAD_VILLAGE;
import static com.audacityit.finder.util.Constants.JF_TAG_LINE;
import static com.audacityit.finder.util.Constants.JF_TELEPHONE;
import static com.audacityit.finder.util.Constants.JF_THANA;
import static com.audacityit.finder.util.Constants.JF_THUMB_IMAGE_PATH;
import static com.audacityit.finder.util.Constants.JF_TITLE;
import static com.audacityit.finder.util.Constants.JF_TWITTER;
import static com.audacityit.finder.util.Constants.JF_VERIFICATION;
import static com.audacityit.finder.util.Constants.JF_WEB;
import static com.audacityit.finder.util.Constants.NO_DATA_FOUND;
import static com.audacityit.finder.util.Constants.NULL_LOCATION;
import static com.audacityit.finder.util.Constants.isHomeOpened;
import static com.audacityit.finder.util.Constants.isResultListFragmentOpened;
import static com.audacityit.finder.util.UtilMethods.loadJSONFromAsset;
import static com.audacityit.finder.util.UtilMethods.showNoInternetDialog;


/**
 * @author Audacity IT Solutions Ltd.
 * @class ResultListFragment
 * @brief Fragment for showing the business list
 */

public class ResultListFragment extends Fragment implements InternetConnectionListener,
        LocationChangeListener {

    public static String catId;
    public static String searchTerm;
    public static LocationChangeListener locationChangeListener;
    private final int RESULT_ACTION = 1;
    private final int RESULT_LIMIT = 100;
    private ListView resultListView;
    private ArrayList<Item> searchResultList;
    private ResultListCallbacks mCallbacks;
    private InternetConnectionListener internetConnectionListener;

    public ResultListFragment() {

    }

    public static ResultListFragment newInstance(String id) {
        ResultListFragment fragment = new ResultListFragment();
        catId = id;
        searchTerm = "";
        locationChangeListener = fragment;
        return fragment;
    }

    public static ResultListFragment newInstance(String id, String term) {
        ResultListFragment fragment = new ResultListFragment();
        catId = id;
        searchTerm = term;
        locationChangeListener = fragment;
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (ResultListCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement ResultListCallbacks.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_result_list, container, false);
        resultListView = (ListView) rootView.findViewById(R.id.resultListView);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        isHomeOpened = false;
        isResultListFragmentOpened = true;
        if (UtilMethods.isConnectedToInternet(getActivity())) {
            if (!TextUtils.isEmpty(catId))
                initResultList();
            else if (!TextUtils.isEmpty(searchTerm))
                getSearchResults(searchTerm);
        } else {

            internetConnectionListener = (InternetConnectionListener) ResultListFragment.this;
            showNoInternetDialog(getActivity(), internetConnectionListener, getResources().getString(R.string.no_internet),
                    getResources().getString(R.string.no_internet_text),
                    getResources().getString(R.string.retry_string),
                    getResources().getString(R.string.exit_string), RESULT_ACTION);
        }

    }

    private void initResultList() {

        /**
         * json is populating from text file. To make api call use ApiHandler class
         * pass parameter using ContentValues (values)
         *
         * <CODE> ApiHandler handler = new ApiHandler(this, URL_GET_RESULT_LIST_WITH_AD, values);</CODE> <BR>
         * <CODE> handler.doApiRequest(ApiHandler.REQUEST_POST);</CODE> <BR>
         *
         * You will get the response in onSuccessResponse(String tag, String jsonString) method
         * if successful api call has done.
         */

        String jsonString = loadJSONFromAsset(getActivity(), "get_result_list");
        parseJson(jsonString);
    }

    private void getSearchResults(String query) {

        /**
         * json is populating from text file. To make api call use ApiHandler class
         * pass parameter using ContentValues (values)
         *
         * <CODE> ApiHandler handler = new ApiHandler(this, URL_GET_SEARCH_LIST_AD, values);</CODE> <BR>
         * <CODE> handler.doApiRequest(ApiHandler.REQUEST_POST);</CODE> <BR>
         *
         * You will get the response in onSuccessResponse(String tag, String jsonString) method
         * if successful api call has done.
         */

        String jsonString = loadJSONFromAsset(getActivity(), "get_search_list");
        parseJson(jsonString);
    }

    @Override
    public void onConnectionEstablished(int code) {
        if (code == RESULT_ACTION) {
            if (!TextUtils.isEmpty(catId))
                initResultList();
            else if (!TextUtils.isEmpty(searchTerm))
                getSearchResults(searchTerm);
        }
    }

    @Override
    public void onUserCanceled(int code) {
        if (code == RESULT_ACTION) {
            getActivity().finish();
        }
    }

    //! detect the location change by user from search filter on actionbar
    @Override
    public void onLocationChange() {
        if (UtilMethods.isConnectedToInternet(getActivity())) {
            getSearchResults(searchTerm);// call for live use
            //!calling for demo updates
            Collections.shuffle(searchResultList);
            ((ResultListAdapter) resultListView.getAdapter()).notifyDataSetChanged();

        } else {
            internetConnectionListener = (InternetConnectionListener) ResultListFragment.this;
            showNoInternetDialog(getActivity(), internetConnectionListener, getResources().getString(R.string.no_internet),
                    getResources().getString(R.string.no_internet_text),
                    getResources().getString(R.string.retry_string),
                    getResources().getString(R.string.exit_string), RESULT_ACTION);
        }
    }

    private void parseJson(String jsonString) {

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            searchResultList = new ArrayList<Item>();
            String imageLargePath = jsonObject.getString(JF_LARGE_IMAGE_PATH);
            String imageThumbPath = jsonObject.getString(JF_THUMB_IMAGE_PATH);
            JSONArray resultArray = jsonObject.getJSONArray(JF_DATA);
            for (int i = 0; i < resultArray.length(); i++) {
                Item item = new Item();
                if (resultArray.getJSONObject(i).getString(JF_ITEM_TYPE).equals("restaurant")) {
                    item.setType(ITEM_TYPE_COMPANY);
                    JSONObject companyObject = resultArray.getJSONObject(i).getJSONObject(JF_COMPANY_LIST);
                    JSONObject itemObject = companyObject.getJSONObject(JF_ENTRY);
                    item.setId(itemObject.getString(JF_ID));
                    item.setTitle(itemObject.getString(JF_TITLE));
                    item.setAddress(itemObject.getString(JF_HOLDING_NO) + ", " + itemObject.getString(JF_ROAD_VILLAGE) + ", " +
                            itemObject.getString(JF_THANA) + ", " + itemObject.getString(JF_AREA));
                    item.setDistrictId(itemObject.getString(JF_DISTRICT_ID));
                    item.setContactPhoneNumber(itemObject.optString(JF_CONTACT_PHONE, NO_DATA_FOUND));
                    item.setTelephoneNumber(itemObject.optString(JF_TELEPHONE, NO_DATA_FOUND));
                    item.setMobileNumber(itemObject.optString(JF_MOBILE, NO_DATA_FOUND));
                    item.setEmailAddress(itemObject.optString(JF_EMAIL, NO_DATA_FOUND));
                    item.setWebUrl(itemObject.optString(JF_WEB, NO_DATA_FOUND));
                    item.setFacebookUrl(itemObject.optString(JF_FACEBOOK, NO_DATA_FOUND));
                    item.setTwitterUrl(itemObject.optString(JF_TWITTER, NO_DATA_FOUND));
                    item.setGooglePlusUrl(itemObject.optString(JF_GPLUS, NO_DATA_FOUND));
                    item.setLinkedInUrl(itemObject.optString(JF_LINKED_IN, NO_DATA_FOUND));
                    item.setLatitude(itemObject.optDouble(JF_LATITUDE, NULL_LOCATION));
                    item.setLongitude(itemObject.optDouble(JF_LONGITUDE, NULL_LOCATION));
                    try {
                        item.setRating(Float.parseFloat(itemObject.optString(JF_RATING, NO_DATA_FOUND)));
                    } catch (NumberFormatException e) {
                        item.setRating(0.0f);
                    }

                    try {
                        item.setRatingCount(Integer.parseInt(itemObject.optString(JF_RATING_COUNT, NO_DATA_FOUND)));
                    } catch (NumberFormatException e) {
                        item.setRatingCount(0);
                    }
                    item.setHotLine(itemObject.optString(JF_HOT_LINE, NO_DATA_FOUND));
                    item.setTagLine(itemObject.optString(JF_TAG_LINE, NO_DATA_FOUND));
                    item.setDescription(itemObject.optString(JF_DESCRIPTION, NO_DATA_FOUND));
                    item.setVerification(itemObject.optString(JF_VERIFICATION, NO_DATA_FOUND).equals("1") ? true : false);
                    item.setDistrictTitle(companyObject.getJSONObject(JF_DISTRICT_INFO).getString(JF_DISTRICT_TITLE));
                    if (companyObject.has(JF_ENTRY_TO_CATEGORY)) {
                        item.setCategoryId(companyObject.optJSONObject(JF_ENTRY_TO_CATEGORY).optString(JF_CATEGORY_ID));
                    }

                    JSONArray imageArray = companyObject.getJSONArray(JF_IMAGES);
                    String[] imageThumb = new String[imageArray.length()];
                    String[] imageLarge = new String[imageArray.length()];

                    for (int j = 0; j < imageArray.length(); j++) {
                        imageThumb[j] = imageArray.getJSONObject(j).getString(JF_TITLE);
                        imageLarge[j] = imageArray.getJSONObject(j).getString(JF_TITLE);
                    }

                    item.setImageThumbUrls(imageThumb);
                    item.setImageLargeUrls(imageLarge);

                    JSONArray tagJsonArray = companyObject.getJSONArray(JF_CATEGORY);
                    String tag = "";
                    for (int j = 0; j < tagJsonArray.length(); j++) {
                        tag = tag + tagJsonArray.getJSONObject(j).getString(JF_TITLE);
                        if (j != tagJsonArray.length() - 1) {
                            tag = tag + ", ";
                        }
                    }
                    item.setTag(tag);
                    searchResultList.add(item);
                } else {
//                    item.setType(ITEM_TYPE_AD);
//                    JSONObject adObject = resultArray.getJSONObject(i).getJSONObject(JF_AD_LIST);
//                    item.setId(adObject.getString(JF_ID));
//                    item.setTitleImg(adObject.getString(JF_IMAGE));
//                    item.setAdUrl(adObject.getString(JF_AD_URL));
                }
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (searchResultList != null && searchResultList.size() > 0) {
                            searchResultList.addAll(searchResultList.size(), searchResultList);
                            searchResultList.addAll(searchResultList.size(), searchResultList);
                            searchResultList.addAll(searchResultList.size(), searchResultList);
                            resultListView.setAdapter(new ResultListAdapter(getActivity(), mCallbacks, searchResultList));
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.no_value), Toast.LENGTH_SHORT).show();
                            resultListView.setAdapter(null);
                        }
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Server error! \nTry again later", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // callback interface listen by HomeActivity to detect user click on business
    public interface ResultListCallbacks {
        void onResultItemSelected(Item itemDetails);
    }

}
