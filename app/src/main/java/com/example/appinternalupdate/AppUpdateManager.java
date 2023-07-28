package com.example.appinternalupdate;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AppUpdateManager {

    // 主流应用商店对应的包名
    private static final ArrayList<String> mainAppStorePackageName = new ArrayList<String>() {{
        add("com.tencent.android.qqdownloader");    // 应用宝
        add("com.huawei.appmarket");                // 华为应用市场（智汇云）
        add("com.xiaomi.market");                   // 小米应用商店
        add("com.bbk.appstore");                    // vivo 市场
        add("com.heytap.market");                   // oneplus 市场
        add("com.oneplus.market");                  // oneplus 市场
        add("com.sec.android.app.samsungapps");     // 三星应用商店
        add("com.lenovo.leos.appstore");            // 联想乐商店
        add("com.meizu.mstore");                    // 魅族应用商店
        add("com.smartisan.appstore");              // 锤子应用市场
        add("com.aspire.mm");                       // 移动MM商城
        add("com.android.vending");                 // Google Play
        add("com.qihoo.appstore");                  // 360手机助手
        add("com.baidu.appsearch");                 // 百度手机助手
        add("com.wandoujia.phoenix2");              // 豌豆荚
        add("com.taobao.appcenter");                // 淘宝手机助手
        add("com.hiapk.marketpho");                 // 安卓市场
        add("cn.goapk.market");                     // 安智市场
        add("com.yingyonghui.market");              // 应用汇
        add("com.sogou.androidtool");               // 搜狗应用中心
        add("com.infinit.wostore.ui");              // 联通沃商店
        add("com.gionee.aora.market");              // 金立易用汇
        add("");// 阿里YunOS应用中心
    }};

    /**
     * 1.判断用户手机内是否安装应用市场
     *  先获取app所有安装的包名，然后判断是否包含应用市场的包名。
     *  @param context
     */
    public static String isAppStoreAvailable(Context context) {
        // 1. 获取package manager
        final PackageManager packageManager = context.getPackageManager();
        // 2. 获取所有已安装程序的包信息
        // 方式 1 2 3:
        List<PackageInfo> packageInfos1 = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);// 默认0
        List<PackageInfo> packageInfos2 = packageManager.getInstalledPackages(0);
        List<PackageInfo> packageInfos3 = null;
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            packageInfos3 = packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0));
        } else {
            packageInfos3 = packageManager.getInstalledPackages(0);
        }

        /*
         * GET_META_DATA:ComponentInfo flag:返回与组件关联的ComponentInfo.metaData数据包。这适用于任何返回ComponentInfo子类的API。
         * GET_SHARED_LIBRARY_FILES:ApplicationInfo flag:返回与应用程序关联的共享库的路径。这适用于任何直接返回ApplicationInfo类或嵌套在另一个类中的API。
         * MATCH_SYSTEM_ONLY:Querying flag: 查询标志：仅包括带有 ApplicationInfo.FLAG_SYSTEM 标记的应用程序的组件
         * MATCH_UNINSTALLED_PACKAGES:MATCH_ANY_USER和MATCH_UNINSTALLED_PACKAGES的组合意味着任何已知的包
         */
        // 方式 4 5 6:
        List<ApplicationInfo> applicationInfos1 = packageManager.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES);
        List<ApplicationInfo> applicationInfos2 = packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES);
        List<ApplicationInfo> applicationInfos3 = packageManager.getInstalledApplications(0);

        // 方式 7 8: 获取有启动活动的应用程序列表
        Intent filter1 = new Intent(Intent.ACTION_MAIN);
        filter1.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activityLists1 = packageManager.queryIntentActivities(filter1, PackageManager.GET_RESOLVED_FILTER);

        Intent intent2 = new Intent();
        intent2.setAction("android.intent.action.MAIN");
        intent2.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> appMarketLists2 = packageManager.queryIntentActivities(intent2, 0);

        // 方式 9: 获取应用市场类的应用程序列表 —— 不全，不建议使用
        Intent filter3 = new Intent(Intent.ACTION_MAIN);
        filter3.addCategory(Intent.CATEGORY_APP_MARKET);    // 此活动允许用户浏览和下载新应用程序。
        List<ResolveInfo> appMarketLists3 = packageManager.queryIntentActivities(filter3, PackageManager.GET_RESOLVED_FILTER);
//        List<ResolveInfo> appMarketLists3 = packageManager.queryIntentActivities(filter3, 0);

        // 3. 将应用程序包名字取出, 存入 appNameList 中
        // 用于存储所有已安装程序的包名
        List<String> appNameList = new ArrayList<>();
        // 方式 1 2 3 对应:
//        if (packageInfos != null) {
//            for (int i = 0; i < packageInfos.size(); i++) {
//                String pName = packageInfos.get(i).packageName;
//                Log.e("whh", "已安装包名 - packageName = " +  pName);
//                appNameList.add(pName);
//            }
//        }

        // 方式 4 5 6 对应:
//        if (applicationInfos1 != null) {
//            for (int i = 0; i < applicationInfos1.size(); i++) {
//                String pName = applicationInfos1.get(i).packageName;
//                Log.e("whh", "已安装包名 - packageName = " +  pName);
//                appNameList.add(pName);
//            }
//        }

        // 方式 7 8 9 对应:
        if (appMarketLists2 != null) {
            for (int i = 0; i < appMarketLists2.size(); i++) {
                String pName  = appMarketLists2.get(i).activityInfo.packageName;
                Log.e("whh", "已安装包名 - packageName = " +  pName);
                appNameList.add(pName);
            }
        }

        // 4. 判断 appNameList 中是否有目标程序的包名
        String marketName = null;
        // 方式 1: 迭代器
//        Iterator it = mainAppStorePackageName.iterator();
//        while (it.hasNext()) {
//            if (pName.contains(it.next().toString())) {
//                hasPName = true;
//            }
//        }
        // 方式 2: foreach
        for (String packageName : mainAppStorePackageName) {
            if (appNameList.contains(packageName)) {
                marketName = packageName;
                break;
            }
        }

        return marketName;
    }

    /**
     *  2.根据包名进入应用市场的app详情界面下载apk
     *      注意:当跳转到具体的app界面时，需要判断手机是否安装了需要跳转的应用市场，没有安装直接跳转会出现问题。
     *          跳转前需要判断手机是否安装了指定应用市场，如果没有安装，需要提示用户安装，然后才能进行跳转。
     *  @param context      context
     *  @param appPkg       目标App的包名
     *  @param marketPkg    应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面
     */
    public static void jumpToMarketAppDetail(Context context, String appPkg, String marketPkg) {
        try {
            Log.e("whh", "context = " +  context + ", appPkg = " + appPkg + ", marketPkg = " + marketPkg);
            if (TextUtils.isEmpty(appPkg)) {
                return;
            }

            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  3. 未安装应用市场，那么根据后台返回的地址，用浏览器打开地址进行下载
     */
    public static void jumpToBrowserAppDetail(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
