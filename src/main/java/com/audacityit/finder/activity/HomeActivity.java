package com.audacityit.finder.activity;


import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.audacityit.finder.R;
import com.audacityit.finder.fragment.DetailViewFragment;
import com.audacityit.finder.fragment.HomeFragment;
import com.audacityit.finder.fragment.NavigationDrawerFragment;
import com.audacityit.finder.fragment.ResultListFragment;
import com.audacityit.finder.fragment.SubCategoryFragment;
import com.audacityit.finder.model.District;
import com.audacityit.finder.model.Item;
import com.audacityit.finder.util.UtilMethods;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.audacityit.finder.util.Constants.JF_AREA;
import static com.audacityit.finder.util.Constants.JF_ENTRY;
import static com.audacityit.finder.util.Constants.JF_ID;
import static com.audacityit.finder.util.Constants.JF_TITLE;
import static com.audacityit.finder.util.Constants.KEY_DISTRICT_2;
import static com.audacityit.finder.util.Constants.isResultListFragmentOpened;
import static com.audacityit.finder.util.UtilMethods.hideSoftKeyboard;
import static com.audacityit.finder.util.UtilMethods.isDeviceCallSupported;
import static com.audacityit.finder.util.UtilMethods.loadJSONFromAsset;
import static com.audacityit.finder.util.UtilMethods.showHotLineCallDialog;
import static com.audacityit.finder.util.UtilMethods.showSoftKeyboard;

/**
 * @author Audacity IT Solutions Ltd.
 * @class HomeActivity
 * @brief Fragment holder activity responsible for all fragment transition and control flow
 */

public class HomeActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        HomeFragment.CategorySelectionCallbacks,
        SubCategoryFragment.SubCategorySelectionCallbacks,
        ResultListFragment.ResultListCallbacks,
        SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    public static int selectedDistrictId = 1;
    public static String selectedArea = "All Area";
    private int navigationDepth = 0;
    private SearchView mSearchView;
    private List<String> suggestions;
    private String mCurrentSearchText;
    private List<District> districtList;
    private List<String> areaList;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private FragmentManager fragmentManager;
    private CharSequence mTitle;
    private String subCategoryTitle = null;
    private String resultListTitle = null;
    private String detailViewTitle = null;
    private String searchQueryTitle = null;
    private boolean isPopupWindowNeedToUpdate = false;
    private boolean isResultListNeedToUpdate = false;
    private RelativeLayout filterLayout;
    private Spinner spinnerDistrict;
    private Spinner spinnerArea;
    private TextView buttonApply;
    private ListView suggestionListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setSearchableInfo(searchableInfo);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mSearchView.setQueryHint(Html.fromHtml("<small><small>" + getString(R.string.search_hint) + "</small></small>"));
        int searchPlateId = mSearchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = mSearchView.findViewById(searchPlateId);
        searchPlate.setBackgroundColor(Color.TRANSPARENT);
        int submitAreaId = mSearchView.getContext().getResources().getIdentifier("android:id/submit_area", null, null);
        View submitArea = mSearchView.findViewById(submitAreaId);
        submitArea.setBackgroundColor(Color.TRANSPARENT);
        int searchImgIcon = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView searchImgView = (ImageView) mSearchView.findViewById(searchImgIcon);
        searchImgView.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        searchImgView.setVisibility(View.GONE);
        int closeButtonId = mSearchView.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) mSearchView.findViewById(closeButtonId);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setQuery(null, false);
                UtilMethods.hideSoftKeyboard(HomeActivity.this);
            }
        });

        suggestionListView = (ListView) findViewById(R.id.suggestionListView);
        suggestionListView.setOnItemClickListener(this);

        findViewById(R.id.blankLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSearchFilerShowing()) {
                    findViewById(R.id.blankLayout).setVisibility(View.GONE);
                    filterLayout.setVisibility(View.GONE);
                    return;
                }
            }
        });
        initSearchFilter();
        onNavigationDrawerItemSelected(0);
    }


    @Override
    public void onBackPressed() {

        if (isSearchFilerShowing()) {
            findViewById(R.id.blankLayout).setVisibility(View.GONE);
            filterLayout.setVisibility(View.GONE);
            return;
        }

        if (mSearchView.getVisibility() == View.VISIBLE) {
            hideSearchView();
            return;
        }

        switch (navigationDepth) {
            case 1:
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment.newInstance(0))
                        .commit();
                if (navigationDepth > 0) {
                    navigationDepth--;
                }
                isResultListFragmentOpened = false;
                getSupportActionBar().setTitle(getTitle());
                break;
            case 2:
                if (TextUtils.isEmpty(SubCategoryFragment.catId)) {
                    SubCategoryFragment.catId = "1";
                }
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SubCategoryFragment.newInstance(SubCategoryFragment.catId))
                        .commit();
                if (navigationDepth > 0)
                    navigationDepth--;
                if (subCategoryTitle != null)
                    getSupportActionBar().setTitle(subCategoryTitle);
                break;
            case 3:
                if (TextUtils.isEmpty(ResultListFragment.catId)) {
                    ResultListFragment.catId = "1";
                }
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ResultListFragment.newInstance(ResultListFragment.catId))
                        .commit();
                if (navigationDepth > 0)
                    navigationDepth--;
                if (resultListTitle != null)
                    getSupportActionBar().setTitle(resultListTitle);
                break;
            case 4:
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment.newInstance(0))
                        .commit();
                navigationDepth = 0;
                getSupportActionBar().setTitle(getTitle());
                isResultListFragmentOpened = false;
                break;
            case 5:
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ResultListFragment.newInstance("", ResultListFragment.searchTerm))
                        .commit();
                if (navigationDepth > 0)
                    navigationDepth--;
                getSupportActionBar().setTitle(ResultListFragment.searchTerm);
                break;

            default:
                super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mSearchView.setQuery(query, false);
            mSearchView.clearFocus();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (mSearchView.getVisibility() == View.VISIBLE) {
            hideSearchView();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment.newInstance(position))
                .commit();
        navigationDepth = 0;
        isResultListFragmentOpened = false;
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        if (navigationDepth == 0)
            actionBar.setTitle(getTitle());
        else if (navigationDepth == 1)
            actionBar.setTitle(subCategoryTitle);
        else if (navigationDepth == 2)
            actionBar.setTitle(resultListTitle);
        else if (navigationDepth == 3)
            actionBar.setTitle(detailViewTitle);
        else if (navigationDepth == 4)
            actionBar.setTitle(searchQueryTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isSearchFilerShowing()) {
            findViewById(R.id.blankLayout).setVisibility(View.GONE);
            filterLayout.setVisibility(View.GONE);
            return false;
        }

        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.menu, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mSearchView.isFocused()) {
            mSearchView.setFocusable(false);
        }
        hideSoftKeyboard(HomeActivity.this);
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                if (!isSearchFilerShowing() && !mNavigationDrawerFragment.isDrawerOpen())
                    showHideSearchView();
                break;
            case R.id.action_call:
                hideSearchView();
                if (isDeviceCallSupported(this)) {
                    showHotLineCallDialog(this, getResources().getString(R.string.hotline_call_heading),
                            getResources().getString(R.string.hotline_call_text),
                            getResources().getString(R.string.yes_string),
                            getResources().getString(R.string.no_string));
                }
                break;
            case R.id.home:
                return super.onOptionsItemSelected(item);
            case R.id.action_filter:
                if (!mNavigationDrawerFragment.isDrawerOpen()) {
                    hideSearchView();
                    if (isSearchFilerShowing()) {
                        findViewById(R.id.blankLayout).setVisibility(View.GONE);
                        filterLayout.setVisibility(View.GONE);
                    } else {
                        showSearchFilter();
                    }
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showHideSearchView() {
        if (mSearchView.getVisibility() == View.VISIBLE) {
            mSearchView.setFocusable(false);
            mSearchView.setVisibility(View.GONE);
            hideSoftKeyboard(this);
        } else {
            mSearchView.setVisibility(View.VISIBLE);
            mSearchView.setQuery("", false);
            mSearchView.setFocusable(true);
            mSearchView.requestFocus();
            showSoftKeyboard(this);
        }
    }

    private void hideSearchView() {
        if (mSearchView.getVisibility() == View.VISIBLE) {
            mSearchView.setVisibility(View.GONE);
        }
        hideSuggestionList();
    }

    private void hideSuggestionList() {
        if (suggestionListView != null && suggestionListView.getVisibility() == View.VISIBLE) {
            suggestionListView.setAdapter(null);
            suggestionListView.setVisibility(View.GONE);
        }
    }


    private void initSearchFilter() {
        filterLayout = (RelativeLayout) findViewById(R.id.filterLayout);
        spinnerDistrict = (Spinner) findViewById(R.id.spinnerDistrict);
        spinnerArea = (Spinner) findViewById(R.id.spinnerArea);
        buttonApply = (TextView) findViewById(R.id.buttonApply);
        filterLayout.setVisibility(View.GONE);

        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.blankLayout).setVisibility(View.GONE);
                filterLayout.setVisibility(View.GONE);
                if (isResultListFragmentOpened && isResultListNeedToUpdate) {
                    ResultListFragment.locationChangeListener.onLocationChange();
                    isResultListNeedToUpdate = false;
                }
            }
        });
    }

    private void showSearchFilter() {
        findViewById(R.id.blankLayout).setVisibility(View.VISIBLE);
        filterLayout.setVisibility(View.VISIBLE);
        if (spinnerDistrict.getAdapter() != null) {
            return;
        }

        if (districtList.size() > 0) {
            String[] district = new String[districtList.size()];
            for (int i = 0; i < districtList.size(); i++) {
                district[i] = districtList.get(i).getTitle();
            }

            ArrayAdapter<String> adapterCity = new ArrayAdapter(this, R.layout.list_item_spinner, district);
            adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDistrict.setAdapter(adapterCity);
            spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedDistrictId = Integer.parseInt(districtList.get(position).getId());
                    isPopupWindowNeedToUpdate = true;
                    if (isResultListFragmentOpened) {
                        isResultListNeedToUpdate = true;
                    }
                    initArea(selectedDistrictId + "");
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private boolean isSearchFilerShowing() {
        if (filterLayout != null && filterLayout.getVisibility() == View.VISIBLE)
            return true;
        else
            return false;
    }

    private void setAreaSpinner() {
        if (areaList.size() > 0) {
            ArrayAdapter<String> areaAdapter = new ArrayAdapter(this, R.layout.list_item_spinner, areaList);
            areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerArea.setAdapter(areaAdapter);
            spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    isResultListNeedToUpdate = true;
                    selectedArea = areaList.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }


    @Override
    public void onCategorySelected(String catID, String title) {
        hideSearchView();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, SubCategoryFragment.newInstance(catID))
                .commit();
        navigationDepth++;
        getSupportActionBar().setTitle(title);
        subCategoryTitle = title;
    }

    @Override
    public void onSubCategorySelected(String catID, String title) {
        hideSearchView();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ResultListFragment.newInstance(catID))
                .commit();
        navigationDepth++;
        getSupportActionBar().setTitle(title);
        resultListTitle = title;
    }

    @Override
    public void onResultItemSelected(Item itemDetails) {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, DetailViewFragment.newInstance(itemDetails))
                .commit();
        hideSearchView();
        navigationDepth++;
        getSupportActionBar().setTitle(itemDetails.getTitle());
        detailViewTitle = itemDetails.getTitle();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        hideSearchView();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ResultListFragment.newInstance("", suggestions.get(position)))
                .commit();
        navigationDepth = 4;
        getSupportActionBar().setTitle(suggestions.get(position));
        searchQueryTitle = suggestions.get(position);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchView.clearFocus();
        hideSearchView();
        UtilMethods.hideSoftKeyboard(this);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ResultListFragment.newInstance("", query))
                .commit();
        navigationDepth = 4;
        getSupportActionBar().setTitle(query);
        searchQueryTitle = query;
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() == 0) {
            hideSuggestionList();
            UtilMethods.hideSoftKeyboard(this);
            mSearchView.clearFocus();
            mCurrentSearchText = null;
            return false;
        }
        if (!TextUtils.isEmpty(newText)) {
            getSearchSuggestions(newText);
            mCurrentSearchText = newText;
            return true;
        }
        return false;
    }

    // Search
    private void showSuggestions() {
        if (suggestions != null && suggestions.size() > 0 && !TextUtils.isEmpty(mCurrentSearchText)) {
            if (suggestionListView.getVisibility() == View.GONE) {
                suggestionListView.setVisibility(View.VISIBLE);
            }
            suggestionListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, suggestions) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView textView = (TextView) view.findViewById(android.R.id.text1);
                    if (suggestions != null && suggestions.size() > 0 && !TextUtils.isEmpty(mCurrentSearchText)) {
                        String text = suggestions.get(position);
                        int startIndex = -1;
                        int endIndex = -1;
                        if (!TextUtils.isEmpty(mCurrentSearchText)) {
                            startIndex = text.toLowerCase().indexOf(mCurrentSearchText.toLowerCase());
                            endIndex = (startIndex + mCurrentSearchText.length() < text.length()) ?
                                    startIndex + mCurrentSearchText.length() : text.length();
                        }
                        if (startIndex >= 0 && endIndex >= 0) {
                            SpannableStringBuilder sb = new SpannableStringBuilder(suggestions.get(position));
                            ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.green_text_color));
                            sb.setSpan(fcs, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                            sb.setSpan(bss, startIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            textView.setText(sb);
                        } else {
                            textView.setText(suggestions.get(position));
                        }
                    }
                    return view;
                }
            });
        }
    }

    private void getSearchSuggestions(String query) {
        String jsonString = loadJSONFromAsset(this, "get_suggestion_list");
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            int arraySize = jsonArray.length();
            if (arraySize > 0) {
                suggestions = new ArrayList<String>();
                for (int i = 0; i < arraySize; i++) {
                    suggestions.add(jsonArray.getString(i));
                }
            } else {
                suggestions = null;
            }

            if (suggestions != null && suggestions.size() > 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showSuggestions();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideSuggestionList();
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (districtList == null)
            initDistrictFilter();
    }

    private void initDistrictFilter() {
        String jsonString = loadJSONFromAsset(this, "get_district");
        try {
            final JSONArray jsonArray = new JSONArray(jsonString);
            districtList = new ArrayList<District>();
            for (int i = 0; i < jsonArray.length(); i++) {
                districtList.add(new District(jsonArray.getJSONObject(i).getJSONObject(KEY_DISTRICT_2).getString(JF_ID),
                        jsonArray.getJSONObject(i).getJSONObject(KEY_DISTRICT_2).getString(JF_TITLE)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void initArea(String id) {
        String jsonString = loadJSONFromAsset(this, "get_area");
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            areaList = new ArrayList<String>();
            areaList.add("All Area");
            for (int i = 0; i < jsonArray.length(); i++) {
                areaList.add(jsonArray.getJSONObject(i).getJSONObject(JF_ENTRY).getString(JF_AREA));
            }

            if (isPopupWindowNeedToUpdate) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAreaSpinner();
                        isPopupWindowNeedToUpdate = false;
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

}