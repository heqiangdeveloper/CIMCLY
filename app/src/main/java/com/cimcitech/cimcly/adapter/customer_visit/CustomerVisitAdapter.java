package com.cimcitech.cimcly.adapter.customer_visit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.activity.home.customer_visit.CustomerVisitActivity;
import com.cimcitech.cimcly.bean.CustomerVisit;
import com.cimcitech.cimcly.utils.DateTool;

import java.util.List;

/**
 * Created by cimcitech on 2017/7/31.
 */

public class CustomerVisitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private List<CustomerVisit> data;
    private LayoutInflater inflater;
    private static final int TYPE_END = 2;
    private boolean isNotMoreData = false;

    public CustomerVisitAdapter(Context context, List<CustomerVisit> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void setNotMoreData(boolean b) {
        this.isNotMoreData = b;
    }

    public boolean isNotMoreData() {
        return isNotMoreData;
    }

    public List getAll() {
        return data;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClickListener(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.customer_visit_list_item, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = inflater.inflate(R.layout.recycler_foot_view, parent, false);
            return new FootViewHolder(view);
        } else if (viewType == TYPE_END) {
            View view = inflater.inflate(R.layout.recycler_end_view, parent, false);
            return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View view) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                        return false;
                    }
                });
            }
            final CustomerVisit item = data.get(position);
            ((ItemViewHolder) holder).user_name_tv.setText(item.getCustname() != null && !item.getCustname().equals("") ?
                    item.getCustname() : "");
            ((ItemViewHolder) holder).time_tv.setText(item.getCreatedate() > 0 ?
                    DateTool.getDateStrNoTime(item.getCreatedate()) : "");
            ((ItemViewHolder) holder).content_tv.setText(item.getVisitsummary() != null && !item.getVisitsummary().equals("") ?
                    item.getVisitsummary() + "" : "");

            if(!CustomerVisitActivity.myData){
                //((ItemViewHolder) holder).div_line.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).salesman_tv.setVisibility(View.VISIBLE);
                ((ItemViewHolder) holder).salesman_tv.setText(item.getUserName() != null && !item
                        .getUserName().equals("") ? "业务员: " + item.getUserName() + "" : "业务员: ");
            }else {
                //((ItemViewHolder) holder).div_line.setVisibility(View.GONE);
                ((ItemViewHolder) holder).salesman_tv.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            if (isNotMoreData())
                return TYPE_END;
            else
                return TYPE_FOOTER;
        } else
            return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return data.size() == 0 ? 0 : data.size() + 1;
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView user_name_tv, time_tv, content_tv,salesman_tv;
        ImageView div_line;
        public ItemViewHolder(View view) {
            super(view);
            user_name_tv = view.findViewById(R.id.user_name_tv);
            time_tv = view.findViewById(R.id.time_tv);
            content_tv = view.findViewById(R.id.content_tv);
            salesman_tv = view.findViewById(R.id.salesman_tv);
            div_line = view.findViewById(R.id.div_line);
        }
    }

    static class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }
}
