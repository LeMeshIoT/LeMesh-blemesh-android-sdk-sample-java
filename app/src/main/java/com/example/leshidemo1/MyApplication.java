package com.example.leshidemo1;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import cn.lelight.leiot.sdk.LeHomeSdk;
import cn.lelight.leiot.sdk.callback.HomeSdkInitCallback;
import cn.lelight.leiot.sdk.core.InitCallback;

public class MyApplication extends Application {

    private volatile static MyApplication application;
    private String APPID = "";
    private String MAC = "";
    private String SECRET = "";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        LeHomeSdk.attachBaseContext(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        //初始化SDK
        initLeSdk();
    }

    /**
     * 激活
     */
    private void initLeSdk() {
        LeHomeSdk.init(this, APPID,MAC,SECRET, new HomeSdkInitCallback() {
            @Override
            public void onResult(int result) {
                //
                if (result == InitCallback.SUCCESS) {
                    // 初始化成功
                    Toast.makeText(getApplicationContext(), "sdk 初始化成功", Toast.LENGTH_SHORT).show();
                } else if (result == InitCallback.ALREADY_INITIALED) {
                    Toast.makeText(getApplicationContext(), "sdk已经初始化过了", Toast.LENGTH_SHORT).show();
                } else if (result == InitCallback.UNAUTH_SDK) {
                    Toast.makeText(getApplicationContext(), "非法授权sdk", Toast.LENGTH_SHORT).show();
                }else if (result == InitCallback.UNEXPECTED_EXCEPTION) {
                    Toast.makeText(getApplicationContext(), "其它异常", Toast.LENGTH_SHORT).show();
                }else if (result == InitCallback.NO_SDK_INFO) {
                    Toast.makeText(getApplicationContext(), "该应用未注册", Toast.LENGTH_SHORT).show();
                }else if (result == InitCallback.AUTH_TYPE_FAID) {
                    Toast.makeText(getApplicationContext(), "该应用类型错误", Toast.LENGTH_SHORT).show();
                }else if (result == InitCallback.OFFLINE_ALREADY_AUTH) {
                    Toast.makeText(getApplicationContext(), "离线设备，重复授权", Toast.LENGTH_SHORT).show();
                }else if (result == InitCallback.NEED_APPID) {
                    Toast.makeText(getApplicationContext(), "需要appid", Toast.LENGTH_SHORT).show();
                }else if (result == InitCallback.NEED_MAC) {
                    Toast.makeText(getApplicationContext(), "需要mac", Toast.LENGTH_SHORT).show();
                }else if (result == InitCallback.NEED_SECRET) {
                    Toast.makeText(getApplicationContext(), "需要secret", Toast.LENGTH_SHORT).show();
                }else if(result == InitCallback.AUTH_FAID){
                    // 检查是否设备有网络
                    Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 获取实例
     *
     * @return MyApplication
     */
    public static MyApplication getInstance() {
        if (application == null) {
            synchronized (MyApplication.class) {
                if (application == null) {
                    application = new MyApplication();
                }
            }
        }
        return application;
    }
    /**
     * 获取当前app version code
     */
    public static long getAppVersionCode() {
        long appVersionCode = 0;
        try {
            PackageInfo packageInfo = MyApplication.getInstance()
                    .getPackageManager()
                    .getPackageInfo(MyApplication.getInstance().getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                appVersionCode = packageInfo.getLongVersionCode();
            } else {
                appVersionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", e.getMessage());
        }
        return appVersionCode;
    }
    public void toast(String str){
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
}