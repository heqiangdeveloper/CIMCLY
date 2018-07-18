package com.cimcitech.cimcly.adapter.intention_track;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cimcitech.cimcly.R;
import com.cimcitech.cimcly.bean.CustomerVisit;
import com.cimcitech.cimcly.bean.opport_unit.OpportUnitVo;
import com.cimcitech.cimcly.utils.DateTool;

import java.util.List;

/**
 * Created by cimcitech on 2017/8/1.
 */

public class IntentionTrackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private List<OpportUnitVo.DataBean.PageInfoBean.ListBean> data;
    private LayoutInflater inflater;
    private static final int TYPE_END = 2;
    private boolean isNotMoreData = false;

    public IntentionTrackAdapter(Context context, List<OpportUnitVo.DataBean.PageInfoBean.ListBean> data) {
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
            View view = inflater.inflate(R.layout.intention_track_item_view, parent, false);
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
            final OpportUnitVo.DataBean.PageInfoBean.ListBean item = data.get(position);
            ((ItemViewHolder) holder).intentional_theme_tv.setText("意向主题：" + item.getOpportsubject());
            ((ItemViewHolder) holder).client_name_tv.setText("客户名称：" + item.getCustName());
            ((ItemViewHolder) holder).status_tv.setText("状态：" + item.getCurrentStageValue());
            ((ItemViewHolder) holder).order_number_tv.setText(item.getOpportno());
            ((ItemViewHolder) holder).order_money_tv.setText(item.getPlanmoney() + "");
            ((ItemViewHolder) holder).create_time_tv.setText("创建时间：" + DateTool.getDateStr(item.getCreatedate()));
            ((ItemViewHolder) holder).product_num_tv.setText("产品数量：" + item.getProductcount());
            ((ItemViewHolder) holder).deliver_time_tv.setText("需求交付：" + DateTool.getDateStr(item.getPlansigndate()));
            ((ItemViewHolder) holder).product_model_tv.setText("产品型号：" + item.getProductid());
            ((ItemViewHolder) holder).user_name_tv.setText("业务员：" + item.getUserName());
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
        TextView intentional_theme_tv,
                client_name_tv,
                status_tv,
                order_number_tv,
                order_money_tv,
                create_time_tv,
                product_num_tv,
                deliver_time_tv,
                product_model_tv,
                user_name_tv;

        public ItemViewHolder(View view) {
            super(view);
            intentional_theme_tv = view.findViewById(R.id.intentional_theme_tv);
            client_name_tv = view.findViewById(R.id.client_name_tv);
            status_tv = view.findViewById(R.id.status_tv);
            order_number_tv = view.findViewById(R.id.order_number_tv);
            order_money_tv = view.findViewById(R.id.order_money_tv);
            create_time_tv = view.findViewById(R.id.create_time_tv);
            product_num_tv = view.findViewById(R.id.product_num_tv);
            deliver_time_tv = view.findViewById(R.id.deliver_time_tv);
            product_model_tv = view.findViewById(R.id.product_model_tv);
            //sales_order_tv = view.findViewById(R.id.sales_order_tv);
            //order_contract_tv = view.findViewById(R.id.order_contract_tv);
            user_name_tv = view.findViewById(R.id.user_name_tv);
        }
    }

    static class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }
}