package org.zywx.wbpalmstar.plugin.uexgestureunlock.util;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

public class GestureUtil {

    public static int[] getScreenDisplay(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        int width = size.x;// 手机屏幕的宽度
        int height = size.y;// 手机屏幕的高度
        int result[] = { width, height };
        return result;
    }
}
