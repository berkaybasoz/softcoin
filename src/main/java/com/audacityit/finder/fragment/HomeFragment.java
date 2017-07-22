package com.audacityit.finder.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.audacityit.finder.R;
import com.audacityit.finder.activity.ActivityWallet;
import com.audacityit.finder.activity.HomeActivity;
import com.audacityit.finder.adapter.CategoryAdapter;
import com.audacityit.finder.model.Category;
import com.audacityit.finder.util.ApiHandler;
import com.audacityit.finder.util.ListViewScrollListener;
import com.audacityit.finder.util.UtilMethods;
import com.audacityit.finder.util.UtilMethods.InternetConnectionListener;
import com.google.android.gms.vision.text.Text;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.audacityit.finder.util.Constants.JF_BACKGROUND_IMAGE;
import static com.audacityit.finder.util.Constants.JF_ICON;
import static com.audacityit.finder.util.Constants.JF_ID;
import static com.audacityit.finder.util.Constants.JF_TITLE;
import static com.audacityit.finder.util.UtilMethods.loadJSONFromAsset;
import static com.audacityit.finder.util.UtilMethods.showNoInternetDialog;

/**
 * @author Audacity IT Solutions Ltd.
 * @class HomeFragment
 * @brief Fragment for showing the category list
 */

public class HomeFragment extends Fragment implements InternetConnectionListener, ApiHandler.ApiHandlerListener, View.OnClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private final int CATEGORY_ACTION = 1;
    private CategorySelectionCallbacks mCallbacks;
    private ArrayList<Category> categoryList;
    private ListView categoryListView;
    private LinearLayout llBakiye;
    private InternetConnectionListener internetConnectionListener;
    private View btnWallet;

    public HomeFragment() {

    }

    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((HomeActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        try {
            mCallbacks = (CategorySelectionCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement CategorySelectionCallbacks.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        categoryListView = (ListView) rootView.findViewById(R.id.categoryListView);

        llBakiye = (LinearLayout) rootView.findViewById(R.id.llBakiye);
        btnWallet = (View) llBakiye.findViewById(R.id.btnWallet);
        btnWallet.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UtilMethods.isConnectedToInternet(getActivity())) {
            initCategoryList();
        } else {
            internetConnectionListener = (InternetConnectionListener) HomeFragment.this;
            showNoInternetDialog(getActivity(), internetConnectionListener,
                    getResources().getString(R.string.no_internet),
                    getResources().getString(R.string.no_internet_text),
                    getResources().getString(R.string.retry_string),
                    getResources().getString(R.string.exit_string), CATEGORY_ACTION);
        }

    }

    //! function for populate category list
    private void initCategoryList() {

        /**
         * json is populating from text file. To make api call use ApiHandler class
         *
         *  <CODE>ApiHandler apiHandler = new ApiHandler(this, URL_GET_CATEGORY);</CODE> <BR>
         *  <CODE>apiHandler.doApiRequest(ApiHandler.REQUEST_GET);</CODE> <BR>
         *
         * You will get the response in onSuccessResponse(String tag, String jsonString) method
         * if successful api call has done. Do the parsing as the following.
         */

        String jsonString = loadJSONFromAsset(getActivity(), "get_category_id_list");
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            categoryList = new ArrayList<Category>();

            for (int i = 0; i < jsonArray.length(); i++) {
                Category category = new Category();
                category.setId(jsonArray.getJSONObject(i).getString(JF_ID));
                category.setTitle(jsonArray.getJSONObject(i).getString(JF_TITLE));
                category.setIconUrl(jsonArray.getJSONObject(i).getString(JF_ICON));

                if (!TextUtils.isEmpty(jsonArray.getJSONObject(i).getString(JF_BACKGROUND_IMAGE))) {
                    category.setImageUrl(jsonArray.getJSONObject(i).getString(JF_BACKGROUND_IMAGE));
                }
                categoryList.add(category);
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    categoryListView.setAdapter(new CategoryAdapter(getActivity(), mCallbacks, categoryList));
                    categoryListView.setOnScrollListener(new ListViewScrollListener() {
                        @Override
                        public void onScrollUp() {
                            showViews();
                        }

                        @Override
                        public void onScrollDown() {
                            hideViews();
                        }
                    });
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void hideViews() {
        llBakiye.animate().translationY(-llBakiye.getHeight()).setInterpolator(new AccelerateInterpolator(2));

        //LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) llBakiye.getLayoutParams();
        //int fabBottomMargin = lp.bottomMargin;
        //mFabButton.animate().translationY(mFabButton.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        llBakiye.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
         
        //mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    @Override
    public void onConnectionEstablished(int code) {
        if (code == CATEGORY_ACTION) {
            initCategoryList();
        }
    }

    @Override
    public void onUserCanceled(int code) {
        if (code == CATEGORY_ACTION) {
            getActivity().finish();
        }
    }

    //! catch json response from here
    @Override
    public void onSuccessResponse(String tag, String jsonString) {
        //! do same parsing as done in initCategoryList()
    }

    //! detect response error here
    @Override
    public void onFailureResponse(String tag) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnWallet) {
            Intent i = new Intent(getContext(), ActivityWallet.class);
            startActivity(i);
        }
    }

    //! callback interface listen by HomeActivity to detect user click on category
    public static interface CategorySelectionCallbacks {
        void onCategorySelected(String catID, String title);
    }

}
