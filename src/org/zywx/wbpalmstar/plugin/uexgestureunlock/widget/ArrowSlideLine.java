package org.zywx.wbpalmstar.plugin.uexgestureunlock.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import org.zywx.wbpalmstar.plugin.uexgestureunlock.entity.ArrowPoint;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.ConfigGestureVO;

public class ArrowSlideLine extends View{

    private Paint paint;
    private ArrowPoint startPoint, centerPoint;
    Path triangle;
    private float angle;
    private float triangleSide;
    private int normalColor;
    private int errorColor;
    private int currentColor;

    public ArrowSlideLine(Context context) {
        super(context);
    }

    public ArrowSlideLine(Context context, ArrowPoint startPoint,
                          ArrowPoint centerPoint,
                          float angle, float triangleSide,
                          ConfigGestureVO data) {
        super(context);
        this.startPoint = startPoint;
        this.centerPoint = centerPoint;
        this.angle = angle;
        this.triangleSide = triangleSide;
        paint = new Paint();
        triangle = new Path();
        this.normalColor = data.getSelectedThemeColor();
        this.errorColor = data.getErrorThemeColor();
        this.currentColor = this.normalColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(currentColor);
        triangle.moveTo(startPoint.getX(), startPoint.getY());
        triangle.lineTo(startPoint.getX() - triangleSide, startPoint.getY() + triangleSide);
        triangle.lineTo(startPoint.getX() + triangleSide, startPoint.getY() + triangleSide);
        triangle.close();
        canvas.rotate(angle, centerPoint.getX(), centerPoint.getY());
        canvas.drawPath(triangle, paint);
    }

    public void setStateError() {
        this.currentColor = this.errorColor;
        invalidate();
    }
}
