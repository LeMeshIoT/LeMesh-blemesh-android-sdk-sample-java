package com.example.leshidemo1.ui.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leshidemo1.R;
import com.example.leshidemo1.ui.adapter.DeviceItemAdapter;
import com.example.leshidemo1.ui.adapter.MyBaseRVAdapter;

import java.util.List;

import cn.lelight.leiot.data.bean.DeviceBean;


/**
 * 选中设备弹框
 */

public class CheckDevicePopWindow {
    private View rootView;
    private PopupWindow popupWindow;
    private TextView tvTitle;//标题
    private RecyclerView rv_device;
    private Context context;
    private String title;
    private List<DeviceBean> deviceBeanList;
    private DeviceItemAdapter deviceItemAdapter;
    public CheckDevicePopWindow(Context context, String tile, List<DeviceBean> deviceBeanList) {
        this.context = context;
        this.deviceBeanList = deviceBeanList;
        this.title = tile;
        deviceItemAdapter = new DeviceItemAdapter(context, deviceBeanList,false,false);
        initView();
    }

    private void initView() {
        rootView = LayoutInflater.from(context).inflate(R.layout.check_device_popuwindow, null);
        tvTitle = rootView.findViewById(R.id.tvTitle);
        rv_device = rootView.findViewById(R.id.rv_device);
        //设备
        LinearLayoutManager deviceManager = new LinearLayoutManager(context) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };
        rv_device.setLayoutManager(deviceManager);
        rv_device.setAdapter(deviceItemAdapter);
    }

    public void setDeviceBeanList(List<DeviceBean> deviceBeanList) {
        this.deviceBeanList = deviceBeanList;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private boolean isDelete;//是否显示删除设备

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public void showPopWindow() {
        deviceItemAdapter.setDelete(isDelete);
        tvTitle.setText(title);
        popupWindow = new PopupWindow(rootView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        //popupWindow.setAnimationStyle(R.style.popwindow_anim_style);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        deviceItemAdapter.updateList(this.deviceBeanList);
        deviceItemAdapter.setOnItemClickListeners(new MyBaseRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (setPopWindowClick != null){
                    setPopWindowClick.onClickPopWindow(position,deviceBeanList.get(position));
                }
            }
        });
    }

    public void closePopWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        } else {
            return;
        }
    }

    private OnPopWindowClick setPopWindowClick;

    public void setPopWindowClick(OnPopWindowClick setPopWindowClick) {
        this.setPopWindowClick = setPopWindowClick;
    }

    public interface OnPopWindowClick {
        void onClickPopWindow(int position,DeviceBean deviceBean);
    }



}
