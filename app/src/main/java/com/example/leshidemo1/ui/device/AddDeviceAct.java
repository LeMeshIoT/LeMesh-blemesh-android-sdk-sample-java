package com.example.leshidemo1.ui.device;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.leshidemo1.databinding.ActAddDeviceBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.lelight.leiot.data.bean.DeviceBean;
import cn.lelight.leiot.sdk.LeHomeSdk;
import cn.lelight.leiot.sdk.api.IBleLeMeshManger;
import cn.lelight.leiot.sdk.api.callback.lemesh.LeMeshAddDeviceCallback;

/**
 * 添加设备
 */
public class AddDeviceAct extends AppCompatActivity {
    private ActAddDeviceBinding addDeviceBinding;
    private List<DeviceBean> deviceBeanList = new ArrayList<>();
    private HashMap<String,DeviceBean> map = new HashMap<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addDeviceBinding = ActAddDeviceBinding.inflate(getLayoutInflater());
        setContentView(addDeviceBinding.getRoot());
        init();
    }

    /**
     * 初始化
     */
    private IBleLeMeshManger iBleLeMeshManger;
    private void init() {
        iBleLeMeshManger = LeHomeSdk.getBleLeMeshManger();
        if (iBleLeMeshManger == null){
            finish();
            return;
        }
        iBleLeMeshManger.startAddSudDevice(new LeMeshAddDeviceCallback() {
            @Override
            public boolean foundNewSubDevice(DeviceBean deviceBean) {
                // 发现未添加设备：" + deviceBean.getMac()
                // todo 这里可以自行添加逻辑决定是否过滤某些类型的设备
                // true 需要添加
                // false 不添加
                Log.e("TAG","foundNewSubDevice:"+deviceBean.toString());
//                if (deviceBean.getDevSubType() == 1){
//                    return true;
//                }
                return true;
            }

            @Override
            public void onAddSudDeviceSuccess(DeviceBean deviceBean) {
                // "添加设备成功：" + deviceBean.getMac()
                Log.e("TAG","添加成功:"+deviceBean.toString());
                if (deviceBean != null){
                    map.put(deviceBean.devId,deviceBean);
                    deviceBeanList.add(deviceBean);
                    addDeviceBinding.tvNum.setText("添加成功:"+map.size());
                }
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iBleLeMeshManger.stopAddSudDevice();
        addDeviceBinding = null;
    }
}
