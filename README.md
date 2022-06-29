# LeMesh-blemesh-android-sdk-sample-kotlin

#### 输入授权信息

```
// MyApplication
// 输入对应信息,否则初始化的时候会报 "非法授权sdk"
private String APPID = "";
private String MAC = "";
private String SECRET = "";
```

#### bleMesh初始化

```
// HomeFragment#initLeMesh
private void initLeMesh() {
// 自行检查是否有开启蓝牙
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
```

#### 流程说明

1. 初始化sdk
2. 初始化blemesh模块
3. 添加设备(如果之前添加了可以忽略这步)
4. 控制设备
