package org.zywx.wbpalmstar.plugin.uexgestureunlock.util;

import org.zywx.wbpalmstar.plugin.uexgestureunlock.entity.GesturePoint;

public interface OnDrawArrowListener {
    public void onDrawArrow(GesturePoint first, GesturePoint second, int blockWidth);
    public void onErrorState();
    public void clearAllArrow();
}
