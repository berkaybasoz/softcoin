package com.audacityit.finder.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.audacityit.finder.R;
import com.audacityit.finder.callback.ProductListCallbacks;
import com.audacityit.finder.fragment.DetailViewFragment;
import com.audacityit.finder.fragment.ResultListFragment;
import com.audacityit.finder.model.Category;
import com.audacityit.finder.model.Item;
import com.audacityit.finder.util.Constants;
import com.audacityit.finder.util.CustomRatingBar;
import com.audacityit.finder.util.UtilMethods;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.audacityit.finder.util.Constants.NO_IMAGE_FOUND;
import static com.audacityit.finder.util.UtilMethods.browseUrl;

/**
 * @author Audacity IT Solutions Ltd.
 * @class ResultListAdapter
 * @brief Adapter class for populating the business list
 */

public class ProductListAdapter extends ArrayAdapter<Category> implements View.OnClickListener {

    private final LayoutInflater inflater;
    private final ArrayList<Item> searchResultList;
    private Activity activity;
    private ProductListCallbacks mCallbacks;

    public ProductListAdapter(Activity activity, ProductListCallbacks mCallbacks, ArrayList<Item> searchResultList) {
        super(activity, R.layout.layout_result_list);
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity.getApplicationContext());
        this.searchResultList = searchResultList;
        this.mCallbacks = mCallbacks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder row;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_product_list, null);
            row = new ViewHolder();
            row.parentView = (RelativeLayout) convertView.findViewById(R.id.parentView);
            row.itemIconImgView = (ImageView) convertView.findViewById(R.id.itemIconImgView);
            row.itemTitleTV = (TextView) convertView.findViewById(R.id.itemTitleTV);
            row.itemDescriptionTV = (TextView) convertView.findViewById(R.id.itemDescriptionTV);
            row.ratingBar = (CustomRatingBar) convertView.findViewById(R.id.ratingBar);
            row.verificationImgView = (ImageView) convertView.findViewById(R.id.verificationImgView);
            row.itemPriceTV = (TextView) convertView.findViewById(R.id.itemPriceTV);
            row.parentView.setOnClickListener(this);
            convertView.setTag(row);
        } else {
            row = (ViewHolder) convertView.getTag();
        }
        Item item = searchResultList.get(position);

        /*String imageUrl = (item.getImageThumbUrls().length > 0) ? item.getImageThumbUrls()[0]
                : "";
        if (imageUrl != "") {
            Picasso.with(activity).load(imageUrl).placeholder(R.drawable.ic_placeholder).tag(imageUrl)
                    .into(row.itemIconImgView);
        }*/
        Picasso.with(activity)
                .load(UtilMethods
                        .getDrawableFromFileName(activity, item.getImageThumbUrls()[0]))
                .placeholder(R.drawable.ic_placeholder)
                .tag(item.getImageThumbUrls()[0])
                .into(row.itemIconImgView);


        row.itemPriceTV.setText(item.getPrice()+" "+ Constants.COIN);
        row.itemTitleTV.setText(item.getTitle());
        row.itemDescriptionTV.setText(item.getDescription());
        //row.ratingBar.setScore(item.getRating());
        if (item.isVerified()) {
            row.verificationImgView.setVisibility(View.VISIBLE);
        } else {
            row.verificationImgView.setVisibility(View.INVISIBLE);
        }
        row.parentView.setTag(position);
        return convertView;
    }

    @Override
    public int getCount() {
        return searchResultList.size();
    }

    @Override
    public void onClick(View v) {
        int position = Integer.parseInt(v.getTag().toString());
        if (v.getId() == R.id.parentView) {
            mCallbacks.onResultItemSelected(searchResultList.get(position));
        } else {
            browseUrl(activity, searchResultList.get(position).getAdUrl());
        }
    }

    private static class ViewHolder {
        public RelativeLayout parentView;
        public ImageView itemIconImgView;
        public TextView itemTitleTV;
        public TextView itemDescriptionTV;
        public CustomRatingBar ratingBar;
        public ImageView verificationImgView;
        public TextView itemPriceTV;
    }
}
