package com.audacityit.finder.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.audacityit.finder.R;
import com.audacityit.finder.adapter.CustomDialogAdapter;
import com.audacityit.finder.model.District;
import com.audacityit.finder.util.FloatLabel;
import com.audacityit.finder.util.UtilMethods;
import com.audacityit.finder.util.UtilMethods.InternetConnectionListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.audacityit.finder.util.Constants.JF_ID;
import static com.audacityit.finder.util.Constants.JF_TITLE;
import static com.audacityit.finder.util.Constants.KEY_DISTRICT_2;
import static com.audacityit.finder.util.UtilMethods.hideSoftKeyboard;
import static com.audacityit.finder.util.UtilMethods.isConnectedToInternet;
import static com.audacityit.finder.util.UtilMethods.loadJSONFromAsset;
import static com.audacityit.finder.util.Validator.isInputted;
import static com.audacityit.finder.util.Validator.isMobileNumberValid;
import static com.audacityit.finder.util.Validator.isValidEmail;
import static com.audacityit.finder.util.Validator.setPhoneCodeListener;

/**
 * @author Audacity IT Solutions Ltd.
 * @class PostBusinessActivity
 * @brief Activity for post a new business
 */

public class PostBusinessActivity extends Activity implements View.OnClickListener, InternetConnectionListener {

    private final int DISTRICT_LIST_ACTION = 1;
    private final int POST_BUSINESS_ACTION = 2;
    private FloatLabel etCompanyName;
    private FloatLabel etContactPersonName;
    private FloatLabel etDistrictName;
    private FloatLabel etMobileNumber;
    private FloatLabel etEmail;
    private boolean isUserCanceled = false;
    private InternetConnectionListener internetConnectionListener;
    private List<District> districtList;
    private AlertDialog districtDialog = null;
    private String selectedDistrictId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_business);
        findViewById(R.id.crossImgView).setOnClickListener(this);
        etCompanyName = (FloatLabel) findViewById(R.id.etCompanyName);
        etContactPersonName = (FloatLabel) findViewById(R.id.etContactPersonName);
        etDistrictName = (FloatLabel) findViewById(R.id.etDistrictName);
        etMobileNumber = (FloatLabel) findViewById(R.id.etMobileNumber);
        etEmail = (FloatLabel) findViewById(R.id.etEmail);
        this.findViewById(R.id.btnSubmit).setOnClickListener(this);

        etCompanyName.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        etContactPersonName.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        etCompanyName.getEditText().setTextColor(getResources().getColor(R.color.post_business_edit_text_color));
        etContactPersonName.getEditText().setTextColor(getResources().getColor(R.color.post_business_edit_text_color));
        etDistrictName.getEditText().setTextColor(getResources().getColor(R.color.post_business_edit_text_color));
        etMobileNumber.getEditText().setTextColor(getResources().getColor(R.color.post_business_edit_text_color));
        etEmail.getEditText().setTextColor(getResources().getColor(R.color.post_business_edit_text_color));
        etMobileNumber.getEditText().setOnFocusChangeListener(setPhoneCodeListener(this));
        etDistrictName.getEditText().setCursorVisible(false);
        etDistrictName.getEditText().setKeyListener(null);
        etDistrictName.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (districtList != null && districtList.size() > 0) {
                    String[] districts = new String[districtList.size()];
                    for (int i = 0; i < districtList.size(); i++) {
                        districts[i] = districtList.get(i).getTitle();
                    }
                    showDistrictDialog(PostBusinessActivity.this, districts);
                }
            }
        });

    }

    private void showDistrictDialog(Context context, String[] districts) {
        hideSoftKeyboard(this);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_list_content_dialog, null);
        TextView headline = (TextView) view.findViewById(R.id.headlineTV);
        headline.setVisibility(View.GONE);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(new CustomDialogAdapter(context, districts));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etDistrictName.getEditText().setText(districtList.get(position).getTitle());
                selectedDistrictId = districtList.get(position).getId();
                districtDialog.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true);

        districtDialog = builder.create();
        districtDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        initDistrictList();
    }

    private void initDistrictList() {
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

    @Override
    protected void onPause() {
        super.onPause();
        if (isUserCanceled) {
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            finish();
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnSubmit:
                if (inputValid()) {
                    if (isConnectedToInternet(PostBusinessActivity.this)) {
                        //TODO: network call
                        initPostBusinessRequest();
                    } else {
                        internetConnectionListener = PostBusinessActivity.this;
                        UtilMethods.showNoInternetDialog(PostBusinessActivity.this, internetConnectionListener, getResources().getString(R.string.no_internet),
                                getResources().getString(R.string.no_internet_text),
                                getResources().getString(R.string.retry_string),
                                getResources().getString(R.string.cancel_string), POST_BUSINESS_ACTION);
                    }
                }
                break;

            case R.id.crossImgView:
                hideSoftKeyboard(this);
                isUserCanceled = true;
                onPause();
                break;

        }
    }

    private void initPostBusinessRequest() {
        Toast.makeText(this, getResources().getString(R.string.post_business_successful), Toast.LENGTH_SHORT).show();
        hideSoftKeyboard(this);
        isUserCanceled = true;
        onPause();
    }

    private boolean inputValid() {

        if (!isInputted(this, etCompanyName)) {
            return false;
        }

        if (!isInputted(this, etContactPersonName)) {
            return false;
        }

        if (!isInputted(this, etDistrictName)) {
            return false;
        }

        if (!isInputted(this, etMobileNumber)) {
            return false;
        }


        if (!isMobileNumberValid(this, etMobileNumber)) {
            return false;
        }

        if (!isInputted(this, etEmail)) {
            return false;
        }

        if (!isValidEmail(this, etEmail)) {
            return false;
        }

        return true;
    }

    @Override
    public void onConnectionEstablished(int code) {
        if (code == DISTRICT_LIST_ACTION) {
            initDistrictList();
        } else if (code == POST_BUSINESS_ACTION) {
            initPostBusinessRequest();
        }
    }

    @Override
    public void onUserCanceled(int code) {

    }
}
