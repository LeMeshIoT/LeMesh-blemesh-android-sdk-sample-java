package com.example.leshidemo1.ui.device;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.example.leshidemo1.R;
import com.example.leshidemo1.databinding.ActDimmingBinding;
import com.example.leshidemo1.ui.utils.DeviceControlUtil;
import com.example.leshidemo1.ui.utils.view.ControlView;

import java.math.BigDecimal;

import cn.lelight.leiot.data.bean.DeviceBean;
import cn.lelight.leiot.sdk.LeHomeSdk;
import cn.lelight.leiot.sdk.api.IDataManger;

public class DimmingAct extends Activity implements View.OnClickListener {
    public static boolean MSwitch = true;//true=>允许修改色温值
    private ActDimmingBinding binding;
    private int type;//1->色温 2->亮度
    private boolean mySwitch = true;//禁用操作 true启用 false禁止

    private IDataManger dataManger;
    private DeviceBean deviceBean;

    private String id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActDimmingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dataManger = LeHomeSdk.getDataManger();
        if (dataManger == null){ finish();return; }
        id = getIntent().getStringExtra("ID");
        deviceBean = dataManger.getDeviceBean(id);
        if (deviceBean == null){ finish();return; }
        Log.e("TAG",deviceBean.getDps().get(1)+"------devicebean-------->"+deviceBean.toString());
        mySwitch = (boolean)deviceBean.getDps().get(1);
        updateData();
        initView();
    }

    private void initView() {
        Log.e("TAG","mySwitch--------->"+mySwitch);
        int brig = (int) Math.round(Double.valueOf(deviceBean.getDps().get(3)+""));//亮度
        int temperature = (int) Math.round(Double.valueOf(deviceBean.getDps().get(4)+""));//色温
        binding.seekbar.setProgress(brig/10);//设置设备亮度值
        binding.colorSelect.setProgress(temperature/10);//设置设备色温值
        binding.tvSeekBar.setText(brig+"");
        binding.tvColorValue.setText("当前色温:  " + temperature);
        //
        //binding.headView.tvTitleRight.setVisibility(View.VISIBLE);
        binding.headView.tvTitleRight.setText("设置");
        binding.headView.tvTitle.setText(deviceBean.getMac());
        GradientDrawable gradientDrawable = (GradientDrawable) binding.llBg.getBackground();
        //色温监听
        binding.colorSelect.setOnProgressChangeListener(new ControlView.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(ControlView seekBar, int progress, boolean isUser) {
                //拖动中
                //Log.e("TAG", "2----------->" + seekBar.getColor());
                binding.tvColorValue.setTextColor(seekBar.getColor());
                gradientDrawable.setColor(seekBar.getColor());
            }

            @Override
            public void onStartTrackingTouch(ControlView seekBar) {

            }

            @Override
            public void onStopTrackingTouch(ControlView seekBar) {
                //拖动结束
                //色温
                type = 1;
                bleWrite(seekBar.getProgress());
                Log.e("TAG", "-3---------->" + seekBar.getProgress());
            }
        });
        //亮度监听
        //亮度
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.e("TAG", "当前进度值--->" + progress + "  / 100 ");
                if (progress == 0) {
                    seekBar.setProgress(1);
                    return;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //亮度
                type = 2;
                bleWrite(seekBar.getProgress());
                Log.e("TAG","放开SeekBar--->"+seekBar.getProgress());
            }
        });
        binding.ivSwitch.setOnClickListener(this::onClick);
        binding.headView.tvTitleRight.setOnClickListener(this::onClick);
        binding.headView.ivBack.setOnClickListener(this::onClick);
    }

    /**
     * 执行
     *
     * @param data 亮度 色温
     */

    private int number;//当前选中的值
    public void bleWrite(int data) {
        //没连接蓝牙，走网关
        BigDecimal scale = new BigDecimal(data).setScale(0, BigDecimal.ROUND_HALF_UP);
        BigDecimal value = new BigDecimal((float) scale.intValue() / 100 * 65535).setScale(0, BigDecimal.ROUND_HALF_UP);
        String value_16 = Integer.toHexString(value.intValue());
        number = (int)(1000 * (data*0.01));
        if (type == 1) {
            //色温
            binding.tvColorValue.setText("当前色温:  " + number);
            DeviceControlUtil.getInstance().deviceSend(deviceBean,4,number);
        } else {
            //亮度
            binding.tvSeekBar.setText(number+"");
            DeviceControlUtil.getInstance().deviceSend(deviceBean,3,number);
        }
        Log.e("TAG", "intValue------->" + value.intValue());
        Log.e("TAG", "value_16------->" + 0);//

    }
    /**
     * 设备修改完开关状态
     * 修改数据
     */
    private void updateData(){
        MSwitch = mySwitch;
        binding.ivSwitch.setImageResource(mySwitch ? R.mipmap.homepage_classic_multi_item_on : R.mipmap.homepage_classic_multi_item_off);
        isSeekbar();
    }
    /**
     * 是否禁用颜色控件
     */
    private void isSeekbar(){
        binding.seekbar.setClickable(mySwitch);
        binding.seekbar.setEnabled(mySwitch);
        binding.seekbar.setSelected(mySwitch);
        binding.seekbar.setFocusable(mySwitch);
    }
    @Override
    public void onClick(View v) {
        if (binding.headView.ivBack.equals(v)){
            finish();
        }else if (binding.headView.tvTitleRight.equals(v)){
            Log.e("TAG","设置-----");
            Intent intent = new Intent(this,SettingAct.class);
            intent.putExtra("ID",id);
            startActivity(intent);
        }else if (binding.ivSwitch.equals(v)) {
            //点击开关按钮
            DeviceControlUtil.getInstance().deviceSend(deviceBean,mySwitch ? 2 : 1,0);
            mySwitch = !mySwitch;
            updateData();
        }
    }
}
