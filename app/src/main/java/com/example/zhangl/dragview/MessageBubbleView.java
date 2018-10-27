package com.example.zhangl.dragview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class MessageBubbleView extends View {


    // 两个圆
    private PointF mFixationPoint, mDragPoint;

    //拖拽圆半径
    private int mDragRadius = 10;

    private int mMaxFixationRadius = 7;
    private int mFixactionRadius;
    private int mMinFixationRadius = 2;

    private Paint mPain;


    public MessageBubbleView(Context context) {
        this(context, null);
    }

    public MessageBubbleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragRadius = dip2px(10);
        mMaxFixationRadius = dip2px(mMaxFixationRadius);
        mMinFixationRadius = dip2px(mMinFixationRadius);
        mPain = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPain.setDither(true);
        mPain.setColor(Color.RED);

    }

    private int dip2px(int dip) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dip,getResources().getDisplayMetrics());
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (mDragPoint == null || mFixationPoint == null){
            return;
        }

        // 画拖拽圆
        canvas.drawCircle(mDragPoint.x,mDragPoint.y,mDragRadius,mPain);


        Path bezeierPath = getBezeierPath();



        if (bezeierPath != null){
            canvas.drawCircle(mFixationPoint.x,mFixationPoint.y,mFixactionRadius,mPain);

            //画贝塞尔曲线
            canvas.drawPath(bezeierPath,mPain);
        }



    }

    private Path getBezeierPath() {
        float distance = getDistance(mDragPoint,mFixationPoint);
        mFixactionRadius = (int) (mMaxFixationRadius - distance/14);
        if (mFixactionRadius < mMinFixationRadius){
            //超过一定距离   贝塞尔和固定圆都不用画了
            return null;
        }

        Path path = new Path();
        //求∠A
        //求斜率

        float tanA = (mDragPoint.y - mFixationPoint.y)/(mDragPoint.x - mFixationPoint.x);

        double arcTanA = Math.atan(tanA);

        //p0
        float p0x = (float) (mFixationPoint.x + mFixactionRadius*Math.sin(arcTanA));
        float p0y = (float) (mFixationPoint.y - mFixactionRadius*Math.cos(arcTanA));

        //p1
        float p1x = (float) (mDragPoint.x + mDragRadius*Math.sin(arcTanA));
        float p1y = (float) (mDragPoint.y - mDragRadius*Math.cos(arcTanA));

        //p2
        float p2x = (float) (mDragPoint.x - mDragRadius*Math.sin(arcTanA));
        float p2y = (float) (mDragPoint.y + mDragRadius*Math.cos(arcTanA));

        //p4
        float p3x = (float) (mFixationPoint.x - mFixactionRadius*Math.sin(arcTanA));
        float p3y = (float) (mFixationPoint.y + mFixactionRadius*Math.cos(arcTanA));



        path.moveTo(p0x,p0y);
        PointF controlPoint = getControlPoint();
        path.quadTo(controlPoint.x,controlPoint.y,p1x,p1y);

        path.lineTo(p2x,p2y);
        path.quadTo(controlPoint.x,controlPoint.y,p3x,p3y);
        path.close();

        return path;
    }

    private PointF getControlPoint() {

        return new PointF((mDragPoint.x+mFixationPoint.x)/2,
                (mDragPoint.y+mFixationPoint.y)/2);

    }

    private float getDistance(PointF point1, PointF point2) {

        return (float) Math.sqrt((point1.x - point2.x)*(point1.x - point2.x) +
                (point1.y - point2.y)*(point1.y - point2.y));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float downX = event.getX();
                float downY = event.getY();
                initPoint(downX,downY);
                break;

            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                upDateDragPoint(moveX,moveY);
                break;


            case MotionEvent.ACTION_UP:


                break;
        }

        invalidate();
        return true;
    }

    private void upDateDragPoint(float moveX, float moveY) {
        mDragPoint.x = moveX;
        mDragPoint.y = moveY;
    }


    private void initPoint(float downX,float downY){
        mFixationPoint = new PointF(downX,downY);
        mDragPoint = new PointF(downX,downY);
    }


}
