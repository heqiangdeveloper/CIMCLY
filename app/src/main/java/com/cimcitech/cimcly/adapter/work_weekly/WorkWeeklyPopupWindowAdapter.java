package com.cimcitech.cimcly.adapter.work_weekly;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cimcitech.cimcly.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cimcitech on 2017/8/1.
 */

public class WorkWeeklyPopupWindowAdapter extends BaseAdapter {

    private List<String> data;

    private LayoutInflater inflater;

    private String content;

    public WorkWeeklyPopupWindowAdapter(Context context, List<String> data, String content) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.content = content;
    }

    public List<String> getAll() {
        return data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            view = inflater.inflate(R.layout.ww_popup_menu, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.pop_item_name_tv.setText(data.get(i));
        viewHolder.pop_item_label_tv.setVisibility(View.INVISIBLE);
        if(viewHolder.pop_item_name_tv.getText().equals(content)){
            viewHolder.pop_item_name_tv.setTextColor(Color.rgb(75,181,250));//colorPrimary
            viewHolder.pop_item_label_tv.setVisibility(View.VISIBLE);
            viewHolder.pop_item_label_tv.setTextColor(Color.rgb(75,181,250));//colorPrimary
        }
        return view;
    }

    static class ViewHolder {
        @Bind(R.id.pop_item_name_tv)
        TextView pop_item_name_tv;
        @Bind(R.id.pop_item_label_tv)
        TextView pop_item_label_tv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
