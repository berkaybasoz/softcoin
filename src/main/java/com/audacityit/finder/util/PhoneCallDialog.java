package com.audacityit.finder.util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.audacityit.finder.R;

import static com.audacityit.finder.util.UtilMethods.phoneCall;

/**
 * @author Audacity IT Solutions Ltd.
 * @class PhoneCallDialog
 * @brief class for showing phone call dialog with a list of phone number
 */

public class PhoneCallDialog {

    private static AlertDialog dialog = null;

    public static void showPhoneCallDialog(final Context context, final String[] numbers) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_list_content_dialog, null);
        TextView headline = (TextView) view.findViewById(R.id.headlineTV);
        headline.setText(context.getResources().getString(R.string.phone_dialog_title));
        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(new PhoneCallDialogAdapter(context, numbers));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                phoneCall(context, numbers[position]);
                dialog.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    public static class PhoneCallDialogAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private String[] numbers;

        public PhoneCallDialogAdapter(Context context, String[] numbers) {
            this.inflater = LayoutInflater.from(context);
            this.numbers = numbers;
        }

        @Override
        public int getCount() {
            return numbers.length;
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

            holder.tv.setText(numbers[position]);
            return v;
        }


        class Holder {
            private TextView tv;
        }

    }

}
