package com.gzt.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.gzt.utils.ShellUtils.CommandResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by 昭提 on 2018/5/4/004.
 */

public class ClutterUtils {
    /**
     * 判断某个应用程序是 不是三方的应用程序
     *
     * @param applicationInfo
     * @return 如果是第三方应用程序则返回false，如果是系统程序则返回true
     */
    public static boolean isSystemApp(ApplicationInfo applicationInfo) {
        if ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return false;
        } else if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return false;
        }
        return true;
    }

    /**
     * get the launcher activity class full name of an application by the package name
     *
     * @param context     the context of current application
     * @param packageName the package name of the application (it can be any application)
     * @return
     */
    public static String getLauncherActivityNameByPackageName(Context context, String packageName) {
        String className = null;
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);//android.intent.action.MAIN
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);//android.intent.category.LAUNCHER
        resolveIntent.setPackage(packageName);
        List <ResolveInfo> resolveinfoList = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
        if (resolveinfoList.iterator().hasNext()) {
            ResolveInfo resolveinfo = resolveinfoList.iterator().next();
            if (resolveinfo != null) {
                className = resolveinfo.activityInfo.name;
            }
        }
        return className;
    }

    /**
     * 获取字符 ch 第N次出现的位置
     *
     * @param str   字符串数据
     * @param ch    要查找的字符
     * @param count 次数
     * @return ch 第N次出现的位置
     */
    public static int getPosition(String str, char ch, int count) {
        int number = 0;
        char arr[] = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == ch) {
                number++;
            }
            if (number == count) {
                return i + 1;
            }
        }
        return number;
    }

    /**
     * 字符串转成md5
     *
     * @param content
     * @return
     */
    public static String md5(String content) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(content.getBytes("UTF-8"));
            byte[] encryption = md5.digest();//加密
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    sb.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    sb.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 写入TXT，覆盖原内容
     *
     * @param content
     * @param fileName
     * @return
     * @throws Exception
     */
    public static boolean writeTxtFile(String content, File fileName) {
        RandomAccessFile mm = null;
        boolean flag = false;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.write(content.getBytes("gbk"));
            fileOutputStream.close();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 读取TXT内容
     *
     * @param file
     * @return
     */
    public static String readTxtFile(File file) {
        String result = "";
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "gbk");
            BufferedReader br = new BufferedReader(reader);
            String s = null;
            while ((s = br.readLine()) != null) {
                result = result + s;
            }
            reader.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入TXT，换行追加写入
     *
     * @param filePath
     * @param content
     */
    public static void fileAppend(String filePath, String content) throws Exception {
        RandomAccessFile rf = new RandomAccessFile(filePath, "rw");
        long count = rf.length();
        rf.seek(count);
        content = content + "\r\n";
        rf.write(content.getBytes("gbk"));
        rf.close();
    }

    /**
     * 读取文件最后一行
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String readLastLine(File file) throws IOException {
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            return null;
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");
            long len = raf.length();
            if (len == 0L) {
                return "";
            } else {
                long pos = len - 1;
                while (pos > 0) {
                    pos--;
                    raf.seek(pos);
                    if (raf.readByte() == '\n') {
                        break;
                    }
                }
                if (pos == 0) {
                    raf.seek(0);
                }
                byte[] bytes = new byte[(int) (len - pos)];
                raf.read(bytes);
                return new String(bytes, "utf-8");
            }
        } catch (FileNotFoundException e) {
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (Exception e2) {
                }
            }
        }
        return null;
    }

    /**
     * 删除文件夹
     *
     * @param folderPath
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            java.io.File myFilePath = new java.io.File(folderPath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断文件夹是否为空
     *
     * @param folderPath
     * @return
     */
    public static boolean folderIsEmpty(String folderPath) {
        File file = new File(folderPath);
        File[] listFiles = file.listFiles();
        if (listFiles.length > 0) {
            return false;
        }
        return true;
    }

    /**
     * 删除所有文件
     *
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (String aTempList : tempList) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + aTempList);
            } else {
                temp = new File(path + File.separator + aTempList);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + aTempList);//先删除文件夹里面的文件
                delFolder(path + "/" + aTempList);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 创建文件
     *
     * @param pathname
     * @return
     * @throws Exception
     */
    public static Boolean createFile(String pathname) throws Exception {
        File file = new File(pathname);//创建文件
        if (!file.exists()) {
            if (file.createNewFile()) return true;
        } else {
            return true;
        }
        return false;
    }


    /**
     * 执行shell命令
     *
     * @param cmd
     * @param isWaitFor
     * @return
     */
    public static boolean exeCmd(String cmd, Boolean isWaitFor) {
        if (isWaitFor == null) {
            isWaitFor = true;
        }
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            if (isWaitFor) {
                process.waitFor();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (isWaitFor) {
                try {
                    if (os != null) {
                        os.close();
                    }
                    process.destroy();
                } catch (Exception e) {
                }
            }
        }
        return true;
    }

    /**
     * 检测AccessibilityServices辅助是否开启了
     *
     * @param mContext
     * @return
     */
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = "com.gzt/com.gzt.MyAbService";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Exception e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
        }
        return accessibilityFound;
    }

    /**
     * 去除前后指定字符
     *
     * @param source 目标字符串
     * @param beTrim 要删除的指定字符
     * @return 删除之后的字符串
     * 调用示例：System.out.println(trim(", ashuh  ",","));
     */
    public String trim(String source, String beTrim) {
        if (source == null) {
            return "";
        }
        source = source.trim(); // 循环去掉字符串首的beTrim字符
        if (source.isEmpty()) {
            return "";
        }
        String beginChar = source.substring(0, 1);
        if (beginChar.equalsIgnoreCase(beTrim)) {
            source = source.substring(1, source.length());
            beginChar = source.substring(0, 1);
        }
        // 循环去掉字符串尾的beTrim字符
        String endChar = source.substring(source.length() - 1, source.length());
        if (endChar.equalsIgnoreCase(beTrim)) {
            source = source.substring(0, source.length() - 1);
            endChar = source.substring(source.length() - 1, source.length());
        }
        return source;
    }

    /**
     * @return
     * @Decription TODO 获取DocumentBuilder对象
     */
    private static DocumentBuilder getDocumentBuilder() {
        // 获得DocumentBuilderFactory对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return db;
    }

    /**
     * 查找界面xml中的text属性值并返回屏幕坐标
     *
     * @param xmlPath
     * @param searchText
     * @return
     */
    public static Integer[] findTextForXml(String xmlPath, String searchText) {
        xmlPath = "file://" + xmlPath;
        Integer[] res = new Integer[2];
        try {
            DocumentBuilder builder = ClutterUtils.getDocumentBuilder();
            Document document = builder.parse(xmlPath);
            NodeList nodes = document.getElementsByTagName("node");
            for (int i = 0; i < nodes.getLength(); i++) {
                //获取当前node节点
                Element node = (Element) nodes.item(i);
                String text = node.getAttribute("text");
                if (text == "") {
                    text = node.getAttribute("content-desc");
                }
                if (searchText.equals(text)) {
                    String bounds = node.getAttribute("bounds");
                    bounds = bounds.replace("][", ",").replace("[", "").replace("]", "");
                    String[] arr = bounds.split(",");
                    res[0] = Integer.parseInt(arr[0]) + (Integer.parseInt(arr[2]) - Integer.parseInt(arr[0])) / 2;
                    res[1] = Integer.parseInt(arr[1]) + (Integer.parseInt(arr[3]) - Integer.parseInt(arr[1])) / 2;
                    return res;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 查找界面xml中的text属性值并返回屏幕坐标
     *
     * @param xmlPath
     * @param searchText
     * @return
     */
    public static Integer[] findTextForXml(String xmlPath, String searchText, Boolean indexof) {
        xmlPath = "file://" + xmlPath;
        Integer[] res = new Integer[2];
        try {
            DocumentBuilder builder = ClutterUtils.getDocumentBuilder();
            Document document = builder.parse(xmlPath);
            NodeList nodes = document.getElementsByTagName("node");
            for (int i = 0; i < nodes.getLength(); i++) {
                //获取当前node节点
                Element node = (Element) nodes.item(i);
                String text = node.getAttribute("text");
                if (text == "") {
                    text = node.getAttribute("content-desc");
                }
                if (text.indexOf(searchText) != -1) {
                    String bounds = node.getAttribute("bounds");
                    bounds = bounds.replace("][", ",").replace("[", "").replace("]", "");
                    String[] arr = bounds.split(",");
                    res[0] = Integer.parseInt(arr[0]) + (Integer.parseInt(arr[2]) - Integer.parseInt(arr[0])) / 2;
                    res[1] = Integer.parseInt(arr[1]) + (Integer.parseInt(arr[3]) - Integer.parseInt(arr[1])) / 2;
                    return res;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 查找界面xml中的text属性并返回text的值
     *
     * @param xmlPath
     * @param searchText
     * @return
     */
    public static String findTextForXmlIndexOf(String xmlPath, String searchText) {
        xmlPath = "file://" + xmlPath;
        try {
            DocumentBuilder builder = ClutterUtils.getDocumentBuilder();
            Document document = builder.parse(xmlPath);
            NodeList nodes = document.getElementsByTagName("node");
            for (int i = 0; i < nodes.getLength(); i++) {
                //获取当前node节点
                Element node = (Element) nodes.item(i);
                String text = node.getAttribute("text");
                if (text.contains(searchText)) {
                    return text;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static void clearAppData(String packageName) {
        CommandResult cs = ShellUtils.execCommand("pm clear " + packageName, true);
        if (!cs.successMsg.contains("Success")) {
            ShellUtils.execCommand("pm clear " + packageName, true);
        }
    }

    /**
     * 获取随机数
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRandomInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 清理内存
     *
     * @param context
     */
    public static void killAllProcess(Context context) throws Exception {
        List <String> pakgs = new ArrayList();
        ActivityManager activityManger = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List <RunningAppProcessInfo> list = activityManger.getRunningAppProcesses();
        int count = 0;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ActivityManager.RunningAppProcessInfo appInfo = list.get(i);
                String[] pkgList = appInfo.pkgList;
                if (appInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    for (int j = 0; j < pkgList.length; j++) {
                        if (!pkgList[j].equals("com.tencent.mm") && !pkgList[j].equals("com.fantuanzi.myapplication") && !pkgList[j].equals("com.goldenfrog.vyprvpn.app") && !pkgList[j].equals("de.robv.android.xposed.installer") && !pkgList[j].equals("de.robv.android.xposed.mods.appsettings")) {
                            mylog("清理了" + pkgList[j]);
                            new ExeCommand().run("am force-stop " + pkgList[j], 10000).getResult();
                        }
                    }
                }
            }
        }
    }

    /**
     * @param packageName
     * @return 版本名
     */
    public static int getAPPversion(String packageName, Context context) {
        List <PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageName.equals(packageInfo.packageName)) {
                return packageInfo.versionCode;
            }
        }
        return -1;
    }

    /**
     * 启动app
     *
     * @param packageName
     * @param activityName
     */
    public static void runApp(String packageName, String activityName) {
        String cr = new ExeCommand().run("am start -n " + packageName + "/" + activityName, 10000).getResult();
        if (!cr.contains("its current task has been brought to the front")) {
            runApp(packageName, activityName);
        }
    }

    /**
     * 启动app
     *
     * @param packageName
     * @param activityName
     */
    public static void runAppOnce(String packageName, String activityName) {
        new ExeCommand().run("am start -n " + packageName + "/" + activityName, 10000).getResult();
    }

    /**
     * 关闭app
     *
     * @param packageName
     */
    public static void killApp(String packageName) {
        new ExeCommand().run("am force-stop " + packageName, 10000).getResult();
    }

    /**
     * 获取最顶层的activity类名
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName();
    }

    /**
     * 判断activity是否在最顶层
     *
     * @param activityName activity类名
     * @return 如果在顶层返回true 否则返回false
     */
    public static boolean isTopActivity(Context context, String activityName) {
        return getTopActivity(context).contains(activityName);
    }

    /**
     * 判断activity是否在最顶层
     *
     * @param packageName 包名
     * @return 如果在顶层返回true 否则返回false
     */
    public static boolean isTopPackage(Context context, String packageName) {
        return getTopActivity(context).contains(packageName);
    }

    /**
     * 微信浏览器打开网页
     *
     * @param url url
     */
    public static void weiXinUrl(String url) {
        new ExeCommand().run("am start -n com.tencent.mm/.plugin.webview.ui.tools.WebViewUI -d " + "'" + url + "'", 10000).getResult();
    }

    /**
     * 全局滑动操作
     *
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param duration
     */
    public static void swipe(int x0, int y0, int x1, int y1,int duration) {
        new ExeCommand().run("input swipe " + x0 + " " + y0 + " " + x1 + " " + y1 + " " + duration, 10000).getResult();
    }

    /**
     * 全局滑动操作
     *
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     */
    public static void swipe(int x0, int y0, int x1, int y1) {
        new ExeCommand().run("input swipe " + x0 + " " + y0 + " " + x1 + " " + y1, 10000).getResult();
    }

    /**
     * 按下home键
     */
    public static void pressHome() {
        new ExeCommand().run("input keyevent 3", 10000).getResult();
    }

    /**
     * 按下电源键
     */
    public static void pressPOWER() {
        new ExeCommand().run("input keyevent 26", 10000).getResult();
    }

    /**
     * 获取设备imei
     *
     * @param context
     * @return
     */
    public static String getImei(Context context) {
        String imei = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        try {
            imei = tm.getDeviceId();
        } catch (SecurityException e) {
        }
        return imei;
    }


    public static void mylog(String content) {
        try {
            ClutterUtils.fileAppend(Common.LOG_FILE, content);
        } catch (Exception e) {
        }
    }


    /**
     * 将异常信息转化成字符串
     *
     * @param t
     * @return
     * @throws IOException
     */
    public static String exception(Throwable t) {
        if (t == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            t.printStackTrace(new PrintStream(baos));
        } finally {
            try {
                baos.close();
            } catch (Exception e) {
            }
        }
        return baos.toString();
    }

    public static void tap(int x, int y) {
        new ExeCommand().run("input tap " + x + " " + y, 10000).getResult();
    }

    public static void back() {
        new ExeCommand().run("input keyevent 4", 10000).getResult();
    }

    public static void showToast(final Activity activity, final String word, final long time) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                final Toast toast = Toast.makeText(activity, word, Toast.LENGTH_LONG);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        toast.cancel();
                    }
                }, time);
            }
        });
    }

    public static void showToast(final Activity ctx, final String msg) {
        // 判断是在子线程，还是主线程
        if ("main".equals(Thread.currentThread().getName())) {
            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
        } else {
            // 子线程
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /*
     * 获取当前程序的版本名
     */
    public static String getVersionName(Context context) throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packInfo.versionName;
    }

    /*
     * 获取当前程序的版本号
     */
    public static int getVersionCode(Context context) throws Exception {
        //获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packInfo.versionCode;
    }

    /**
     * 随机指定范围内N个不重复的数
     * 最简单最基本的方法
     * @param min 指定范围最小值
     * @param max 指定范围最大值
     * @param n 随机数个数
     */
    public static int[] getRandomInt(int min, int max, int n){
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while(count < n) {
            int num = getRandomInt(min,max);
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if(num == result[j]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    /**
     * 判断数组中是否包含某个数字
     * @param arr
     * @param targetValue
     * @return
     */
    public static boolean inIntArray(int[] arr, int targetValue) {
        for (int val: arr) {
            if (val == targetValue) {
                return true;
            }
        }
        return false;
    }
}
