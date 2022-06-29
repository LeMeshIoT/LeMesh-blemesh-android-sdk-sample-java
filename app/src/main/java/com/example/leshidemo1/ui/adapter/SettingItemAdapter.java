package com.example.leshidemo1.ui.adapter;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.leshidemo1.MyApplication;
import com.example.leshidemo1.R;
import com.example.leshidemo1.ui.bean.DpsBean;
import com.example.leshidemo1.ui.utils.DeviceControlUtil;

import java.util.List;

import cn.lelight.leiot.data.bean.DeviceBean;
import cn.lelight.leiot.data.bean.base.DpBean;
import cn.lelight.leiot.data.leenum.dps.LightDp;
import cn.lelight.leiot.sdk.api.callback.IControlCallback;

/**
 * 设置 dps适配器
 */
public class SettingItemAdapter extends MyBaseRVAdapter<DpsBean, SettingItemAdapter.VH> {
    private DeviceBean myDeviceBean;
    private List<DpsBean> mList;
    public SettingItemAdapter(Context context,DeviceBean deviceBean, List<DpsBean> list) {
        super(context, list);
        this.myDeviceBean = deviceBean;
        this.mList = list;
    }


    /**
     * 创建
     */
    @Override
    protected VH ICreateItemHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_setting_item, parent, false);
        return new VH(view);
    }


    /**
     * 加载
     */
    @Override
    protected void IBindItemHolder(final VH holder, final int position) {
        final DpsBean dpsBean = mList.get(position);
        holder.tv_dps_id.setText(dpsBean.getId()+"");
        holder.tv_dps_name.setText(dpsBean.getName());
        holder.tv_dps_type.setText(getTypeStr(dpsBean.getType()));
        holder.tv_value.setText(dpsBean.getValue());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position,dpsBean);
//                if (mOnItemClickListener != null) {
//                    mOnItemClickListener.onItemClick(holder.itemView, position);
//                }
            }
        });
    }


    class VH extends RecyclerView.ViewHolder {
        private TextView tv_dps_id;
        private TextView tv_dps_name;
        private TextView tv_dps_type;
        private TextView tv_value;

        public VH(View itemView) {
            super(itemView);
            tv_dps_id = itemView.findViewById(R.id.tv_dps_id);
            tv_dps_name = itemView.findViewById(R.id.tv_dps_name);
            tv_dps_type = itemView.findViewById(R.id.tv_dps_type);
            tv_value = itemView.findViewById(R.id.tv_value);
        }
    }

    /**
     * 弹框
     * @param dpsBean
     */
    private void showDialog(int position,DpsBean dpsBean){
        switch (dpsBean.getType()){
            case 1://单选弹框
                new MaterialDialog.Builder(mContext)
                        .title(myDeviceBean.getName())
                        .positiveText("确认")
                        .items(R.array.switch_arr)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                Log.e("TAG","which------->"+which);
                                myDeviceBean.sendDp(new DpBean(dpsBean.getId(), dpsBean.getType(),which == 0), new IControlCallback() {
                                    @Override
                                    public void onSuccess() {
                                        MyApplication.getInstance().toast("发送成功!");
                                        mList.get(position).setValue(which == 0 ? "true" : "false");
                                        updateList();
                                    }

                                    @Override
                                    public void onFail(int i, String s) {
                                        MyApplication.getInstance().toast("发送失败:"+s);
                                    }
                                });
                                return true;
                            }
                        })
                        .show();

                break;
            case 2:
                new MaterialDialog.Builder(mContext)
                        .title(myDeviceBean.getName())
                        .content("请根据具体范围输入数值\\n本弹窗不做范围限制，仅作调试测试用")
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .input("输入数值", null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                Log.e("TAG","input------->"+input.toString());
                                Log.e("TAG","getId------->"+dpsBean.getId());
                                Log.e("TAG","getType------->"+dpsBean.getType());
                                if (TextUtils.isEmpty(input)){
                                    MyApplication.getInstance().toast("请输入数值!");
                                    return;
                                }
                                myDeviceBean.sendDp(new DpBean(dpsBean.getId(), dpsBean.getType(),Integer.parseInt(input.toString())), new IControlCallback() {
                                    @Override
                                    public void onSuccess() {
                                        MyApplication.getInstance().toast("发送成功!");
                                        mList.get(position).setValue(input.toString()+"");
                                        updateList();
                                    }

                                    @Override
                                    public void onFail(int i, String s) {
                                        MyApplication.getInstance().toast("发送失败:"+s);
                                    }
                                });
                            }
                        })
                        .positiveText("确定")
                        .show();
                break;
            case 5:
                new MaterialDialog.Builder(mContext)
                        .title(myDeviceBean.getName())
                        .content("请根据具体范围输入数值\\n本弹窗不做范围限制，仅作调试测试用")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("输入内容", null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                Log.e("TAG","input2------->"+input.toString());
                                if (TextUtils.isEmpty(input)){
                                    MyApplication.getInstance().toast("请输入字符串!");
                                    return;
                                }
                                myDeviceBean.sendDp(new DpBean(dpsBean.getId(), dpsBean.getType(),input.toString()+""), new IControlCallback() {
                                    @Override
                                    public void onSuccess() {
                                        MyApplication.getInstance().toast("发送成功!");
                                        mList.get(position).setValue(input.toString()+"");
                                        updateList();
                                    }

                                    @Override
                                    public void onFail(int i, String s) {
                                        MyApplication.getInstance().toast("发送失败:"+s);
                                    }
                                });
                            }
                        })
                        .positiveText("确定")
                        .show();
                break;
        }
    }

    /**
     * 1 bool
     * 2 value
     * 3 enum
     * 4
     * 5 str
     */
    private String getTypeStr(int type){
        String str = "";
        switch (type){
            case 1:
                str = "布尔值";
                break;
            case 2:
                str = "数值";
                break;
            case 3:
                str = "enum";
                break;
            case 4:
                str = "";
                break;
            case 5:
                str = "字符串";
                break;
        }
        return str;
    }
}
