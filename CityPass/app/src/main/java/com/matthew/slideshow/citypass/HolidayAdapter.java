package com.matthew.slideshow.citypass;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by John on 2016/2/25.
 */
public class HolidayAdapter extends ArrayAdapter<Holiday> {
    Context context;
    int layoutResourceId;
    Holiday data[] = null;

    public HolidayAdapter(Context context, int layoutResourceId, Holiday[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        HolidayHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new HolidayHolder();
            holder.eventDateDetail = (TextView) row.findViewById(R.id.eventDateDetail);
            holder.eventName = (TextView) row.findViewById(R.id.eventName);

            row.setTag(holder);
        } else {
            holder = (HolidayHolder) row.getTag();
        }

        Holiday holiday = data[position];
        if (holiday.type.equals("H"))
            holder.eventDateDetail.setTextColor(Color.parseColor("#ff7f7f"));
        holder.eventDateDetail.setText(holiday.date_detail);
        holder.eventName.setText(holiday.holiday_or_event_name);

        return row;
    }

    static class HolidayHolder {
        TextView eventDateDetail;
        TextView eventName;
    }
}
