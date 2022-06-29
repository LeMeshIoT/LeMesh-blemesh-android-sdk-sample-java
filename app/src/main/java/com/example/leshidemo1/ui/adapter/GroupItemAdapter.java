package com.example.leshidemo1.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.example.leshidemo1.R;

import java.util.List;

import cn.lelight.leiot.data.bean.GroupBean;

/**
 * 群组适配器
 */
public class GroupItemAdapter extends MyBaseRVAdapter<GroupBean, GroupItemAdapter.VH> {

    public GroupItemAdapter(Context context, List<GroupBean> list) {
        super(context, list);
    }
    private int groupId = -1;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    /**
     * 创建
     */
    @Override
    protected VH ICreateItemHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_group_item, parent, false);
        return new VH(view);
    }

    /**
     * 加载
     */
    @Override
    protected void IBindItemHolder(final VH holder, final int position) {
        final GroupBean groupBean = mList.get(position);
        holder.tvGroupName.setText(groupBean.getName());
        if (groupId == groupBean.getGroupId()){
            holder.tvGroupName.setTextColor(Color.RED);
            holder.tvGroupName.setTextSize(16f);
        }else{
            holder.tvGroupName.setTextColor(Color.BLACK);
            holder.tvGroupName.setTextSize(12f);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            }
        });

    }

    class VH extends RecyclerView.ViewHolder {
        private TextView tvGroupName;
        public VH(View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tv_group_name);
        }
    }
}
