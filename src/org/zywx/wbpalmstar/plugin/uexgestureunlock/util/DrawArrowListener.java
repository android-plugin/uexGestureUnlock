package org.zywx.wbpalmstar.plugin.uexgestureunlock.util;

import android.content.Context;
import android.widget.FrameLayout;

import org.zywx.wbpalmstar.plugin.uexgestureunlock.entity.ArrowPoint;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.entity.GesturePoint;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.ConfigGestureVO;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.widget.ArrowSlideLine;

import java.util.ArrayList;
import java.util.List;

public class DrawArrowListener implements OnDrawArrowListener{

    private Context mContext;
    private FrameLayout mParent;
    private List<ArrowSlideLine> list;
    private ConfigGestureVO mData;

    public DrawArrowListener(Context mContext, FrameLayout mParent, ConfigGestureVO data) {
        this.mContext = mContext;
        this.mParent = mParent;
        list = new ArrayList<ArrowSlideLine>();
        this.mData = data;
    }

    @Override
    public void onDrawArrow(GesturePoint first, GesturePoint second, int blockWidth) {
        ArrowPoint startPoint, centerPoint;
        startPoint = new ArrowPoint(first.getCenterX(), first.getCenterY() - 7 * blockWidth / 24);
        centerPoint = new ArrowPoint(first.getCenterX(), first.getCenterY());
        double angle = (Math.atan2(first.getCenterY() - second.getCenterY(),
                first.getCenterX() - second.getCenterX()) - Math.PI / 2) * 180 / Math.PI;
        ArrowSlideLine arrow = new ArrowSlideLine(mContext,
                startPoint, centerPoint, (float)angle,
                blockWidth / 18,
                mData);
        list.add(arrow);
        mParent.addView(arrow);
    }

    @Override
    public void onErrorState() {
        for (int i = 0; i < list.size(); i++){
            ArrowSlideLine arrow = list.get(i);
            arrow.setStateError();
        }
    }

    @Override
    public void clearAllArrow() {
        for (int i = 0; i < list.size(); i++){
            mParent.removeView(list.get(i));
        }
    }
}
