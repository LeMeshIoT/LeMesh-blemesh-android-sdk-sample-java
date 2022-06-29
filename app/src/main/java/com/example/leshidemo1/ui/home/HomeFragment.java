package com.example.leshidemo1.ui.home;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.leshidemo1.MyApplication;
import com.example.leshidemo1.R;
import com.example.leshidemo1.databinding.FragmentHomeBinding;
import com.example.leshidemo1.ui.device.AddDeviceAct;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.List;

import cn.lelight.leiot.data.bean.DeviceBean;
import cn.lelight.leiot.data.leenum.ModuleType;
import cn.lelight.leiot.sdk.LeHomeSdk;
import cn.lelight.leiot.sdk.api.IBleLeMeshManger;
import cn.lelight.leiot.sdk.api.IDataManger;
import cn.lelight.leiot.sdk.api.callback.IDeleteDeviceCallback;
import cn.lelight.leiot.sdk.api.callback.lemesh.LeMeshInitCallback;
import cn.lelight.leiot.sdk.core.InitCallback;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private View root;

    private RxPermissions rxPermissions;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        if (binding == null) {
            Toast.makeText(getContext(), "视图绑定失败", Toast.LENGTH_SHORT).show();
        }
        rxPermissions = new RxPermissions(this); // where this is an Activity or Fragment instance
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    Log.e("TAG", "权限-------->" + granted);
                    if (granted) {
                        initLeMesh();
                    }
                });
        initView();
        return root;
    }

    private void initView() {
        binding.textVer.setText("ver:" + LeHomeSdk.getBleLeMeshManger().getVersion());
        binding.addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddDeviceAct.class);
                startActivity(intent);
            }
        });
    }

    private void initLeMesh() {
        IBleLeMeshManger iBleLeMeshManger = LeHomeSdk.getBleLeMeshManger();
        if (iBleLeMeshManger == null) {
            binding.textBlemesh.setText("blemesh初始化:未知错误");
            return;
        }
        iBleLeMeshManger.initPlugin(getContext(), new LeMeshInitCallback() {
            @Override
            public void onResult(int result) {
                switch (result) {
                    case 0://sdk 初始化成功
                        binding.textBlemesh.setText("blemesh初始化:完成");
                        break;
                    case 1://不支持蓝牙
                        binding.textBlemesh.setText("blemesh初始化:不支持蓝牙");
                        break;
                    case 2://蓝牙未开启
                        binding.textBlemesh.setText("blemesh初始化:蓝牙未开启");
                        break;
                    case 3://不支持蓝牙广播
                        binding.textBlemesh.setText("blemesh初始化:不支持蓝牙广播");
                        break;
                    case 4://未知错误
                        binding.textBlemesh.setText("blemesh初始化:未知错误");
                        break;
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}