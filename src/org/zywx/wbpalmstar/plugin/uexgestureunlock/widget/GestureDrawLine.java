package org.zywx.wbpalmstar.plugin.uexgestureunlock.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import org.zywx.wbpalmstar.plugin.uexgestureunlock.util.GestureUtil;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.JsConst;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.entity.GesturePoint;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.util.OnDrawArrowListener;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.ConfigGestureVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 手势密码路径绘制
 *
 */
public class GestureDrawLine extends View {
    private int mov_x;// 声明起点坐标
    private int mov_y;
    private Paint paint;// 声明画笔
    private Path linePath;
    private List<GesturePoint> list;// 装有各个view坐标的集合
    private List<Pair<GesturePoint, GesturePoint>> lineList;// 记录画过的线
    private Map<String, GesturePoint> autoCheckPointMap;// 自动选中的情况点
    private boolean isDrawEnable = true; // 是否允许绘制

    /**
     * 屏幕的宽度和高度
     */
    private int[] screenDisplay;

    /**
     * 手指当前在哪个Point内
     */
    private GesturePoint currentPoint;
    /**
     * 用户绘图的回调
     */
    private GestureCallBack callBack;

    /**
     * 用户当前绘制的图形密码
     */
    private StringBuilder passWordSb;

    /**
     * 是否为校验
     */
    private boolean isVerify;

    /**
     * 用户传入的passWord
     */
    private String passWord;

    private OnDrawArrowListener mOnDrawArrowListener;

    private int blockWidth;

    private int selectedColor;

    private int errorColor;

    public GestureDrawLine(Context context, List<GesturePoint> list, boolean isVerify,
                           String passWord, GestureCallBack callBack,
                           OnDrawArrowListener listener,
                           int blockWidth,
                           ConfigGestureVO data) {
        super(context);
        this.blockWidth = blockWidth;
        screenDisplay = GestureUtil.getScreenDisplay(context);
        paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
        linePath = new Path();// 使用Path代替之前的canvas记录连线
        paint.setStyle(Style.STROKE);// 设置非填充
        paint.setStrokeWidth(5);// 笔宽5像素
        paint.setColor(selectedColor);// 设置默认连线颜色
        paint.setAntiAlias(true);// 不显示锯齿

        this.list = list;
        this.lineList = new ArrayList<Pair<GesturePoint, GesturePoint>>();

        initAutoCheckPointMap();
        this.callBack = callBack;

        // 初始化密码缓存
        this.isVerify = isVerify;
        this.passWordSb = new StringBuilder();
        this.passWord = passWord;
        this.mOnDrawArrowListener = listener;

        this.selectedColor = data.getSelectedThemeColor();
        this.errorColor = data.getErrorThemeColor();
    }

    public void setBlockWidth(int blockWidth){
        this.blockWidth = blockWidth;
    }

    private void initAutoCheckPointMap() {
        autoCheckPointMap = new HashMap<String,GesturePoint>();
        autoCheckPointMap.put("1,3", getGesturePointByNum(2));
        autoCheckPointMap.put("1,7", getGesturePointByNum(4));
        autoCheckPointMap.put("1,9", getGesturePointByNum(5));
        autoCheckPointMap.put("2,8", getGesturePointByNum(5));
        autoCheckPointMap.put("3,7", getGesturePointByNum(5));
        autoCheckPointMap.put("3,9", getGesturePointByNum(6));
        autoCheckPointMap.put("4,6", getGesturePointByNum(5));
        autoCheckPointMap.put("7,9", getGesturePointByNum(8));
    }

    private GesturePoint getGesturePointByNum(int num) {
        for (GesturePoint point : list) {
            if (point.getNum() == num) {
                return point;
            }
        }
        return null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        screenDisplay = GestureUtil.getScreenDisplay(getContext());
        int width = this.getLayoutParams().width;
        int screenWidth = screenDisplay[0];
        if (width != screenDisplay[0]) {
            setMeasuredDimension(screenWidth, screenWidth);
        }
    }

    // 画位图
    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        canvas.drawPath(linePath, paint);
    }

    // 触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isDrawEnable) {
            // 当期不允许绘制
            return true;
        }
        paint.setColor(selectedColor);// 设置默认连线颜色
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mov_x = (int) event.getX();
                mov_y = (int) event.getY();
                // 判断当前点击的位置是处于哪个点之内
                currentPoint = getPointAt(mov_x, mov_y);
                if (currentPoint != null) {
                    currentPoint.setPointState(JsConst.POINT_STATE_SELECTED);
                    passWordSb.append(currentPoint.getNum());
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                clearScreenAndDrawList();

                // 得到当前移动位置是处于哪个点内
                GesturePoint pointAt = getPointAt((int) event.getX(), (int) event.getY());
                // 代表当前用户手指处于点与点之前
                if (currentPoint == null && pointAt == null) {
                    return true;
                } else {// 代表用户的手指移动到了点上
                    if (currentPoint == null) {// 先判断当前的point是不是为null
                        // 如果为空，那么把手指移动到的点赋值给currentPoint
                        currentPoint = pointAt;
                        // 把currentPoint这个点设置选中为true;
                        currentPoint.setPointState(JsConst.POINT_STATE_SELECTED);
                        passWordSb.append(currentPoint.getNum());
                    }
                }
                if (pointAt == null || currentPoint.equals(pointAt) ||
                        JsConst.POINT_STATE_SELECTED == pointAt.getPointState()) {
                    // 点击移动区域不在圆的区域，或者当前点击的点与当前移动到的点的位置相同，或者当前点击的点处于选中状态
                    // 那么以当前的点中心为起点，以手指移动位置为终点画线
                    linePath.moveTo(currentPoint.getCenterX(), currentPoint.getCenterY());
                    linePath.lineTo(event.getX(), event.getY());// 画线
                } else {
                    // 如果当前点击的点与当前移动到的点的位置不同
                    // 那么以前前点的中心为起点，以手移动到的点的位置画线
                    linePath.moveTo(currentPoint.getCenterX(), currentPoint.getCenterY());
                    linePath.lineTo(pointAt.getCenterX(), pointAt.getCenterY());
                    pointAt.setPointState(JsConst.POINT_STATE_SELECTED);

                    // 判断是否中间点需要选中
                    GesturePoint betweenPoint = getBetweenCheckPoint(currentPoint, pointAt);
                    if (betweenPoint != null &&
                            JsConst.POINT_STATE_SELECTED != betweenPoint.getPointState()) {
                        // 存在中间点并且没有被选中
                        Pair<GesturePoint, GesturePoint> pair1 =
                                new Pair<GesturePoint, GesturePoint>(currentPoint, betweenPoint);
                        if (mOnDrawArrowListener != null){
                            mOnDrawArrowListener.onDrawArrow(currentPoint, betweenPoint, blockWidth);
                        }
                        lineList.add(pair1);
                        passWordSb.append(betweenPoint.getNum());
                        Pair<GesturePoint, GesturePoint> pair2 =
                                new Pair<GesturePoint, GesturePoint>(betweenPoint, pointAt);
                        if (mOnDrawArrowListener != null){
                            mOnDrawArrowListener.onDrawArrow(betweenPoint, pointAt, blockWidth);
                        }
                        lineList.add(pair2);
                        passWordSb.append(pointAt.getNum());
                        // 设置中间点选中
                        betweenPoint.setPointState(JsConst.POINT_STATE_SELECTED);
                        // 赋值当前的point;
                        currentPoint = pointAt;
                    } else {
                        Pair<GesturePoint, GesturePoint> pair =
                                new Pair<GesturePoint, GesturePoint>(currentPoint, pointAt);
                        if (mOnDrawArrowListener != null){
                            mOnDrawArrowListener.onDrawArrow(currentPoint, pointAt, blockWidth);
                        }
                        lineList.add(pair);
                        passWordSb.append(pointAt.getNum());
                        // 赋值当前的point;
                        currentPoint = pointAt;
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:// 当手指抬起的时候
                if (isVerify) {
                    // 手势密码校验
                    // 清掉屏幕上所有的线，只画上集合里面保存的线
                    if (passWord.equals(passWordSb.toString())) {
                        // 代表用户绘制的密码手势与传入的密码相同
                        callBack.checkedSuccess();
                    } else {
                        // 用户绘制的密码与传入的密码不同。
                        callBack.checkedFail();
                    }
                } else {
                    callBack.onGestureCodeInput(passWordSb.toString());
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 指定时间去清除绘制的状态
     * @param delayTime 延迟执行时间
     * @param flag 是否绘制红色路线
     */
    public void clearDrawlineState(long delayTime, boolean flag) {
        if (flag) {
            // 绘制红色提示路线
            isDrawEnable = false;
            drawErrorPathTip();
        }
        new Handler().postDelayed(new clearStateRunnable(), delayTime);
    }

    /**
     * 清除绘制状态的线程
     */
    final class clearStateRunnable implements Runnable {
        public void run() {
            // 重置passWordSb
            passWordSb = new StringBuilder();
            // 清空保存点的集合
            lineList.clear();
            // 重新绘制界面
            clearScreenAndDrawList();
            for (GesturePoint p : list) {
                p.setPointState(JsConst.POINT_STATE_NORMAL);
            }
            invalidate();
            isDrawEnable = true;
            if (mOnDrawArrowListener != null){
                mOnDrawArrowListener.clearAllArrow();
            }
        }
    }

    /**
     * 通过点的位置去集合里面查找这个点是包含在哪个Point里面的
     *
     * @param x
     * @param y
     * @return 如果没有找到，则返回null，代表用户当前移动的地方属于点与点之间
     */
    private GesturePoint getPointAt(int x, int y) {

        for (GesturePoint point : list) {
            // 先判断x
            int leftX = point.getLeftX();
            int rightX = point.getRightX();
            if (!(x >= leftX && x < rightX)) {
                // 如果为假，则跳到下一个对比
                continue;
            }

            int topY = point.getTopY();
            int bottomY = point.getBottomY();
            if (!(y >= topY && y < bottomY)) {
                // 如果为假，则跳到下一个对比
                continue;
            }

            // 如果执行到这，那么说明当前点击的点的位置在遍历到点的位置这个地方
            return point;
        }

        return null;
    }

    private GesturePoint getBetweenCheckPoint(GesturePoint pointStart, GesturePoint pointEnd) {
        int startNum = pointStart.getNum();
        int endNum = pointEnd.getNum();
        String key;
        if (startNum < endNum) {
            key = startNum + "," + endNum;
        } else {
            key = endNum + "," + startNum;
        }
        return autoCheckPointMap.get(key);
    }

    /**
     * 清掉屏幕上所有的线，然后画出集合里面的线
     */
    private void clearScreenAndDrawList() {
        linePath.reset();
        if (lineList.size() > 0) {
            Pair<GesturePoint, GesturePoint> pair = lineList.get(0);
            linePath.moveTo(pair.first.getCenterX(), pair.first.getCenterY());
        }
        for (Pair<GesturePoint, GesturePoint> pair : lineList) {
            linePath.lineTo(pair.second.getCenterX(), pair.second.getCenterY());
        }
    }

    /**
     * 校验错误/两次绘制不一致提示
     */
    private void drawErrorPathTip() {
        linePath.reset();
        if (mOnDrawArrowListener != null){
            mOnDrawArrowListener.onErrorState();
        }
        paint.setColor(errorColor);// 设置错误线路颜色
        if (lineList.size() > 0) {
            Pair<GesturePoint, GesturePoint> pair = lineList.get(0);
            linePath.moveTo(pair.first.getCenterX(), pair.first.getCenterY());
        }
        for (Pair<GesturePoint, GesturePoint> pair : lineList) {
            pair.first.setPointState(JsConst.POINT_STATE_WRONG);
            pair.second.setPointState(JsConst.POINT_STATE_WRONG);
            linePath.lineTo(pair.second.getCenterX(), pair.second.getCenterY());
        }
        invalidate();
    }


    public interface GestureCallBack {

        /**
         * 用户设置/输入了手势密码
         */
        public abstract void onGestureCodeInput(String inputCode);

        /**
         * 代表用户绘制的密码与传入的密码相同
         */
        public abstract void checkedSuccess();

        /**
         * 代表用户绘制的密码与传入的密码不相同
         */
        public abstract void checkedFail();
    }

}
