package com.audacityit.finder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.audacityit.finder.R;

/**
 * @author Audacity IT Solutions Ltd.
 * @class CustomDialogAdapter
 * @brief Adapter for showing list in dialog. Used in PostBusinessActivity
 */

public class CustomDialogAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private String[] items;

    public CustomDialogAdapter(Context context, String[] items) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        View v = convertView;
        if (convertView == null) {
            holder = new Holder();
            v = inflater.inflate(R.layout.layout_custom_dialog_item, null);
            holder.tv = (TextView) v.findViewById(R.id.tv);
            v.setTag(holder);
        } else {
            holder = (Holder) v.getTag();
        }

        holder.tv.setText(items[position]);
        return v;
    }


    class Holder {
        private TextView tv;
    }

}