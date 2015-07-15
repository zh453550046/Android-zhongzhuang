package com.example.zhouwn.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by zhouwn on 2015/7/13.
 */
public class ProgressScrollView extends ViewGroup {

    private Bitmap image;
    private float imageWidth, imageHeight, startAngle = 90f, sweepAngle, deltSweepAngle, newAngle, lastX, lastY, willAngle;
    private Paint paint;
    private int color = android.graphics.Color.RED;
    private View view;
    public static final float STROKEWIDTH = 10f;

    public ProgressScrollView(Context context) {
        super(context);
        init();
    }

    public ProgressScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKEWIDTH);
        // 设置光源的方向
        float[] direction = new float[]{1, 1, 1};
        //设置环境光亮度
        float light = 0.4f;
        // 选择要应用的反射等级
        float specular = 6;
        // 向mask应用一定级别的模糊
        float blur = 3.5f;
        paint.setMaskFilter(new EmbossMaskFilter(direction, light, specular, blur));
        setWillNotDraw(false);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() != 0 && view != null) {
            view.layout((int) ((2 * STROKEWIDTH + 10) / 2), (int) ((2 * STROKEWIDTH + 10) / 2), (int) (imageWidth + (2 * STROKEWIDTH + 10) / 2), (int) (imageHeight + (2 * STROKEWIDTH + 10) / 2));
            view.setRotation(3f);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec), heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec), heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0, height = 0;

        if (widthMode != MeasureSpec.EXACTLY) {
            if (imageWidth != 0) {
                width = (int) (imageWidth + 2 * STROKEWIDTH + 10);
            }
        } else {
            width = widthSize;
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            if (imageHeight != 0) {
                height = (int) (imageHeight + 2 * STROKEWIDTH + 10);
            }
        } else {
            height = heightSize;
        }
        setMeasuredDimension(width, height);
        for (int i = 0; i < getChildCount(); i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(color);
        RectF rectF = new RectF();
        rectF.left = 10;
        rectF.top = 10;
        rectF.right = imageWidth + 2 * STROKEWIDTH;
        rectF.bottom = imageHeight + 2 * STROKEWIDTH;
        //画圆弧
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);
    }

    public void setResource(int resource) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.progressview_item, null);
        ImageView iv = (ImageView) view.findViewById(R.id.iv);
        iv.setImageResource(resource);
        image = BitmapFactory.decodeResource(getResources(), resource);
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();
        addView(view);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x, y;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                x = ev.getX();
                y = ev.getY();
                float angle1 = getAngle(lastX, lastY), angle2 = getAngle(x, y);
                switch (getQuadrant(x, y)) {
                    case 1:
                    case 4:
                        newAngle += angle2 - angle1;
                        break;
                    case 2:
                    case 3:
                        newAngle += -angle2 + angle1;
                        break;
                }
                if (newAngle < 0) {
                    newAngle = 360 + newAngle;
                }
                if (!(sweepAngle == 360 && newAngle > 360 && newAngle < 405)) {
                    newAngle = newAngle % 360;
                }
                view.setRotation(newAngle);
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                startProgressChange();
                break;
        }
        return true;
    }

    private int getQuadrant(float touchX, float touchY) {
        float deltaX = touchX - getMeasuredWidth() / 2;
        float deltaY = touchY - getMeasuredHeight() / 2;
        if (deltaX > 0) {
            return deltaY > 0 ? 4 : 1;
        } else {
            return deltaY > 0 ? 3 : 2;
        }
    }

    private float getAngle(float touchX, float touchY) {
        float deltaX = touchX - getMeasuredWidth() / 2;
        float deltaY = touchY - getMeasuredHeight() / 2;
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return (float) (Math.asin(deltaY / distance) * 180 / Math.PI);
    }


    private void startProgressChange() {
        int n = (int) newAngle / 90;
        int m = (int) newAngle % 90;
        if (m > 45) {
            if (n == 4) {
                newAngle = newAngle / 360;
                n = 1;
            } else {
                n = n + 1;
            }
        }
        willAngle = n * 90;
        switch (n) {
            case 1:
                color = Color.RED;
                break;
            case 2:
                color = Color.DKGRAY;
                break;
            case 3:
                color = Color.WHITE;
                break;
            case 4:
                color = Color.MAGENTA;
                break;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", newAngle, willAngle + 2);
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                newAngle = willAngle;
            }
        });
        animator.start();
        deltSweepAngle = willAngle - sweepAngle;
        ValueAnimator animator1 = ValueAnimator.ofFloat(sweepAngle, willAngle);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float currentAngle = (Float) animation.getAnimatedValue();
                sweepAngle = currentAngle;
                invalidate();
            }
        });
        animator1.start();
    }

}
