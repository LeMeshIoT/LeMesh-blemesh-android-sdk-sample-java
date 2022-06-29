package com.example.leshidemo1.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.leshidemo1.R;
import com.example.leshidemo1.ui.utils.InputPopWindow;

import java.util.List;

import cn.lelight.leiot.data.bean.DeviceBean;
import cn.lelight.leiot.data.bean.GroupBean;
import cn.lelight.leiot.sdk.api.callback.ICreateCallback;
import cn.lelight.leiot.sdk.api.callback.IDeleteDeviceCallback;

/**
 * 设备适配器
 */
public class DeviceItemAdapter extends MyBaseRVAdapter<DeviceBean, DeviceItemAdapter.VH> {
    private boolean isDelete;//是否显示删除设备
    private boolean isDps;//是否显示Dps
    public DeviceItemAdapter(Context context, List<DeviceBean> list,boolean isDelete,boolean isDps) {
        super(context, list);
        this.isDelete = isDelete;
        this.isDps = isDps;
    }


    /**
     * 创建
     */
    @Override
    protected VH ICreateItemHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_device_item, parent, false);
        return new VH(view);
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    /**
     * 加载
     */
    @Override
    protected void IBindItemHolder(final VH holder, final int position) {
        final DeviceBean deviceBean = mList.get(position);
        holder.tv_device_dps.setText(deviceBean.getDps().toString());
        holder.tv_device_mac.setText(deviceBean.getMac());
        holder.tv_device_dps.setVisibility(isDps ? View.VISIBLE : View.GONE);//是否显示DPS
        holder.tv_device_delete.setVisibility(isDelete ? View.VISIBLE : View.GONE);//是否显示删除按钮
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            }
        });
        holder.tv_device_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemDeleteClickListener != null){
                    mOnItemDeleteClickListener.onClick(position);
                }
            }
        });
    }
    protected OnItemDeleteClickListener mOnItemDeleteClickListener;
    /**
     * 点击修改设备数量
     * @param mOnItemDeleteClickListener
     */
    public void setOnItemDeleteClickListener(OnItemDeleteClickListener mOnItemDeleteClickListener) {
        this.mOnItemDeleteClickListener = mOnItemDeleteClickListener;
    }

    /**
     * 点击监听
     */
    public interface OnItemDeleteClickListener {
        void onClick(int position);
    }

    class VH extends RecyclerView.ViewHolder {
        private TextView tv_device_dps;
        private TextView tv_device_mac;
        private TextView tv_device_delete;
        public VH(View itemView) {
            super(itemView);
            tv_device_dps = itemView.findViewById(R.id.tv_device_dps);
            tv_device_mac = itemView.findViewById(R.id.tv_device_mac);
            tv_device_delete = itemView.findViewById(R.id.tv_device_delete);
        }
    }
}
