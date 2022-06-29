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

    private GroupItemAdapter groupItemAdapter;//群组适配器
    private DeviceItemAdapter deviceItemAdapter;//设备适配器
    //房间
    private List<RoomBean> roomBeanList;//房间集合
    private IRoomManger roomManger;
    private RoomBean myRoomBean;//当前房间
    private int roomId = -1;//当前房间ID
    //群组
    private List<GroupBean> groupBeanList;//群组集合
    private IGroupManger groupManger;
    private GroupBean myGroupBean;//当前群组
    //设备
    private List<DeviceBean> devicesList;//设备

    private HashMap<String, Object> map = new HashMap<>();//记录设备是否有添加到其他房间

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
            roomBeanList = dataManger.getAllRoomBeans() == null ? new ArrayList<>() : dataManger.getAllRoomBeans();//获取所有房间是否为空
            groupBeanList = dataManger.getAllGroupBeans() == null ? new ArrayList<>() : dataManger.getAllGroupBeans();//获取所有群组是否为空
            myRoomBean = roomBeanList.get(0);//默认是所有房间
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
        inputPopWindow = new InputPopWindow(getContext(), "请输入群组名字", "确定", true);
        checkDevicePopWindow = new CheckDevicePopWindow(getContext(), "", null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };
        //群组
        binding.rvGroups.setLayoutManager(linearLayoutManager);
        groupItemAdapter = new GroupItemAdapter(getContext(), groupBeanList);
        binding.rvGroups.setAdapter(groupItemAdapter);
        groupItemAdapter.setOnItemClickListeners(new MyBaseRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //toast("点击="+groupBeanList.get(position));

                GroupBean bean = groupBeanList.get(position);
                myGroupBean = bean;//记录当前群组
                groupItemAdapter.setGroupId(bean.getGroupId());
                groupItemAdapter.updateList();

                binding.tvAllDevices.setTextColor(Color.BLACK);
                binding.tvAllDevices.setTextSize(12f);
                //更新设备信息
                devicesList = groupManger.getGroupDeviceBeans(bean);
                deviceItemAdapter.updateList(devicesList);
            }
        });
        //设备
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
                //进入设备控制页
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
                inputPopWindow.setTitle("确定删除设备?");
                inputPopWindow.setInput(false);
                inputPopWindow.showPopWindow();
                inputPopWindow.setPopWindowClick(new InputPopWindow.OnPopWindowClick() {
                    @Override
                    public void onClickPopWindow(String data) {
                        devicesList.get(position).onDelete(new IDeleteDeviceCallback() {
                            @Override
                            public void onDeleteSuccess() {
                                //删除成功
                                //deviceItemAdapter.remove(position);
                                Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                inputPopWindow.closePopWindow();
                            }

                            @Override
                            public void onDeleteFail(String s) {
                                //deviceItemAdapter.remove(position);
                                devicesList.get(position).onDeleteData();
//                                inputPopWindow.closePopWindow();
//                                inputPopWindow.setTitle("原因:" + s + "\n是否强制删除本地数据?\n(设备需要8短重置)");
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
        //设备状态监听回调
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
        //房间和群组监听回调
        LeHomeSdk.getInstance().setHomeRoomGroupChangeListener(new IHomeRoomGroupChangeListener() {
            @Override
            public void onRoomBeanAdd(RoomBean roomBean) {
                //房间->添加成功
                roomBeanList.add(roomBean);
                TabLayout.Tab tab = binding.tabRoom.newTab();
                tab.setText(roomBean.getName());
                tab.setTag(roomBean.getRoomId());
                binding.tabRoom.addTab(tab);
            }

            @Override
            public void onRoomBeanUpdate(RoomBean roomBean) {
                Log.e("TAG", "更新房间--------->");
                updDevice();
            }

            @Override
            public void onRoomBeanDeleted(RoomBean roomBean) {

            }

            @Override
            public void onGroupBeanAdd(GroupBean GroupBean) {
                //群组->添加成功
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
        //监听切换房间
        binding.tabRoom.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                roomId = (int) tab.getTag();
                updGroupList();
                myRoomBean = roomBeanList.get(tab.getPosition());//记录当前房间
                myGroupBean = null;//清除当前群组值
                binding.tvAllDevices.setTextColor(Color.RED);
                binding.tvAllDevices.setTextSize(16f);
                //清除群组选中样式
                groupItemAdapter.setGroupId(-1);
                groupItemAdapter.updateList();
                //更新设备信息
                Log.e("TAG", "room------" + myRoomBean.toString());
                devicesList = roomManger.getRoomDeviceBeans(myRoomBean);
                Log.e("TAG", "devicesList------" + devicesList.toString());
                deviceItemAdapter.updateList(devicesList);
                //roomIndex = tab.getPosition();//房间下标
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
            Log.e("TAG", "群组添加设备选中------>");
            devicesList = groupManger.getGroupDeviceBeans(myGroupBean);
            deviceItemAdapter.updateList(devicesList);
        } else {
            Log.e("TAG", "房间添加设备选中------>");
            devicesList = roomManger.getRoomDeviceBeans(myRoomBean);
            deviceItemAdapter.updateList(devicesList);
        }
    }
    //更新群组信息
    private void updGroupList() {
        if (dataManger == null) {
            return;
        }
        List<GroupBean> myList = dataManger.getAllGroupBeans();
        groupBeanList = myList;
        if (roomId == -1) {
            groupItemAdapter.updateList(groupBeanList);
            return;
        }//所有的
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

    private DeviceBean isDeviceBean;//房间群组添加设备
    @Override
    public void onClick(View v) {
        if (binding.tvAllDevices.equals(v)) {
            //获取所有设备
            groupItemAdapter.setGroupId(-1);
            groupItemAdapter.updateList();
            myGroupBean = null;//清除当前群组值
            binding.tvAllDevices.setTextColor(Color.RED);
            binding.tvAllDevices.setTextSize(16f);
            //更新设备信息
            devicesList = roomId == -1 ? dataManger.getAllDevices() : roomManger.getRoomDeviceBeans(myRoomBean);
            ;
            deviceItemAdapter.updateList(devicesList);
        } else if (binding.addRoom.equals(v)) {
            //添加房间
            if (roomManger == null) {
                toast("roomManger=null");
                return;
            }
            inputPopWindow.setTitle("请输入房间名字");
            inputPopWindow.setPopWindowClick(new InputPopWindow.OnPopWindowClick() {
                @Override
                public void onClickPopWindow(String data) {
                    if (TextUtils.isEmpty(data)) {
                        toast("请输入房间名字");
                        return;
                    }
                    roomManger.creatRoom(data, new ICreateCallback() {
                        @Override
                        public void onAddSuccess() {
                            toast("添加成功");
                        }

                        @Override
                        public void onAddFail(String msg) {
                            toast("添加失败:" + msg);
                        }
                    });
                    inputPopWindow.closePopWindow();
                }
            });
            inputPopWindow.showPopWindow();
        } else if (binding.addGroup.equals(v)) {
            //添加群组
            if (roomId == -1) {
                toast("请选中房间后再添加!");
                return;
            }
            if (groupManger == null) {
                toast("groupManger=null");
                return;
            }
            inputPopWindow.setTitle("请输入群组名字");
            inputPopWindow.setPopWindowClick(new InputPopWindow.OnPopWindowClick() {
                @Override
                public void onClickPopWindow(String data) {
                    if (TextUtils.isEmpty(data)) {
                        toast("请输入群组名字");
                        return;
                    }
                    groupManger.creatGroup(data, roomId, new ICreateCallback() {
                        @Override
                        public void onAddSuccess() {
                            toast("添加成功");
                        }

                        @Override
                        public void onAddFail(String msg) {
                            toast("添加失败:" + msg);
                        }
                    });
                    inputPopWindow.closePopWindow();
                }
            });
            inputPopWindow.showPopWindow();
        } else if (binding.btnOpenAll.equals(v)) {
            //打开所有
            roomSwitch(true);
            //sendDevice(1);
        } else if (binding.btnCloesAll.equals(v)) {
            //关闭所有
            //sendDevice(2);
            roomSwitch(false);
        } else if (binding.btnAddDevice.equals(v)) {
            //添加设备
            if (roomId == -1) {
                toast("请选中房间后再添加!");
                return;
            }
            String titleStr = "";//提示框标题
            if (myGroupBean != null) {
                titleStr = "选中设备(添加到群组:" + myGroupBean.getName() + ")";
            } else if (myRoomBean != null) {
                titleStr = "选中设备(添加到房间:" + myRoomBean.getName() + ")";
            } else {
                toast("请选中房间后再添加");
            }

            checkDevicePopWindow.setTitle(titleStr);
            List<DeviceBean> beanList = getAddDevices();
            if (beanList.size() == 0) {
                toast("暂无可添加的设备!");
                return;
            }
            checkDevicePopWindow.setDeviceBeanList(beanList);
            checkDevicePopWindow.setDelete(false);
            checkDevicePopWindow.setPopWindowClick(new CheckDevicePopWindow.OnPopWindowClick() {
                @Override
                public void onClickPopWindow(int position, DeviceBean deviceBean) {
                    isDeviceBean = deviceBean;
                    if (myGroupBean != null) {
                        Log.e("TAG", "群组添加设备选中------>" + deviceBean.toString());
                        isDeviceBean.addToGroupBean(myGroupBean);
                    } else {
                        Log.e("TAG", "房间添加设备选中------>" + deviceBean.toString());
                        isDeviceBean.addToRoomBean(myRoomBean);
                    }
                    checkDevicePopWindow.closePopWindow();
                }
            });
            checkDevicePopWindow.showPopWindow();
        } else if (binding.btnDelDevice.equals(v)) {
            //删除当前所有设备
            Log.e("TAG", "删除-------->" + devicesList.toString());
            if (devicesList.size() == 0) {
                toast("暂无可删除的设备!");
                return;
            }
            if (roomId == -1){
                inputPopWindow.setTitle("确定删除所有设备?");
                inputPopWindow.setInput(false);
                inputPopWindow.showPopWindow();
                inputPopWindow.setPopWindowClick(new InputPopWindow.OnPopWindowClick() {
                    @Override
                    public void onClickPopWindow(String data) {
                        //删除所有
                        for (int i = 0; i < devicesList.size(); i++) {
                            Log.e("TAG","删除-------->"+devicesList.get(i).toString());
                            devicesList.get(i).onDelete(new IDeleteDeviceCallback() {
                                @Override
                                public void onDeleteSuccess() {
                                    //删除成功
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
                String titleStr = "";//提示框标题
                if (myGroupBean != null) {
                    titleStr = "移除设备(从\"" + myGroupBean.getName() + "\"群组中移除)";
                } else if (myRoomBean != null) {
                    titleStr = "移除设备(从\"" + myRoomBean.getName() + "\"房间中移除)";
                }
                checkDevicePopWindow.setTitle(titleStr);
                checkDevicePopWindow.setDelete(false);
                checkDevicePopWindow.setDeviceBeanList(devicesList);
                checkDevicePopWindow.setPopWindowClick(new CheckDevicePopWindow.OnPopWindowClick() {
                    @Override
                    public void onClickPopWindow(int position, DeviceBean deviceBean) {
                        if (myGroupBean != null) {
                            //群组
                            Log.e("TAG", "群组中移除设备------>" + deviceBean.toString());
                            deviceBean.delGroupBean(myGroupBean);
                        } else if (myRoomBean != null) {
                            //房间
                            Log.e("TAG", "房间中移除设备------>" + deviceBean.toString());
                            deviceBean.delRoomBean(myRoomBean);
                        }
                        devicesList.remove(position);
                        deviceItemAdapter.updateList(devicesList);
                        checkDevicePopWindow.closePopWindow();
                        toast("移除成功!");
                    }
                });
                checkDevicePopWindow.showPopWindow();
            }
        }
    }

    /**
     * 获取可添加的设备列表
     *
     * @return
     */
    private List<DeviceBean> getAddDevices() {
        //记录设备是否添加到房间
        map = new HashMap<>();
        for (int i = 0; i < roomBeanList.size(); i++) {
            if (i != 0) {
                for (int j = 0; j < roomBeanList.get(i).getDevIds().size(); j++) {
                    map.put(roomBeanList.get(i).getDevIds().get(j), "");
                }
            }
        }
        List<DeviceBean> deviceBeans = new ArrayList<>();
        //移除已添加过的设备
        for (int i = 0; i < dataManger.getAllDevices().size(); i++) {
            Log.e("TAG", "------------" + dataManger.getAllDevices().get(i).getDevId());
            if (!map.toString().contains(dataManger.getAllDevices().get(i).getDevId())) {
                deviceBeans.add(dataManger.getAllDevices().get(i));
            }
        }
        return deviceBeans;
    }

    /**
     * 房间开关
     * type:true开 false关
     */
    public void roomSwitch(boolean type){
        if (myGroupBean != null) {
            //打开群主内所有灯具
            Log.e("TAG","myGroupBean-------"+myGroupBean.toString());
            groupManger.controlGroup(myGroupBean,DeviceType.Light, new DpBean(LightDp.POWER.getDpId(), LightDp.POWER.getType(), type));
        }else if (myRoomBean != null){
            // 打开房间内所有灯具
            Log.e("TAG","myRoomBean-------"+myRoomBean.toString());
            roomManger.controlRoom(myRoomBean, DeviceType.Light, new DpBean(LightDp.POWER.getDpId(), LightDp.POWER.getType(), type));
        }else {
            toast("失败");
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