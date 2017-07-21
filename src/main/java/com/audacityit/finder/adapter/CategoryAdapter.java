package com.audacityit.finder.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.audacityit.finder.R;
import com.audacityit.finder.fragment.HomeFragment;
import com.audacityit.finder.model.Category;
import com.audacityit.finder.util.UtilMethods;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Audacity IT Solutions Ltd.
 * @class CategoryAdapter
 * @brief Adapter class for populating category in list view
 */

public class CategoryAdapter extends ArrayAdapter<Category> implements View.OnClickListener {

    private final LayoutInflater inflater;
    private final ArrayList<Category> categoryList;
    private Activity activity;
    private HomeFragment.CategorySelectionCallbacks mCallbacks;
    private String dummyUrl = "http://www.howiwork.org";
    AbsListView.LayoutParams params;


    public CategoryAdapter(Activity activity, HomeFragment.CategorySelectionCallbacks mCallbacks, ArrayList<Category> categoryList) {
        super(activity, R.layout.layout_category_list);
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity.getApplicationContext());
        this.categoryList = categoryList;
        this.mCallbacks = mCallbacks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder row;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_category_list, null);
            row = new ViewHolder();
            row.bannerImage = (ImageView) convertView.findViewById(R.id.catBannerImageView);
            row.categoryImage = (ImageView) convertView.findViewById(R.id.catImageView);
            row.categoryName = (TextView) convertView.findViewById(R.id.catNameTV);
//            params = (AbsListView.LayoutParams) convertView.getLayoutParams();
//            if(params!=null) {
//                params.height = UtilMethods.getWindowSize(activity).y / 3;
//            }
            convertView.setTag(row);
        } else {
            row = (ViewHolder) convertView.getTag();
//            params = (AbsListView.LayoutParams) convertView.getLayoutParams();
//            if(params!=null) {
//                params.height = UtilMethods.getWindowSize(activity).y / 3;
//            }
        }

        Category category = categoryList.get(position);
        Picasso.with(activity).load(UtilMethods
                .getDrawableFromFileName(activity,category.getIconUrl()))
                .tag(category.getIconUrl())
                .into(row.categoryImage);
        row.categoryName.setText(category.getTitle());

        Picasso.with(activity)
                .load(UtilMethods.getDrawableFromFileName(activity,category.getImageUrl()))
                .placeholder(R.drawable.img_banner_placeholder)
                .tag(category.getIconUrl())
                .fit()
                .into(row.bannerImage);


        row.bannerImage.setOnClickListener(this);
        row.categoryImage.setTag(position);
        row.categoryName.setTag(position);
        row.bannerImage.setTag(position);
        return convertView;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public void onClick(View v) {
        int position = Integer.parseInt(v.getTag().toString());
        mCallbacks.onCategorySelected(categoryList.get(position).getId(),
                categoryList.get(position).getTitle());
    }

    private static class ViewHolder {
        public ImageView bannerImage;
        public TextView categoryName;
        public ImageView categoryImage;
    }
}
