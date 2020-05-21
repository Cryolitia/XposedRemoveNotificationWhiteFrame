package cn.nexus6p.removewhitenotificationforbugme;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class main implements IXposedHookInitPackageResources, IXposedHookLoadPackage {

    private static ArrayList<String> nameStrings = new ArrayList<>();
    static {
        nameStrings.add("notification_background_custom_padding_left");
        nameStrings.add("notification_background_custom_padding_top");
        nameStrings.add("notification_background_custom_padding_right");
        nameStrings.add("notification_background_custom_padding_bottom");
        nameStrings.add("notification_custom_view_margin_end");
        nameStrings.add("notification_custom_view_margin_start");
        nameStrings.add("notification_custom_view_corner_radius");
        nameStrings.add("notification_row_extra_padding");
        nameStrings.add("notification_custom_view_margin");
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        for (String nameString : nameStrings) {
            try {
                final int ID = resparam.res.getIdentifier(nameString, "dimen", "com.android.systemui");
                resparam.res.setReplacement(ID, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                final ArrayList<Integer> IDs = new ArrayList<>();
                for (String nameString : nameStrings) {
                    try {
                        IDs.add(context.getResources().getIdentifier(nameString, "dimen", "com.android.systemui"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                XposedHelpers.findAndHookMethod(Resources.class, "getDimensionPixelSize", int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        int resID = (int) param.args[0];
                        for (Integer ID : IDs) {
                            if (resID == ID) {
                                param.setResult(0);
                                return;
                            }
                        }
                    }
                });
            }
        });
    }
}
