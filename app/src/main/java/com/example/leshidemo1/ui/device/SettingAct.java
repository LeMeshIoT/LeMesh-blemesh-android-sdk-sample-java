package com.example.leshidemo1.ui.device;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.leshidemo1.databinding.ActDimmingBinding;
import com.example.leshidemo1.databinding.ActSettingBinding;
import com.example.leshidemo1.ui.adapter.MyBaseRVAdapter;
import com.example.leshidemo1.ui.adapter.SettingItemAdapter;
import com.example.leshidemo1.ui.bean.DpsBean;
import com.example.leshidemo1.ui.utils.ExpandViewClick;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.lelight.leiot.data.bean.DeviceBean;
import cn.lelight.leiot.data.leenum.DeviceType;
import cn.lelight.leiot.data.leenum.dps.CurtainDp;
import cn.lelight.leiot.data.leenum.dps.LightDp;
import cn.lelight.leiot.data.leenum.dps.SwitchDp;
import cn.lelight.leiot.sdk.LeHomeSdk;
import cn.lelight.leiot.sdk.api.IDataManger;

public class SettingAct extends Activity {
    private ActSettingBinding binding;

    private IDataManger dataManger;
    private DeviceBean deviceBean;
    private List<DpsBean> dpsBeanList = new ArrayList<>();
    private SettingItemAdapter itemAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dataManger = LeHomeSdk.getDataManger();
        if (dataManger == null){ finish();return; }
        String id = getIntent().getStringExtra("ID");
        deviceBean = dataManger.getDeviceBean(id);
        if (deviceBean == null){ finish();return; }
        initData();
        initView();
    }


    private void initData() {
        if (deviceBean.getType() == DeviceType.Light.getType()) {
            for (LightDp lightDp : LightDp.values()){
                String value = "无";
                if (deviceBean.getDps().containsKey(lightDp.getDpId())){
                    value = deviceBean.getDps().get(lightDp.getDpId()).toString()+"";
                }
                DpsBean dpsBean = new DpsBean(lightDp.getDpId(),lightDp.getType(),lightDp.getName(),value);
                dpsBeanList.add(dpsBean);
            }
        }else if (deviceBean.getType() == DeviceType.Curtain.getType()) {
            for (CurtainDp curtainDp : CurtainDp.values()){
                String value = "无";
                if (deviceBean.getDps().containsKey(curtainDp.getDpId())){
                    value = deviceBean.getDps().get(curtainDp.getDpId()).toString()+"";
                }
                DpsBean dpsBean = new DpsBean(curtainDp.getDpId(),curtainDp.getType(),curtainDp.getName(),value);
                dpsBeanList.add(dpsBean);
            }
        }else if (deviceBean.getType() == DeviceType.Switch.getType()) {
            for (SwitchDp switchDp : SwitchDp.values()){
                String value = "无";
                if (deviceBean.getDps().containsKey(switchDp.getDpId())){
                    value = deviceBean.getDps().get(switchDp.getDpId()).toString()+"";
                }
                DpsBean dpsBean = new DpsBean(switchDp.getDpId(),switchDp.getType(),switchDp.getName(),value);
                dpsBeanList.add(dpsBean);
            }
        }
        if (dpsBeanList == null || dpsBeanList.size() == 0){toast("获取DPS失败!");return;}
    }
    private void initView() {
        binding.headView.tvTitle.setText(deviceBean.getMac());
        itemAdapter = new SettingItemAdapter(this,deviceBean,dpsBeanList);
        //设备
        LinearLayoutManager deviceManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };
        binding.rvDps.setLayoutManager(deviceManager);
        binding.rvDps.setAdapter(itemAdapter);
        ExpandViewClick.getInstance().expendTouchArea(binding.headView.ivBack, 15);
        binding.headView.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
