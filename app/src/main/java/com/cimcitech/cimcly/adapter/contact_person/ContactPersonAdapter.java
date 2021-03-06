package com.cimcitech.cimcly.adapter.contact_person;

/**
 * Created by cimcitech on 2017/8/2.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.bean.CustomerVisit;
import com.cimcitech.cimcly.bean.MyClientVo;
import com.cimcitech.cimcly.bean.client.Contact;
import com.cimcitech.cimcly.bean.client.Customer;
import com.cimcitech.cimcly.utils.DateTool;

import java.util.List;

/**
 * Created by cimcitech on 2017/8/1.
 */

public class ContactPersonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private List<Contact> data;
    private LayoutInflater inflater;
    private static final int TYPE_END = 2;
    private boolean isNotMoreData = false;

    public ContactPersonAdapter(Context context, List<Contact> data) {
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
            View view = inflater.inflate(R.layout.contact_person_item_view, parent, false);
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
            final Contact item = data.get(position);

            ((ItemViewHolder) holder).name_tv.setText(item.getPersonname() != null && !item.getPersonname().equals("") ?
                    "姓名：" + item.getPersonname() : "姓名：");
            ((ItemViewHolder) holder).client_tv.setText(item.getCustname() != null && !item.getCustname().equals("") ?
                    "所属客户：" + item.getCustname() : "所属客户：");
            ((ItemViewHolder) holder).mobile_tv.setText(item.getContmobile() != null && !item.getContmobile().equals("") ?
                    "联系电话：" + item.getContmobile() : "联系电话：");
            ((ItemViewHolder) holder).phone_tv.setText(item.getConttel() != null && !item.getConttel().equals("") ?
                    "手机：" + item.getConttel() : "手机：");
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
        TextView name_tv, client_tv, mobile_tv, phone_tv;

        public ItemViewHolder(View view) {
            super(view);
            name_tv = view.findViewById(R.id.name_tv);
            client_tv = view.findViewById(R.id.client_tv);
            mobile_tv = view.findViewById(R.id.mobile_tv);
            phone_tv = view.findViewById(R.id.phone_tv);
        }
    }

    static class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }
}
