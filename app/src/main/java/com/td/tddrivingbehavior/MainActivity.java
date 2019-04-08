package com.td.tddrivingbehavior;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.td.tddrivingbehavior.entity.Vehicle;
import com.td.tddrivingbehavior.utils.FileUtils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private boolean isPermissionGranted = false;

    /**
     * BaiduMap Display
     */
    private MapView mMapView = null;

    /**
     * Location Display
     */
    private BaiduMap mBaiduMap;
    private LocationClient mLocClient;
    boolean isFirstLoc = true; // Is the first time positioning
    private MyLocationData locData;

    /**
     * init Location option
     */
    private MyLocationConfiguration.LocationMode mCurrentMode;

    /**
     * get Location information and stored them
     */
    private MyOrientationListener myOrientationListener;
    private float mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    //车辆信息(线程同步)
    volatile List<Vehicle> vehicleList = Collections.synchronizedList(new ArrayList<Vehicle>());
    //文件名
    private String fileName = "AA00004-T_1.csv";
    //是否第一次画图
    volatile boolean isFirstPlot = true; // Is the first time positioning
    //画图线程
    PlotThread plotThread;
    //读取文件线程
    ReadFileThread readFileThread;
    //停止画图按钮（需要修改其是否可见）
    Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置布局
        setContentView(R.layout.activity_map);

        //Get Android Permissions
        if (!isPermissionGranted) {
            CheckPermission();
        }
        //Baidu地图显示及定位
        init_BaiduMap();
        //自定义控件 初始化
        initButton();
        //获取应用内存信息
        getMaxMemoryInfo();
        //获取手机内存信息
        getMemoryInfo();
    }

    /**
     * 获取手机内存信息
     */
    private void getMemoryInfo() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        Log.e("Memory", "系统总内存:" + (info.totalMem / (1024 * 1024)) + "M");
        Log.e("Memory", "系统剩余内存:" + (info.availMem / (1024 * 1024)) + "M");
        Log.e("Memory", "系统是否处于低内存运行：" + info.lowMemory);
        Log.e("Memory", "系统剩余内存低于" + (info.threshold / (1024 * 1024)) + "M时为低内存运行");
    }

    /**
     * 获取应用内存信息
     */
    private void getMaxMemoryInfo() {
        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        Log.e("Memory:", Long.toString(maxMemory / (1024 * 1024)));
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        Log.e("Memory:", Long.toString(activityManager.getMemoryClass()));
        Log.e("Memory:", Long.toString(activityManager.getLargeMemoryClass()));
    }

    /**
     * 自定义控件 初始化
     */
    private void initButton() {
        //Button
        Button loadDataButton = (Button) findViewById(R.id.loadData);
        Button dispalyButton = (Button) findViewById(R.id.dispaly);
        stopButton = (Button) findViewById(R.id.stop);
        Button clearButton = (Button) findViewById(R.id.clearMap);
        loadDataButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        stopButton.setVisibility(View.GONE);//还没开始画图，停止按钮不可见
        dispalyButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        //EditText
        EditText editText = (EditText) findViewById(R.id.edit);
        editText.setText("AA00004-T_1");
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && s.toString().matches("[A-Za-z0-9]+-[A-Za-z_0-9]+")) {
                    //去除所有空格
                    fileName = s.toString().replaceAll(" ", "") + ".csv";
                } else {
                    Toast.makeText(getApplicationContext(), "输入内容无效", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loadData:
                //读取.csv文件
                if (readFileThread == null) {
                    readFileThread = new ReadFileThread();
                    readFileThread.start();
                } else {
                    Toast.makeText(getApplicationContext(), "不要重复加载文件", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.dispaly:
                if (vehicleList.isEmpty())
                    return;
                //判断是否已经开启画图线程
                if (null == plotThread) {
                    //子线程操作UI
                    plotThread = new PlotThread();
                    plotThread.start();
                } else {
                    //继续画图
                    plotThread.setSuspend(false);
                }
                break;
            case R.id.stop:
                if (vehicleList.isEmpty())
                    return;
                //判断是否已经开启画图线程
                if (null == plotThread) {
                    Toast.makeText(this, "还没开始画图，无法暂停", Toast.LENGTH_SHORT).show();
                } else {
                    //暂停画图
                    plotThread.setSuspend(true);
                }
                break;
            case R.id.clearMap:
                //清除地图
                mBaiduMap.clear();
//                if (!vehicleList.isEmpty())
//                    Toast.makeText(this, convertGPS2BDLL(vehicleList.get(0).getLat(), vehicleList.get(0).getLng()).latitude + "|||" + vehicleList.get(0).getLng(), Toast.LENGTH_SHORT).show();

                break;
        }
    }

    /**
     * 读取csv线程
     */
    class ReadFileThread extends Thread {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //如果已经读取过了，则需要先清空list
                    if (!vehicleList.isEmpty()) {
                        vehicleList.clear();
                        isFirstPlot = true; //需要自动跳转
                        Toast.makeText(getApplicationContext(), "非首次读取csv，请等待跳转|||" + fileName, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "首次读取csv，请等待跳转|||" + fileName, Toast.LENGTH_LONG).show();
                    }
                }
            });
            //如果读取失败，则不Toast成功
            if (readCSVFile(fileName)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "读取csv文件成功", Toast.LENGTH_LONG).show();
                        //一个线程只读一次文件，读完后需要新建线程
                        readFileThread = null;
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "读取csv文件失败", Toast.LENGTH_LONG).show();
                        //一个线程只读一次文件，读完后需要新建线程
                        readFileThread = null;
                    }
                });
            }
        }
    }

    /**
     * 画图线程（可随时暂停、开始）
     */
    class PlotThread extends Thread {

        private String control = "";//只是任意的实例化一个对象而已

        private boolean suspend = false;//线程暂停标识

//        private int record = -1;//用于弹出标记点下标

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "开始画图", Toast.LENGTH_LONG).show();
                    stopButton.setVisibility(View.VISIBLE);//在画图过程中，停止按钮可见
                }
            });
//            //三点画弧线
//            for (int i = 0; (i + 2) < vehicleList.size(); i += 3) {//(i + 2)为了防止List取值溢出
//                synchronized (control) {
//                    if (suspend) {
//                        try {
//                            control.wait();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                LatLng p1 = convertGPS2BDLL(vehicleList.get(i).getLat(), vehicleList.get(i).getLng());
//                LatLng p2 = convertGPS2BDLL(vehicleList.get(i + 1).getLat(), vehicleList.get(i + 1).getLng());
//                LatLng p3 = convertGPS2BDLL(vehicleList.get(i + 2).getLat(), vehicleList.get(i + 2).getLng());
//                List<LatLng> points = new ArrayList<>();
//                points.add(p1);
//                points.add(p2);
//                points.add(p3);
//                OverlayOptions ooArc = new PolylineOptions()
//                        .color(0xAA00FF00)//设置颜色和透明度，均使用16进制显示，0xAARRGGBB，如 0xAA00FF00 其中AA是透明度，00FF00为颜色
//                        .width(8)//线宽
//                        .dottedLine(false)//是否是虚线
//                        .points(points);
//                mBaiduMap.addOverlay(ooArc);
//                //延时用
//                try {
//                    sleep(50);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
            //画点，不连线
            for (int i = 0; i < vehicleList.size(); i++) {
                synchronized (control) {
                    if (suspend) {
                        try {
                            control.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                LatLng p1 = convertGPS2BDLL(vehicleList.get(i).getLat(), vehicleList.get(i).getLng());
                if (1 == i % 2) {//减少数据量
                    OverlayOptions dotOptions = new DotOptions()
                            .center(p1)
                            .color(0xAA00FF00) //设置颜色和透明度，0xAARRGGBB，如 0xAA00FF00 其中AA是透明度，00FF00为颜色
                            .radius(5) //默认5px，现在用最小的1px
                            .visible(true);
                    mBaiduMap.addOverlay(dotOptions);
                }
                if (1 == i % 50) {//减少数据量
                    OverlayOptions textOptions = new TextOptions()
                            .text(String.valueOf(i)) //文字内容(int转String)
                            .fontColor(0xFFFF0000)//文字颜色
                            .fontSize(10)//文字大小
                            .visible(true)
                            .align(TextOptions.ALIGN_RIGHT, TextOptions.ALIGN_BOTTOM) //文字对齐方式
                            .position(p1);//文字位置
                    mBaiduMap.addOverlay(textOptions);
                }

                //延时用
//                try {
//                    sleep(20);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "画图成功", Toast.LENGTH_LONG).show();
                    stopButton.setVisibility(View.GONE);//停止按钮不可见，表示画完了
                    //一个线程只画一次图，画完后需要新建线程
                    plotThread = null;
                }
            });
        }

        public void setSuspend(boolean suspend) {
            if (!suspend) {
                synchronized (control) {
                    control.notifyAll();
                }
            }
            this.suspend = suspend;
        }
    }

    /**
     * 将GPS设备采集的原始GPS坐标转换成BD09LL
     */
    private LatLng convertGPS2BDLL(String latitude, String longitude) {

        LatLng sourceLatLng = new LatLng(Double.valueOf(latitude) / 1000000, Double.valueOf(longitude) / 1000000);

        return new CoordinateConverter()
                .from(CoordinateConverter.CoordType.GPS)    //源数据类型GPS
                .coord(sourceLatLng)                        //源数据
                .convert();
    }

    /**
     * 读取.csv文件
     *
     * @param fileName 文件名
     * @return 成功返回true，失败返回false
     */
    private boolean readCSVFile(String fileName) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        String dirPath = path + "/TDdata";
        if (!dirPath.endsWith(File.separator)) {//不是以 路径分隔符 "/" 结束，则添加路径分隔符 "/"
            dirPath = dirPath + File.separator;
        }
        File dir = new File(path + "/TDdata");
        //文件夹不存在，则创建新的
        if (!dir.exists()) {
            dir.mkdirs();
            return false;
        } else {
            //存在则读取文件
            File file = new File(dirPath + fileName);
            if (!file.exists() || !file.canRead())
                return false;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)/*getAssets().open(fileName)*/, "UTF-8"));  // 防止出现乱码
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
                Iterable<CSVRecord> csvRecords = csvParser.getRecords();
                for (CSVRecord csvRecord : csvRecords) {
                    Vehicle vehicleBean = new Vehicle();
                    vehicleBean.setVehicleplatenumber(csvRecord.get("vehicleplatenumber"));
                    vehicleBean.setDevice_num(csvRecord.get("device_num"));
                    vehicleBean.setDirection_angle(csvRecord.get("direction_angle"));
                    vehicleBean.setLng(csvRecord.get("lngx"));//lng
                    vehicleBean.setLat(csvRecord.get("latx"));//lat
                    vehicleBean.setAcc_state(csvRecord.get("acc_state"));
                    vehicleBean.setRight_turn_signals(csvRecord.get("right_turn_signals"));
                    vehicleBean.setLeft_turn_signals(csvRecord.get("left_turn_signals"));
                    vehicleBean.setHand_brake(csvRecord.get("hand_brake"));
                    vehicleBean.setFoot_brake(csvRecord.get("foot_brake"));
                    vehicleBean.setLocation_time(csvRecord.get("location_time"));
                    vehicleBean.setGps_speed(csvRecord.get("gps_speed"));
                    vehicleBean.setMileage(csvRecord.get("mileage"));
                    vehicleList.add(vehicleBean);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Init BaiduMap and Location, and Display
     */
    private void init_BaiduMap() {

        //get Baidu MapView and BaiduMap, Display Map
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);//Do not show scale controllers
        mBaiduMap = mMapView.getMap();

        //Set positioning mode
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;//NORMAL; COMPASS; FOLLOWING
        //Set positioning Marker as default
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, null));

        initLocationOption();
    }

    /**
     * set Location option
     */
    private void initLocationOption() {
        // init positioning
        mLocClient = new LocationClient(getApplicationContext());

        LocationClientOption option = new LocationClientOption();
        //Set the positioning mode to high precision mode
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll"); //Set the coordinate type
        option.setScanSpan(1000);//1 second positioning
        mLocClient.setLocOption(option);

        //Registered positioning listener function (Anonymous class)
        mLocClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {

                //Map view is not processing new received locations after destruction
                if (bdLocation == null || mMapView == null) {
                    return;
                }
                //recording Latitude, Longitude, Radius
                mCurrentLat = bdLocation.getLatitude();
                mCurrentLon = bdLocation.getLongitude();
                mCurrentAccracy = bdLocation.getRadius();

                //Monitor direction sensor
                myOrientationListener = new MyOrientationListener(getApplicationContext());
                myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
                    @Override
                    public void onOrientationChanged(float x) {
                        //Assign the acquired x-axis direction to mCurrentDirection
                        mCurrentDirection = x;
                    }
                });
                //start direction sensor
                myOrientationListener.start();

                if (!vehicleList.isEmpty()) {
                    LatLng ll = convertGPS2BDLL(vehicleList.get(0).getLat(), vehicleList.get(0).getLng());

                    //获取数据后，重定位到车辆位置
                    if (isFirstPlot) {
                        isFirstPlot = false;
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.target(ll).zoom(18.0f);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    }
                    //定位点
                    locData = new MyLocationData.Builder().accuracy(mCurrentAccracy).direction(mCurrentDirection)
                            .latitude(ll.latitude).longitude(ll.longitude).build();
                    mBaiduMap.setMyLocationData(locData);
                } else {
                    //动画重定位到定位处
                    if (isFirstLoc) {
                        isFirstLoc = false;
                        LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());//中心点
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.target(ll).zoom(18.0f);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    }
                    //没获取到数据则显示在定位点
                    locData = new MyLocationData.Builder().accuracy(mCurrentAccracy).direction(mCurrentDirection)
                            .latitude(mCurrentLat).longitude(mCurrentLon).build();
                    mBaiduMap.setMyLocationData(locData);
                }

            }
        });
    }


    /**
     * If Android SDK Version >= 6.0; need to request runtime permissions.
     */
    private void CheckPermission() {

        String[] needPermissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION, //Network positioning
                Manifest.permission.ACCESS_FINE_LOCATION,   //GPS positioning
                Manifest.permission.READ_PHONE_STATE,       //get phone state
                Manifest.permission.WRITE_EXTERNAL_STORAGE  //Used to write offline positioning data
        };

        //permissionList contains ungranted permissions
        List<String> permissionList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : needPermissions) {
                //Determine if the permission is GRANTED. If it is not, manually turns it on.
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission);
                }
            }
            //Apply for permission manually
            if (!permissionList.isEmpty()) {
                String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                ActivityCompat.requestPermissions(this, permissions, 1);
            } else {
                isPermissionGranted = true;
            }
        }
    }

    /**
     * Request permission callback function
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "In order to use the app normally, please authorize all permissions", Toast.LENGTH_SHORT).show();
                            isPermissionGranted = false;
                        } else {
                            isPermissionGranted = true;
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //MapView.onResume() is executed when the activity executes onResume to implement map life cycle management
        mMapView.onResume();

        //Current activity is on the top of the stack, start positioning
        mBaiduMap.setMyLocationEnabled(true);
        //if all permissions granted, then start LocationClient.
        if (!mLocClient.isStarted() && isPermissionGranted) {
            mLocClient.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //MapView.onPause() is executed when the activity executes onPause to implement map life cycle management
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Current activity is not on the top of the stack, stop positioning
        mBaiduMap.setMyLocationEnabled(false);
        mLocClient.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //MapView.onDestroy() is executed when the activity executes onDestroy to implement map life cycle management
        mMapView.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (Configuration.ORIENTATION_LANDSCAPE == newConfig.orientation) {
            Toast.makeText(this, "Horizontal screen", Toast.LENGTH_SHORT).show();
        } else if (Configuration.ORIENTATION_PORTRAIT == newConfig.orientation) {
            Toast.makeText(this, "Vertical screen", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 弹出提示框：判断是否确定退出
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("系统提示")
                    .setMessage("确定要退出吗?")
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finish();
                        }
                    })
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();
            dialog.show();

            //设置弹窗样式：在dialog执行show之后才能来设置
            TextView tvMsg = (TextView) dialog.findViewById(android.R.id.message);
            tvMsg.setTextSize(16);
            tvMsg.setTextColor(Color.parseColor("#4E4E4E"));

            dialog.getButton(dialog.BUTTON_NEGATIVE).setTextSize(16);
            dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#8C8C8C"));
            dialog.getButton(dialog.BUTTON_POSITIVE).setTextSize(16);
            dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1DA6DD"));

            try {
                Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
                mAlert.setAccessible(true);
                Object alertController = mAlert.get(dialog);

                Field mTitleView = alertController.getClass().getDeclaredField("mTitleView");
                mTitleView.setAccessible(true);

                TextView tvTitle = (TextView) mTitleView.get(alertController);
                if (null != tvTitle) {
                    tvTitle.setTextSize(16);
                    tvTitle.setTextColor(Color.parseColor("#000000"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }
}
