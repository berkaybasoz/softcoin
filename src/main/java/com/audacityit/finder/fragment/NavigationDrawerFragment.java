package com.audacityit.finder.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.audacityit.finder.R;
import com.audacityit.finder.activity.ChangePasswordActivity;
import com.audacityit.finder.activity.PostBusinessActivity;
import com.audacityit.finder.activity.ProfileUpdateActivity;
import com.audacityit.finder.activity.SignInActivity;
import com.audacityit.finder.activity.SignUpActivity;
import com.audacityit.finder.adapter.NavigationAdapter;

import static com.audacityit.finder.util.Constants.FINDER_HOTLINE;
import static com.audacityit.finder.util.Constants.URL_FACEBOOK;
import static com.audacityit.finder.util.Constants.URL_GOOGLE_PLUS;
import static com.audacityit.finder.util.Constants.URL_LINKEDIN;
import static com.audacityit.finder.util.Constants.URL_TWITTER;
import static com.audacityit.finder.util.Constants.URL_YOUTUBE;
import static com.audacityit.finder.util.Constants.isResultListFragmentOpened;
import static com.audacityit.finder.util.UtilMethods.browseUrl;
import static com.audacityit.finder.util.UtilMethods.isUserSignedIn;
import static com.audacityit.finder.util.UtilMethods.phoneCall;
import static com.audacityit.finder.util.UtilMethods.showExitDialog;

/**
 * @author Audacity IT Solutions Ltd.
 * @class NavigationDrawerFragment
 * @brief Fragment for showing left drawer with dynamic options based on sign up/ sing in or log out state
 */

public class NavigationDrawerFragment extends Fragment {

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private NavigationDrawerCallbacks mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ExpandableListView navigationListView;
    private View mFragmentContainerView;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        navigationListView = (ExpandableListView) rootView.findViewById(R.id.navigationListView);

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mDrawerLayout != null) {
                        mDrawerLayout.closeDrawer(mFragmentContainerView);
                        return true;
                    } else
                        return false;
                } else
                    return false;

            }
        });

        /**
         * if user is not logged in signInSignUpView will be visible
         * to modify the layout go to res > layout > fragment_navigation_drawer
         */
        if (!isUserSignedIn(getActivity()))
            rootView.findViewById(R.id.signInSignUpView).setVisibility(View.VISIBLE);

        rootView.findViewById(R.id.logoImgView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.onNavigationDrawerItemSelected(0);
                isResultListFragmentOpened = false;
                closeDrawer();
            }
        });

        rootView.findViewById(R.id.btnCallNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                phoneCall(getActivity(), FINDER_HOTLINE);
            }
        });

        rootView.findViewById(R.id.btnPostBusiness).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PostBusinessActivity.class));
                closeDrawer();
            }
        });

        rootView.findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SignUpActivity.class));
                closeDrawer();
            }
        });

        rootView.findViewById(R.id.btnSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SignInActivity.class));
                closeDrawer();
            }
        });

        navigationListView.setAdapter(new NavigationAdapter(getActivity()));
        navigationListView.setIndicatorBounds(navigationListView.getRight() - 40, navigationListView.getWidth());
        navigationListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (groupPosition == 0) {
                    mCallbacks.onNavigationDrawerItemSelected(0);
                    isResultListFragmentOpened = false;
                    closeDrawer();
                }

                if (isUserSignedIn(getActivity()) && groupPosition == 3) {
                    closeDrawer();
                    showExitDialog(getActivity(), getResources().getString(R.string.exit_heading),
                            getResources().getString(R.string.exit_text),
                            getResources().getString(R.string.yes_string),
                            getResources().getString(R.string.no_string));

                }
                return false;
            }
        });

        navigationListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (isUserSignedIn(getActivity())) {

                    if (groupPosition == 1) {

                        if (childPosition == 0) {
                            startActivity(new Intent(getActivity(), ProfileUpdateActivity.class));
                            closeDrawer();
                        } else {
                            startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                            closeDrawer();
                        }

                    } else if (groupPosition == 2) {

                        if (childPosition == 0) {
                            browseUrl(getActivity(), URL_FACEBOOK);
                            closeDrawer();
                        } else if (childPosition == 1) {
                            browseUrl(getActivity(), URL_TWITTER);
                            closeDrawer();
                        } else if (childPosition == 2) {
                            browseUrl(getActivity(), URL_GOOGLE_PLUS);
                            closeDrawer();
                        } else if (childPosition == 3) {
                            browseUrl(getActivity(), URL_YOUTUBE);
                            closeDrawer();
                        } else if (childPosition == 4) {
                            browseUrl(getActivity(), URL_LINKEDIN);
                            closeDrawer();
                        }
                    }
                } else {

                    if (groupPosition == 1) {

                        if (childPosition == 0) {
                            browseUrl(getActivity(), URL_FACEBOOK);
                            closeDrawer();
                        } else if (childPosition == 1) {
                            browseUrl(getActivity(), URL_TWITTER);
                            closeDrawer();
                        } else if (childPosition == 2) {
                            browseUrl(getActivity(), URL_GOOGLE_PLUS);
                            closeDrawer();
                        } else if (childPosition == 3) {
                            browseUrl(getActivity(), URL_YOUTUBE);
                            closeDrawer();
                        } else if (childPosition == 4) {
                            browseUrl(getActivity(), URL_LINKEDIN);
                            closeDrawer();
                        }
                    }
                }

                return false;
            }
        });
        return rootView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close

        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }
                getActivity().supportInvalidateOptionsMenu();
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.menu, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private void closeDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    //! interface for listening navigation list item click
    public interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(int position);
    }
}
