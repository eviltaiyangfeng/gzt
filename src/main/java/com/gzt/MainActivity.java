package com.gzt;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.gzt.utils.ClutterUtils;
import com.gzt.utils.NetWorkUtils;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //浮窗提示文本控件
    private TextView mPrompt;
    private static int browserCount;
    private SharedPreferences mSharedPreferences;
    private WakeLock wakeLock;
    private EditText bzET;
    private String bz;
    private EditText nameET;
    private String name;
    private ClipboardManager mClipboardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化功能配置文件
        mSharedPreferences = getSharedPreferences("gzt_config", 0);
        bzET = findViewById(R.id.bz);
        nameET = findViewById(R.id.name);
        bz = mSharedPreferences.getString("bz", "0");
        name = mSharedPreferences.getString("name", "");
        if (!bz.equals("0")) {
            bzET.setText(bz);
        }
        if (!TextUtils.isEmpty(name)) {
            nameET.setText(name);
        }
        acquireWakeLock();
        //请求root权限
        ClutterUtils.exeCmd("su", false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
    }

    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "PostLocationService");
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    //释放设备电源锁
    private void releaseWakeLock() {
        if (null != wakeLock) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    @SuppressLint("ApplySharedPref")
    public void startTaskBtnClick(View view) {
        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        bz = bzET.getText().toString();
        mSharedPreferences.edit().putString("bz", bz).commit();
        name = nameET.getText().toString();
        mSharedPreferences.edit().putString("name", name).commit();
        if (mPrompt != null) {
            Toast.makeText(this, "脚本已经在运行", Toast.LENGTH_SHORT).show();
            return;
        }
        createToucher();
        Button startTaskBtn = findViewById(R.id.startTaskBtn);
        startTaskBtn.setText("小浮窗正在工作");
        startTaskBtn.setClickable(false);
        //静音模式
        myPrompt("开启静音模式");
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            audioManager.getStreamVolume(AudioManager.STREAM_RING);
        }
        //开启accessibilityservice
        myPrompt("开启辅助");
        if (!ClutterUtils.isAccessibilitySettingsOn(getApplicationContext())) {
            MyAbService.startAccessibilityService();
        }
        Thread startTask = new Thread(new Runnable() {
            @Override
            public void run() {
                browserTask();
            }
        });
        startTask.setDaemon(true);
        startTask.start();
        moveTaskToBack(false);
    }

    private void randSwipe() {
        int sRandX = ClutterUtils.getRandomInt(30, 700);
        int sRandY = ClutterUtils.getRandomInt(600, 1200);
        int eRandX = ClutterUtils.getRandomInt(30, 700);
        int eRandY = ClutterUtils.getRandomInt(sRandY - 300, sRandY - 200);
        ClutterUtils.swipe(sRandX, sRandY, eRandX, eRandY);
    }

    private int initDay;
    private int[] sleepHours;
    @SuppressLint("ApplySharedPref")
    private void browserTask() {
        try {
            //初始化随机休息时间点
            Calendar sCalendar = Calendar.getInstance();
            if (initDay != Calendar.getInstance().get(Calendar.DATE)) {
                initDay = Calendar.getInstance().get(Calendar.DATE);
                sleepHours = ClutterUtils.getRandomInt(10,23,ClutterUtils.getRandomInt(0,5));
            }
            //判断当前是否为休息时间点
            int countFlag = 0;
            while (ClutterUtils.inIntArray(sleepHours,Calendar.getInstance().get(Calendar.HOUR_OF_DAY))) {
                countFlag++;
                Thread.sleep(3000);
                myPrompt("现在是休息时间"+ countFlag);
            }
            //如果是息屏状态就点亮屏幕
            PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            if (!powerManager.isScreenOn()) {
                ClutterUtils.pressPOWER();
            }
            myPrompt("启动微信");
            ClutterUtils.runAppOnce("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            while (true) {
                MyAbService.clickByText(getApplicationContext(), "　取消　");
                backHomePage();
                if (MyAbService.findByText(getApplicationContext(), "发现")) {
                    Thread.sleep(1000);
                    break;
                }
                Thread.sleep(1000);
                myPrompt("等待进入微信主界面");
            }
            while (!MyAbService.findByText_noThrows(getApplicationContext(), "新的朋友")) {
                myPrompt("等待进入通讯录");
                MyAbService.clickByText_noThrows(getApplicationContext(),"通讯录");
                Thread.sleep(1000);
            }
            countFlag = 0;
            Thread.sleep(1000);
            while (!MyAbService.clickByText_noThrows(this, name)) {
                if (countFlag>3) {
                    countFlag=0;
                    randSwipe();
                }
                countFlag++;
                myPrompt("点击"+name+countFlag);
                Thread.sleep(1000);
            }
            countFlag = 0;
            Boolean clickSendMessage = false;
            while (!MyAbService.findByDesc_noThrows(getApplicationContext(),"更多功能按钮，已折叠")) {
                countFlag++;
                if (!clickSendMessage) {
                    if (MyAbService.clickByText_noThrows(getApplicationContext(),"发消息")) {
                        clickSendMessage = true;
                    }
                }
                myPrompt("等待进入聊天界面"+countFlag);
                Thread.sleep(1000);
            }
            Thread.sleep(1000);
            String viewId = MyAbService.getFirstViewIdResourceName(getApplicationContext(),"android.widget.EditText");
            while (viewId == null) {
                Thread.sleep(1000);
                myPrompt("正在找寻编辑框");
                viewId = MyAbService.getFirstViewIdResourceName(getApplicationContext(),"android.widget.EditText");
            }
            Thread.sleep(1000);
            String task_link = "http://task.ls127.com/api_gettask.php?uid=16046&bz=" + bz;
            mClipboardManager.setPrimaryClip(ClipData.newPlainText(task_link,task_link));
            myPrompt("粘贴链接");
            while (!MyAbService.paste(getApplicationContext(), viewId)) {
                Thread.sleep(1000);
            }
            Thread.sleep(1000);
            myPrompt("发送");
            while (!MyAbService.clickByText_noThrows(getApplicationContext(),"发送")) {
                Thread.sleep(1000);
            }
            Thread.sleep(3000);
            myPrompt("点击链接");
            while (!MyAbService.clickChatLinkByclassName(getApplicationContext(),"android.view.View")) {
                Thread.sleep(1000);
                myPrompt("正在点击");
            }
            String taskHint = "";
            if (!loadingWxWeb()) {
            } else {
                if (MyAbService.findByText(MainActivity.this, "网页由 task.ls127.com 提供")) {
                    if (MyAbService.findByDesc_noThrows(getApplicationContext(),"初次请求成功,请继续.")) {
                        taskHint = "初次请求成功";
                    } else if (MyAbService.findByDescContains_noThrows(getApplicationContext(),"请求过快了,请保证连接完全打开")) {
                        taskHint = "请求过快";
                    } else if (MyAbService.findByDescContains_noThrows(getApplicationContext(),"本微信没有阅读能力")) {
                        taskHint = "没有阅读能力";
                    } else if (MyAbService.findByDescContains_noThrows(getApplicationContext(),"该微信已达到您设置的小时量")) {
                        taskHint = "已达到小时量";
                    } else if (MyAbService.findByDescContains_noThrows(getApplicationContext(),"该微信已达到设置的日量")) {
                        taskHint = "已达到日量";
                    }
                } else {
                    myPrompt("浏览公众号文章");
                    if (MyAbService.findByText(MainActivity.this, "网页由 mp.weixin.qq.com 提供")) {
                        //滑动次数随机
                        int swipeCount = ClutterUtils.getRandomInt(1, 20);
                        for (int i = 0; i < swipeCount; i++) {
                            //向上滑动坐标随机
                            randSwipe();
                            //滑动间隔随机秒
                            int delay = ClutterUtils.getRandomInt(0, 10);
                            for (int d = delay; d > 0; d--) {
                                myPrompt(d + "秒后继续滑");
                                Thread.sleep(1000);
                            }
                        }
                    } else {
                        myPrompt("页面内容未知，检测网络连接");
                        while (!NetWorkUtils.isNetworkConnected(this)) {
                            myPrompt("网络不可用");
                            for (int i = 10; i > 0; i--) {
                                myPrompt(i + "秒后重新检测网络连接");
                                Thread.sleep(1000);
                            }
                        }
                        myPrompt("网络连接正常");
                    }
                }
                browserCount++;
            }
            myPrompt("返回主界面");
            backHomePage();
            Thread.sleep(1000);
            ClutterUtils.pressHome();
            if (powerManager.isScreenOn()) {
                ClutterUtils.pressPOWER();
            }
            int sleepCount = ClutterUtils.getRandomInt(1, 18);
            switch (taskHint) {
                case "初次请求成功":
                    myPrompt("初次请求成功");
                    sleepCount = 1;
                    break;
                case "请求过快":
                    myPrompt("请求过快");
                    sleepCount = 2;
                    break;
                case "没有阅读能力":
                    for (int i = 28800; i > 0; i--) {
                        myPrompt("没有阅读能力" + i + "秒后重新检测");
                        Thread.sleep(3000);
                    }
                    break;
                case "已达到小时量":
                    sleepCount = 0;
                    countFlag =0;
                    int h = sCalendar.get(Calendar.HOUR_OF_DAY);
                    while (h == Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                        countFlag++;
                        myPrompt(h+"已达到小时量"+ countFlag);
                        Thread.sleep(3000);
                    }
                    break;
                case "已达到日量":
                    sleepCount = 0;
                    browserCount = 0;
                    int d = sCalendar.get(Calendar.DATE);
                    countFlag =0;
                    while (d == Calendar.getInstance().get(Calendar.DATE)) {
                        countFlag++;
                        myPrompt(d+"日任务已完成"+ countFlag);
                        Thread.sleep(3000);
                    }
                    //次日1-8点内随机时间开始执行
                    int randHourStart = ClutterUtils.getRandomInt(1,8);
                    countFlag =0;
                    while (randHourStart != Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                        countFlag++;
                        myPrompt(randHourStart+"点开始执行"+ countFlag);
                        Thread.sleep(3000);
                    }
                    break;
                default:
                    break;
            }
            for (int i = sleepCount * 60; i > 0; i--) {
                myPrompt(taskHint +" "+ i + "秒后继续第" + (browserCount + 1) + "篇");
                Thread.sleep(1000);
            }
            browserTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backHomePage() throws Exception {
        while (MyAbService.clickByDesc(getApplicationContext(), "返回")) {
            Thread.sleep(500);
        }
    }

    public boolean loadingWxWeb() throws Exception {
        Boolean webview;
        int count = 1;
        do {
            myPrompt("等待进入网页" + " " + count);
            webview = ClutterUtils.isTopActivity(getApplicationContext(), "ui.tools.WebViewUI");
            if (count>60) {
                myPrompt("打开网页失败");
                return false;
            }
            Thread.sleep(1000);
            count++;
        } while (!webview);
        Boolean waitProgress = MyAbService.findByDesc(getApplicationContext(), "progressBar");
        int waitingToWeb = 0;
        while (!waitProgress) {
            if (waitingToWeb > 1200) {
                return false;
            } else {
                waitingToWeb++;
            }
            MyAbService.clickByText(getApplicationContext(), "　取消　");
            myPrompt("尚未加载网页" + waitingToWeb);
            Thread.sleep(100);
            waitProgress = MyAbService.findByDesc(getApplicationContext(), "progressBar");
        }
        Boolean progress;
        count = 1;
        do {
            myPrompt("等待页面加载完成" + " " + count);
            if (count>60) {
                myPrompt("网页加载超时");
                return false;
            }
            progress = MyAbService.findByDesc(getApplicationContext(), "progressBar");
            Thread.sleep(1000);
            count++;
        } while (progress);
        myPrompt("页面加载完毕");
        return true;
    }

    private void createToucher() {
        if (mPrompt != null) {
            return;
        }
        //赋值WindowManager&LayoutParam.
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        WindowManager mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //屏幕宽高
        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        //params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        //设置窗口初始停靠位置.
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 10;
        params.y = 88;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局.
        LinearLayout promptLayout = (LinearLayout) inflater.inflate(R.layout.prompt, null);
        //设置悬浮窗口长宽数据.
        params.height = 1270;
        params.width = width;
        //添加toucherlayout
        mWindowManager.addView(promptLayout, params);

        //主动计算出当前View的宽高信息.
        promptLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        //浮动窗口按钮.
        mPrompt = promptLayout.findViewById(R.id.myPrompt);
    }

    public void myPrompt(String text) {
        final Handler handler = new Handler(getMainLooper()) {
            public void handleMessage(Message msg) {
                String str = (String) msg.obj;
                mPrompt.setText(str);
                removeMessages(0, msg);
            }
        };
        Message msg = new Message();
        msg.what = 0;
        msg.obj = text;
        // 把消息发送到主线程，在主线程里现实Toast
        handler.sendMessage(msg);
    }

    public void update(View view) {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                checkVersion();
                Looper.loop();
            }
        }.start();
    }

    //对比本程序的版本号和最新程序的版本号
    public boolean checkVersion() {
        URL infoUrl;
        InputStream inStream;
        String line;
        int versionCode;
        try {
            infoUrl = new URL("http://www.fantuanzi.com/updateGZT.php");
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setConnectTimeout(5000);
            httpConnection.setReadTimeout(5000);
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                while ((line = reader.readLine()) != null) strber.append(line).append("\n");
                inStream.close();
                if (!TextUtils.isEmpty(strber.toString())) {
                    JSONObject jsonObject = new JSONObject(strber.toString());
                    versionCode = jsonObject.optInt("versionCode");
                    //如果检测本程序的版本号小于服务器的版本号，那么提示用户更新
                    if (ClutterUtils.getVersionCode(MainActivity.this) < versionCode) {
                        showDialogUpdate();//弹出提示版本更新的对话框
                    }else{
                        //否则吐司，说现在是最新的版本
                        Toast.makeText(this,"当前已经是最新的版本",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            httpConnection.disconnect();
        } catch (Exception e) {
            Toast.makeText(this,"错误:"+e.getMessage(),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 提示版本更新的对话框
     */
    private void showDialogUpdate() {
        // 这里的属性可以一直设置，因为每次设置后返回的是一个builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置提示框的标题
        builder.setTitle("版本升级").
                // 设置提示框的图标
                        setIcon(R.mipmap.ic_launcher).
                // 设置要显示的信息
                        setMessage("发现新版本！请及时更新").
                // 设置确定按钮
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(MainActivity.this, "选择确定哦", 0).show();
                        loadNewVersionProgress();//下载最新的版本程序
                    }
                }).

                // 设置取消按钮,null是什么都不做，并关闭对话框
                        setNegativeButton("取消", null);

        // 生产对话框
        AlertDialog alertDialog = builder.create();
        // 显示对话框
        alertDialog.show();
    }

    /**
     * 下载新版本程序，需要子线程
     */
    private void loadNewVersionProgress() {
        final   String uri="http://www.fantuanzi.com/gzt.apk";
        final ProgressDialog pd;    //进度条对话框
        pd = new  ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        //启动子线程下载任务
        new Thread(){
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(uri, pd);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    //下载apk失败
                    Toast.makeText(getApplicationContext(), "下载新版本失败", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }}.start();
    }

    /**
     * 从服务器获取apk文件的代码
     * 传入网址uri，进度条对象即可获得一个File文件
     * （要在子线程中执行哦）
     */
    public static File getFileFromServer(String uri, ProgressDialog pd) throws Exception{
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            URL url = new URL(uri);
            HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            long time= System.currentTimeMillis();//当前时间的毫秒数
            File file = new File(Environment.getExternalStorageDirectory(), time+"updata.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len ;
            int total=0;
            while((len =bis.read(buffer))!=-1){
                fos.write(buffer, 0, len);
                total+= len;
                //获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        }
        else{
            return null;
        }
    }

    /**
     * 安装apk
     */
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
