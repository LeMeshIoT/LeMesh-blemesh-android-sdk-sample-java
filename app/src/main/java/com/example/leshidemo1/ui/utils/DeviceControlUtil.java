package com.example.leshidemo1.ui.utils;

import com.example.leshidemo1.MyApplication;

import cn.lelight.leiot.data.bean.DeviceBean;
import cn.lelight.leiot.data.bean.GroupBean;
import cn.lelight.leiot.data.bean.base.DpBean;
import cn.lelight.leiot.data.leenum.DeviceType;
import cn.lelight.leiot.data.leenum.dps.LightDp;
import cn.lelight.leiot.sdk.api.IGroupManger;
import cn.lelight.leiot.sdk.api.callback.IControlCallback;

/**
 * 设备控制类
 */
public class DeviceControlUtil {
    private volatile static DeviceControlUtil deviceControlUtil;
    /**
     * 获取实例
     * @return MyApplication
     */
    public static DeviceControlUtil getInstance() {
        if (deviceControlUtil == null) {
            synchronized (DeviceControlUtil.class) {
                if (deviceControlUtil == null) {
                    deviceControlUtil = new DeviceControlUtil();
                }
            }
        }
        return deviceControlUtil;
    }



    /**
     *
     * @param deviceBean 设备实体类
     * @param type 1:开 2:关 3:亮度 4:色温
     */
    public void deviceSend(DeviceBean deviceBean,int type,int num){
        if (deviceBean == null){return;}
        switch (type){
            case 1:
                // 开灯
                deviceBean.sendDp(new DpBean(LightDp.POWER.getDpId(), LightDp.POWER.getType(),true), new IControlCallback() {
                    @Override
                    public void onSuccess() {
                        MyApplication.getInstance().toast("打开成功!");
                    }

                    @Override
                    public void onFail(int i, String s) {
                        MyApplication.getInstance().toast("打开失败:"+s);
                    }
                });
                break;
            case 2:
                // 关灯
                deviceBean.sendDp(new DpBean(LightDp.POWER.getDpId(), LightDp.POWER.getType(),false), new IControlCallback() {
                    @Override
                    public void onSuccess() {
                        MyApplication.getInstance().toast("关闭成功!");
                    }

                    @Override
                    public void onFail(int i, String s) {
                        MyApplication.getInstance().toast("关闭失败:"+s);
                    }
                });
                break;
            case 3:
                // 亮度
                deviceBean.sendDp(new DpBean(LightDp.BRIGHT.getDpId(), LightDp.BRIGHT.getType(),num), new IControlCallback() {
                    @Override
                    public void onSuccess() {
                        MyApplication.getInstance().toast("调节亮度成功!");
                    }

                    @Override
                    public void onFail(int i, String s) {
                        MyApplication.getInstance().toast("调节亮度失败:"+s);
                    }
                });
                break;
            case 4://色温
                deviceBean.sendDp(new DpBean(LightDp.CCT.getDpId(), LightDp.CCT.getType(),num), new IControlCallback() {
                    @Override
                    public void onSuccess() {
                        MyApplication.getInstance().toast("调节色温成功!");
                    }

                    @Override
                    public void onFail(int i, String s) {
                        MyApplication.getInstance().toast("调节色温失败:"+s);
                    }
                });
                break;
        }
    }

}
