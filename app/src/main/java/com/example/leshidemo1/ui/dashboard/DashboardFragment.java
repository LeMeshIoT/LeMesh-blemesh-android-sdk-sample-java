package com.example.leshidemo1.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.leshidemo1.R;
import com.example.leshidemo1.databinding.FragmentDashboardBinding;
import com.example.leshidemo1.ui.adapter.DeviceItemAdapter;
import com.example.leshidemo1.ui.adapter.GroupItemAdapter;
import com.example.leshidemo1.ui.adapter.MyBaseRVAdapter;
import com.example.leshidemo1.ui.device.DimmingAct;
import com.example.leshidemo1.ui.device.SettingAct;
import com.example.leshidemo1.ui.utils.CheckDevicePopWindow;
import com.example.leshidemo1.ui.utils.DeviceControlUtil;
import com.example.leshidemo1.ui.utils.InputPopWindow;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lelight.leiot.data.bean.AllRoomBean;
import cn.lelight.leiot.data.bean.DeviceBean;
import cn.lelight.leiot.data.bean.GroupBean;
import cn.lelight.leiot.data.bean.RoomBean;
import cn.lelight.leiot.data.bean.base.DpBean;
import cn.lelight.leiot.data.leenum.DeviceType;
import cn.lelight.leiot.data.leenum.dps.LightDp;
import cn.lelight.leiot.sdk.LeHomeSdk;
import cn.lelight.leiot.sdk.api.IDataManger;
import cn.lelight.leiot.sdk.api.IGroupManger;
import cn.lelight.leiot.sdk.api.IRoomManger;
import cn.lelight.leiot.sdk.api.callback.ICreateCallback;
import cn.lelight.leiot.sdk.api.callback.IDeleteDeviceCallback;
import cn.lelight.leiot.sdk.api.callback.data.IHomeDataChangeListener;
import cn.lelight.leiot.sdk.api.callback.data.IHomeRoomGroupChangeListener;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    private GroupItemAdapter groupItemAdapter;//???????????????
    private DeviceItemAdapter deviceItemAdapter;//???????????????
    //??????
    private List<RoomBean> roomBeanList;//????????????
    private IRoomManger roomManger;
    private RoomBean myRoomBean;//????????????
    private int roomId = -1;//????????????ID
    //??????
    private List<GroupBean> groupBeanList;//????????????
    private IGroupManger groupManger;
    private GroupBean myGroupBean;//????????????
    //??????
    private List<DeviceBean> devicesList;//??????

    private HashMap<String, Object> map = new HashMap<>();//??????????????????????????????????????????

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initData();
        initView();
        return root;
    }

    private IDataManger dataManger;

    private void initData() {
        dataManger = LeHomeSdk.getDataManger();
        roomManger = LeHomeSdk.getRoomManger();
        groupManger = LeHomeSdk.getGroupManger();

        if (dataManger != null) {
            roomBeanList = dataManger.getAllRoomBeans() == null ? new ArrayList<>() : dataManger.getAllRoomBeans();//??????????????????????????????
            groupBeanList = dataManger.getAllGroupBeans() == null ? new ArrayList<>() : dataManger.getAllGroupBeans();//??????????????????????????????
            myRoomBean = roomBeanList.get(0);//?????????????????????
            devicesList = dataManger.getAllDevices();
            for (int i = 0; i < roomBeanList.size(); i++) {
                TabLayout.Tab tab = binding.tabRoom.newTab();
                tab.setText(roomBeanList.get(i).getName());
                tab.setTag(roomBeanList.get(i).getRoomId());
                binding.tabRoom.addTab(tab);
            }
            Log.e("tag", "roomBeanList---------->" + new Gson().toJson(roomBeanList));
            Log.e("tag", "groupBeanList---------->" + new Gson().toJson(groupBeanList));
            Log.e("tag", "devicesList---------->" + devicesList.toString());
            Log.e("tag", "map---------->" + map.toString());
        } else {
            toast("dataManger=null");
        }
    }

    private void initView() {
        inputPopWindow = new InputPopWindow(getContext(), "?????????????????????", "??????", true);
        checkDevicePopWindow = new CheckDevicePopWindow(getContext(), "", null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };
        //??????
        binding.rvGroups.setLayoutManager(linearLayoutManager);
        groupItemAdapter = new GroupItemAdapter(getContext(), groupBeanList);
        binding.rvGroups.setAdapter(groupItemAdapter);
        groupItemAdapter.setOnItemClickListeners(new MyBaseRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //toast("??????="+groupBeanList.get(position));

                GroupBean bean = groupBeanList.get(position);
                myGroupBean = bean;//??????????????????
                groupItemAdapter.setGroupId(bean.getGroupId());
                groupItemAdapter.updateList();

                binding.tvAllDevices.setTextColor(Color.BLACK);
                binding.tvAllDevices.setTextSize(12f);
                //??????????????????
                devicesList = groupManger.getGroupDeviceBeans(bean);
                deviceItemAdapter.updateList(devicesList);
            }
        });
        //??????
        LinearLayoutManager deviceManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };
        binding.rvDevice.setLayoutManager(deviceManager);
        deviceItemAdapter = new DeviceItemAdapter(getContext(), devicesList, true, true);
        binding.rvDevice.setAdapter(deviceItemAdapter);
        deviceItemAdapter.setOnItemClickListeners(new MyBaseRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //?????????????????????
                Intent intent;
//                switch (devicesList.get(position).getDevSubType()){
//                    case 2:
//                        intent = new Intent(getContext(), DimmingAct.class);
//                        break;
//                    default:
//                        intent = new Intent(getContext(), SettingAct.class);
//                        break;
//                }
                intent = new Intent(getContext(), SettingAct.class);
                intent.putExtra("ID", devicesList.get(position).getDevId());
                startActivity(intent);
            }
        });
        deviceItemAdapter.setOnItemDeleteClickListener(new DeviceItemAdapter.OnItemDeleteClickListener() {
            @Override
            public void onClick(int position) {
                inputPopWindow.setTitle("???????????????????");
                inputPopWindow.setInput(false);
                inputPopWindow.showPopWindow();
                inputPopWindow.setPopWindowClick(new InputPopWindow.OnPopWindowClick() {
                    @Override
                    public void onClickPopWindow(String data) {
                        devicesList.get(position).onDelete(new IDeleteDeviceCallback() {
                            @Override
                            public void onDeleteSuccess() {
                                //????????????
                                //deviceItemAdapter.remove(position);
                                Toast.makeText(getContext(), "????????????", Toast.LENGTH_SHORT).show();
                                inputPopWindow.closePopWindow();
                            }

                            @Override
                            public void onDeleteFail(String s) {
                                //deviceItemAdapter.remove(position);
                                devicesList.get(position).onDeleteData();
//                                inputPopWindow.closePopWindow();
//                                inputPopWindow.setTitle("??????:" + s + "\n???????????????????????????????\n(????????????8?????????)");
//                                inputPopWindow.showPopWindow();
//                                inputPopWindow.setPopWindowClick(new InputPopWindow.OnPopWindowClick() {
//                                    @Override
//                                    public void onClickPopWindow(String data) {
//                                        deviceItemAdapter.remove(position);
//                                        devicesList.get(position).onDeleteData();
//                                    }
//                                });
                            }
                        });
                    }
                });
            }
        });
        //????????????????????????
        LeHomeSdk.getInstance().setHomeDataChangeListener(new IHomeDataChangeListener() {
            @Override
            public void onDeviceAdd(DeviceBean deviceBean) {

            }

            @Override
            public void onDeviceUpdate(DeviceBean deviceBean) {
                Log.e("TAG", "deviceBean-----------" + deviceBean.toString());
                        for (int i = 0; i < devicesList.size(); i++) {
                            if (devicesList.get(i).getDevId().equals(deviceBean.getDevId())) {
                                devicesList.set(i, deviceBean);
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                                return;
                            }
                        }

            }

            @Override
            public void onDeviceDeleted(DeviceBean deviceBean) {
                Log.e("TAG", "onDeviceDeleted-----------");
                updDevice();
            }
        });
        //???????????????????????????
        LeHomeSdk.getInstance().setHomeRoomGroupChangeListener(new IHomeRoomGroupChangeListener() {
            @Override
            public void onRoomBeanAdd(RoomBean roomBean) {
                //??????->????????????
                roomBeanList.add(roomBean);
                TabLayout.Tab tab = binding.tabRoom.newTab();
                tab.setText(roomBean.getName());
                tab.setTag(roomBean.getRoomId());
                binding.tabRoom.addTab(tab);
            }

            @Override
            public void onRoomBeanUpdate(RoomBean roomBean) {
                Log.e("TAG", "????????????--------->");
                updDevice();
            }

            @Override
            public void onRoomBeanDeleted(RoomBean roomBean) {

            }

            @Override
            public void onGroupBeanAdd(GroupBean GroupBean) {
                //??????->????????????
                groupBeanList.add(GroupBean);
                groupItemAdapter.updateList(groupBeanList);
            }

            @Override
            public void onGroupBeanUpdate(GroupBean GroupBean) {

            }

            @Override
            public void onGroupBeanDeleted(GroupBean GroupBean) {

            }
        });
        //??????????????????
        binding.tabRoom.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                roomId = (int) tab.getTag();
                updGroupList();
                myRoomBean = roomBeanList.get(tab.getPosition());//??????????????????
                myGroupBean = null;//?????????????????????
                binding.tvAllDevices.setTextColor(Color.RED);
                binding.tvAllDevices.setTextSize(16f);
                //????????????????????????
                groupItemAdapter.setGroupId(-1);
                groupItemAdapter.updateList();
                //??????????????????
                Log.e("TAG", "room------" + myRoomBean.toString());
                devicesList = roomManger.getRoomDeviceBeans(myRoomBean);
                Log.e("TAG", "devicesList------" + devicesList.toString());
                deviceItemAdapter.updateList(devicesList);
                //roomIndex = tab.getPosition();//????????????
                Log.e("TAG", "onTabSelected-->" + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.e("TAG", "onTabUnselected");

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.addRoom.setOnClickListener(this::onClick);
        binding.addGroup.setOnClickListener(this::onClick);
        binding.tvAllDevices.setOnClickListener(this::onClick);
        binding.btnOpenAll.setOnClickListener(this::onClick);
        binding.btnCloesAll.setOnClickListener(this::onClick);
        binding.btnAddDevice.setOnClickListener(this::onClick);
        binding.btnDelDevice.setOnClickListener(this::onClick);
    }

    private void updDevice(){
        if (myGroupBean != null) {
            Log.e("TAG", "????????????????????????------>");
            devicesList = groupManger.getGroupDeviceBeans(myGroupBean);
            deviceItemAdapter.updateList(devicesList);
        } else {
            Log.e("TAG", "????????????????????????------>");
            devicesList = roomManger.getRoomDeviceBeans(myRoomBean);
            deviceItemAdapter.updateList(devicesList);
        }
    }
    //??????????????????
    private void updGroupList() {
        if (dataManger == null) {
            return;
        }
        List<GroupBean> myList = dataManger.getAllGroupBeans();
        groupBeanList = myList;
        if (roomId == -1) {
            groupItemAdapter.updateList(groupBeanList);
            return;
        }//?????????
        groupBeanList = new ArrayList<>();
        for (int i = 0; i < myList.size(); i++) {
            if (myList.get(i).getParentRoomId() == roomId) {
                groupBeanList.add(myList.get(i));
            }
        }
        groupItemAdapter.updateList(groupBeanList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private InputPopWindow inputPopWindow;
    private CheckDevicePopWindow checkDevicePopWindow;

    private DeviceBean isDeviceBean;//????????????????????????
    @Override
    public void onClick(View v) {
        if (binding.tvAllDevices.equals(v)) {
            //??????????????????
            groupItemAdapter.setGroupId(-1);
            groupItemAdapter.updateList();
            myGroupBean = null;//?????????????????????
            binding.tvAllDevices.setTextColor(Color.RED);
            binding.tvAllDevices.setTextSize(16f);
            //??????????????????
            devicesList = roomId == -1 ? dataManger.getAllDevices() : roomManger.getRoomDeviceBeans(myRoomBean);
            ;
            deviceItemAdapter.updateList(devicesList);
        } else if (binding.addRoom.equals(v)) {
            //????????????
            if (roomManger == null) {
                toast("roomManger=null");
                return;
            }
            inputPopWindow.setTitle("?????????????????????");
            inputPopWindow.setPopWindowClick(new InputPopWindow.OnPopWindowClick() {
                @Override
                public void onClickPopWindow(String data) {
                    if (TextUtils.isEmpty(data)) {
                        toast("?????????????????????");
                        return;
                    }
                    roomManger.creatRoom(data, new ICreateCallback() {
                        @Override
                        public void onAddSuccess() {
                            toast("????????????");
                        }

                        @Override
                        public void onAddFail(String msg) {
                            toast("????????????:" + msg);
                        }
                    });
                    inputPopWindow.closePopWindow();
                }
            });
            inputPopWindow.showPopWindow();
        } else if (binding.addGroup.equals(v)) {
            //????????????
            if (roomId == -1) {
                toast("???????????????????????????!");
                return;
            }
            if (groupManger == null) {
                toast("groupManger=null");
                return;
            }
            inputPopWindow.setTitle("?????????????????????");
            inputPopWindow.setPopWindowClick(new InputPopWindow.OnPopWindowClick() {
                @Override
                public void onClickPopWindow(String data) {
                    if (TextUtils.isEmpty(data)) {
                        toast("?????????????????????");
                        return;
                    }
                    groupManger.creatGroup(data, roomId, new ICreateCallback() {
                        @Override
                        public void onAddSuccess() {
                            toast("????????????");
                        }

                        @Override
                        public void onAddFail(String msg) {
                            toast("????????????:" + msg);
                        }
                    });
                    inputPopWindow.closePopWindow();
                }
            });
            inputPopWindow.showPopWindow();
        } else if (binding.btnOpenAll.equals(v)) {
            //????????????
            roomSwitch(true);
            //sendDevice(1);
        } else if (binding.btnCloesAll.equals(v)) {
            //????????????
            //sendDevice(2);
            roomSwitch(false);
        } else if (binding.btnAddDevice.equals(v)) {
            //????????????
            if (roomId == -1) {
                toast("???????????????????????????!");
                return;
            }
            String titleStr = "";//???????????????
            if (myGroupBean != null) {
                titleStr = "????????????(???????????????:" + myGroupBean.getName() + ")";
            } else if (myRoomBean != null) {
                titleStr = "????????????(???????????????:" + myRoomBean.getName() + ")";
            } else {
                toast("???????????????????????????");
            }

            checkDevicePopWindow.setTitle(titleStr);
            List<DeviceBean> beanList = getAddDevices();
            if (beanList.size() == 0) {
                toast("????????????????????????!");
                return;
            }
            checkDevicePopWindow.setDeviceBeanList(beanList);
            checkDevicePopWindow.setDelete(false);
            checkDevicePopWindow.setPopWindowClick(new CheckDevicePopWindow.OnPopWindowClick() {
                @Override
                public void onClickPopWindow(int position, DeviceBean deviceBean) {
                    isDeviceBean = deviceBean;
                    if (myGroupBean != null) {
                        Log.e("TAG", "????????????????????????------>" + deviceBean.toString());
                        isDeviceBean.addToGroupBean(myGroupBean);
                    } else {
                        Log.e("TAG", "????????????????????????------>" + deviceBean.toString());
                        isDeviceBean.addToRoomBean(myRoomBean);
                    }
                    checkDevicePopWindow.closePopWindow();
                }
            });
            checkDevicePopWindow.showPopWindow();
        } else if (binding.btnDelDevice.equals(v)) {
            //????????????????????????
            Log.e("TAG", "??????-------->" + devicesList.toString());
            if (devicesList.size() == 0) {
                toast("????????????????????????!");
                return;
            }
            if (roomId == -1){
                inputPopWindow.setTitle("?????????????????????????");
                inputPopWindow.setInput(false);
                inputPopWindow.showPopWindow();
                inputPopWindow.setPopWindowClick(new InputPopWindow.OnPopWindowClick() {
                    @Override
                    public void onClickPopWindow(String data) {
                        //????????????
                        for (int i = 0; i < devicesList.size(); i++) {
                            Log.e("TAG","??????-------->"+devicesList.get(i).toString());
                            devicesList.get(i).onDelete(new IDeleteDeviceCallback() {
                                @Override
                                public void onDeleteSuccess() {
                                    //????????????
                                }
                                @Override
                                public void onDeleteFail(String s) {
                                }
                            });
                            deviceItemAdapter.updateList(new ArrayList<>());
                        }
                        inputPopWindow.closePopWindow();
                    }
                });
            }else {
                String titleStr = "";//???????????????
                if (myGroupBean != null) {
                    titleStr = "????????????(???\"" + myGroupBean.getName() + "\"???????????????)";
                } else if (myRoomBean != null) {
                    titleStr = "????????????(???\"" + myRoomBean.getName() + "\"???????????????)";
                }
                checkDevicePopWindow.setTitle(titleStr);
                checkDevicePopWindow.setDelete(false);
                checkDevicePopWindow.setDeviceBeanList(devicesList);
                checkDevicePopWindow.setPopWindowClick(new CheckDevicePopWindow.OnPopWindowClick() {
                    @Override
                    public void onClickPopWindow(int position, DeviceBean deviceBean) {
                        if (myGroupBean != null) {
                            //??????
                            Log.e("TAG", "?????????????????????------>" + deviceBean.toString());
                            deviceBean.delGroupBean(myGroupBean);
                        } else if (myRoomBean != null) {
                            //??????
                            Log.e("TAG", "?????????????????????------>" + deviceBean.toString());
                            deviceBean.delRoomBean(myRoomBean);
                        }
                        devicesList.remove(position);
                        deviceItemAdapter.updateList(devicesList);
                        checkDevicePopWindow.closePopWindow();
                        toast("????????????!");
                    }
                });
                checkDevicePopWindow.showPopWindow();
            }
        }
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    private List<DeviceBean> getAddDevices() {
        //?????????????????????????????????
        map = new HashMap<>();
        for (int i = 0; i < roomBeanList.size(); i++) {
            if (i != 0) {
                for (int j = 0; j < roomBeanList.get(i).getDevIds().size(); j++) {
                    map.put(roomBeanList.get(i).getDevIds().get(j), "");
                }
            }
        }
        List<DeviceBean> deviceBeans = new ArrayList<>();
        //???????????????????????????
        for (int i = 0; i < dataManger.getAllDevices().size(); i++) {
            Log.e("TAG", "------------" + dataManger.getAllDevices().get(i).getDevId());
            if (!map.toString().contains(dataManger.getAllDevices().get(i).getDevId())) {
                deviceBeans.add(dataManger.getAllDevices().get(i));
            }
        }
        return deviceBeans;
    }

    /**
     * ????????????
     * type:true??? false???
     */
    public void roomSwitch(boolean type){
        if (myGroupBean != null) {
            //???????????????????????????
            Log.e("TAG","myGroupBean-------"+myGroupBean.toString());
            groupManger.controlGroup(myGroupBean,DeviceType.Light, new DpBean(LightDp.POWER.getDpId(), LightDp.POWER.getType(), type));
        }else if (myRoomBean != null){
            // ???????????????????????????
            Log.e("TAG","myRoomBean-------"+myRoomBean.toString());
            roomManger.controlRoom(myRoomBean, DeviceType.Light, new DpBean(LightDp.POWER.getDpId(), LightDp.POWER.getType(), type));
        }else {
            toast("??????");
        }
    }
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    deviceItemAdapter.updateList(devicesList);
                    break;
            }
        }
    };
    private void toast(String str) {
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }
}