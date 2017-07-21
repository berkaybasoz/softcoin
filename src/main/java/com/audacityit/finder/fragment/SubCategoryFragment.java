package com.audacityit.finder.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.audacityit.finder.R;
import com.audacityit.finder.adapter.SubCategoryAdapter;
import com.audacityit.finder.model.Category;
import com.audacityit.finder.util.ApiHandler;
import com.audacityit.finder.util.UtilMethods;
import com.audacityit.finder.util.UtilMethods.InternetConnectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.audacityit.finder.util.Constants.JF_DATA;
import static com.audacityit.finder.util.Constants.JF_ID;
import static com.audacityit.finder.util.Constants.JF_TITLE;
import static com.audacityit.finder.util.Constants.isHomeOpened;
import static com.audacityit.finder.util.Constants.isResultListFragmentOpened;
import static com.audacityit.finder.util.UtilMethods.loadJSONFromAsset;
import static com.audacityit.finder.util.UtilMethods.showNoInternetDialog;

/**
 * @author Audacity IT Solutions Ltd.
 * @class HomeFragment
 * @brief Fragment for showing the sub-category list
 */

public class SubCategoryFragment extends Fragment implements InternetConnectionListener, ApiHandler.ApiHandlerListener {

    public static String catId;
    private final int SUB_CATEGORY_ACTION = 1;
    private ListView subCategoryListView;
    private ArrayList<Category> subCategoryList;
    private SubCategorySelectionCallbacks mCallbacks;
    private InternetConnectionListener internetConnectionListener;

    public SubCategoryFragment() {

    }

    public static SubCategoryFragment newInstance(String id) {
        SubCategoryFragment fragment = new SubCategoryFragment();
        catId = id;
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (SubCategorySelectionCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement SubCategorySelectionCallbacks.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_category, container, false);
        subCategoryListView = (ListView) rootView.findViewById(R.id.subCategoryListView);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        isHomeOpened = false;
        isResultListFragmentOpened = false;
        if (UtilMethods.isConnectedToInternet(getActivity()))
            initSubCategoryList();
        else {
            internetConnectionListener = (InternetConnectionListener) SubCategoryFragment.this;
            showNoInternetDialog(getActivity(), internetConnectionListener, getResources().getString(R.string.no_internet),
                    getResources().getString(R.string.no_internet_text),
                    getResources().getString(R.string.retry_string),
                    getResources().getString(R.string.exit_string), SUB_CATEGORY_ACTION);
        }
    }

    private void initSubCategoryList() {

        /**
         * json is populating from text file. To make api call use ApiHandler class
         *
         *   <CODE> ContentValues values = new ContentValues(); </CODE> <BR> 
         *   <CODE> values.put(KEY_CATEGORY_ID, catId); </CODE> <BR>
         *   <CODE> ApiHandler apiHandler = new ApiHandler(this, URL_GET_SUB_CATEGORY, values); </CODE> <BR>
         *   <CODE> apiHandler.doApiRequest(ApiHandler.REQUEST_POST); </CODE> <BR>
         *
         * You will get the response in onSuccessResponse(String tag, String jsonString) method
         * if successful api call has done.
         */

        String jsonString = loadJSONFromAsset(getActivity(), "get_sub_category_list");
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            subCategoryList = new ArrayList<Category>();
            JSONArray categoryArray = jsonObject.getJSONArray(JF_DATA);
            for (int i = 0; i < categoryArray.length(); i++) {
                Category category = new Category();
                category.setId(categoryArray.getJSONObject(i).getString(JF_ID));
                category.setTitle(categoryArray.getJSONObject(i).getString(JF_TITLE));
                subCategoryList.add(category);
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    subCategoryListView.setAdapter(new SubCategoryAdapter(getActivity(), mCallbacks, subCategoryList));
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionEstablished(int code) {
        if (code == SUB_CATEGORY_ACTION) {
            initSubCategoryList();
        }
    }

    @Override
    public void onUserCanceled(int code) {
        if (code == SUB_CATEGORY_ACTION) {
            getActivity().finish();
        }
    }

    //! catch json response from here
    @Override
    public void onSuccessResponse(String tag, String jsonString) {
        //! do same parsing as done in initSubCategoryList()
    }

    //! detect response error here
    @Override
    public void onFailureResponse(String tag) {

    }

    //! callback interface listen by HomeActivity to detect user click on sub-category
    public static interface SubCategorySelectionCallbacks {
        void onSubCategorySelected(String subCatID, String title);
    }

}
