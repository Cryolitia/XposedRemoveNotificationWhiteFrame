package cn.nexus6p.removewhitenotificationforbugme;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class main implements IXposedHookInitPackageResources, IXposedHookLoadPackage {
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals("com.android.systemui")) return;
        final int leftID = resparam.res.getIdentifier("notification_background_custom_padding_left","dimen","com.android.systemui");
        final int topID = resparam.res.getIdentifier("notification_background_custom_padding_top","dimen","com.android.systemui");
        final int rightID = resparam.res.getIdentifier("notification_background_custom_padding_right","dimen","com.android.systemui");
        final int bottomID = resparam.res.getIdentifier("notification_background_custom_padding_bottom","dimen","com.android.systemui");
        resparam.res.setReplacement(leftID,0);
        resparam.res.setReplacement(topID,0);
        resparam.res.setReplacement(rightID,0);
        resparam.res.setReplacement(bottomID,0);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.android.systemui")) return;
        final ClassLoader classLoader = lpparam.classLoader;
        XposedHelpers.findAndHookMethod(Application.class.getName(), classLoader, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Context context = (Context) param.args[0];
                final int leftID = context.getResources().getIdentifier("notification_background_custom_padding_left","dimen","com.android.systemui");
                final int topID = context.getResources().getIdentifier("notification_background_custom_padding_top","dimen","com.android.systemui");
                final int rightID = context.getResources().getIdentifier("notification_background_custom_padding_right","dimen","com.android.systemui");
                final int bottomID = context.getResources().getIdentifier("notification_background_custom_padding_bottom","dimen","com.android.systemui");
                XposedHelpers.findAndHookMethod(Resources.class, "getDimensionPixelSize", int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        int resID = (int) param.args[0];
                        if (resID==leftID||resID==topID||resID==rightID||resID==bottomID) {
                            param.setResult(0);
                        }
                    }
                });
            }
        });
    }
}
