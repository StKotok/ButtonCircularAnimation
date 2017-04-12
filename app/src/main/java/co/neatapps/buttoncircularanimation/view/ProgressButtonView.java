package co.neatapps.buttoncircularanimation.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Date;
import java.util.TimerTask;

import co.neatapps.buttoncircularanimation.R;

public class ProgressButtonView extends View implements View.OnTouchListener {

    public static final float LOCK_MARGIN_COEF = 2.2f;
    public static final int ARK_THICKNESS_COEF = 15;

    public static final int ACTIVATION_DELAY = 1200;
    public static final long LONG_PRESS_DELAY = 50;

    private RectF canvasRect;
    private float arcThickness;
    private float startAngle = -90;
    private float center;
    private Paint paintMain;
    private Paint paintArc1;

    private int colorBlue;
    private int colorGray;

    private float currentProgress = 0f;

    private boolean locked = false;
    private Date pressStartTime = null;
    private ValueAnimator animatorArc1;

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public ProgressButtonView(Context context) {
        super(context);
        init(context);
    }

    public ProgressButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProgressButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.colorBlue = ContextCompat.getColor(context, R.color.blue);
        this.colorGray = ContextCompat.getColor(context, R.color.gray1);

        setOnTouchListener(this);
    }


//    C L I C K I N G

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressStartTime = new Date();
                new Handler().postDelayed(new TimerTask() {
                    @Override
                    public void run() {
                        if (isLongPressed()) {
                            setCurrentProgress(360, true);
                            new CountDownTimer(ACTIVATION_DELAY, ACTIVATION_DELAY) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    if (isLongPressed() && isLongPressCompleted()) {
                                        locked = !locked;
                                        pressStartTime = null;
                                        setCurrentProgress(0, false);
                                    }
                                }
                            };
                        }
                    }
                }, LONG_PRESS_DELAY);
                break;

            case MotionEvent.ACTION_UP:
                setCurrentProgress(0, false);
                if (!isLongPressed()) {
                    locked = !locked;
                }
//                if (!isLongPressed()) {
//                setCurrentProgress(0, false);
//                locked = !locked;
//                }
//                else {
//                    if (isLongPressCompleted()) {
//                        locked = !locked;
//                        setCurrentProgress(0, false);
//                    }
//                    pressStartTime = null;
//                }
                break;
        }

        return true;
    }

    private boolean isLongPressed() {
        boolean b = false;
        if (pressStartTime != null) {
            b = (pressStartTime.getTime() - LONG_PRESS_DELAY) < new Date().getTime();
        }
        return b;
    }

    private boolean isLongPressCompleted() {
        boolean b = false;
        if (pressStartTime != null) {
            b = (new Date().getTime() - ACTIVATION_DELAY) > pressStartTime.getTime();
        }
        return b;
    }


//    D R A W I N G

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCenterCircle(canvas);
        drawLock(canvas);
        drawText(canvas);
        drawArcs(canvas);
    }

    private void drawCenterCircle(Canvas canvas) {
        Paint paint = getPaintMain(locked ? colorGray : colorBlue);
        canvas.drawCircle(center, center, center - arcThickness - arcThickness / 2, paint);
    }

    private void drawLock(Canvas canvas) {
        int resId = locked ? R.drawable.ic_edit : R.drawable.ic_plus;
        float margin = center - arcThickness * LOCK_MARGIN_COEF;
        int lockSize = (int) (canvasRect.right - margin * 2f);
        Bitmap bitmap = Utils.getScaledResourceBitmap(resId, lockSize, getContext());
        float leftIndent = ((canvasRect.right - margin * 2f) - (float) bitmap.getWidth()) / 2f;
        float topIndent = ((canvasRect.bottom - margin * 2f) - (float) bitmap.getHeight()) / 2f;
        canvas.drawBitmap(bitmap, margin + leftIndent, margin + topIndent, null);
    }

    private void drawText(Canvas canvas) {
        if (locked) {
            // todo
        } else {
            // todo
        }
    }

    private void drawArcs(Canvas canvas) {
        if (isLongPressed() && !isLongPressCompleted()) {
            drawArc(canvas, startAngle, currentProgress, locked ? colorGray : colorBlue, 255);
        }
    }


    public void setCurrentProgress(float progress, boolean smoothProgress) {
        if (animatorArc1 != null) {
            animatorArc1.cancel();
            animatorArc1 = null;
        }
        if (smoothProgress) {
            animatorArc1 = ValueAnimator.ofFloat(this.currentProgress, progress);
            animatorArc1.setDuration(ACTIVATION_DELAY);
            animatorArc1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ProgressButtonView.this.currentProgress = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            animatorArc1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animatorArc1 = null;
                }
            });
            animatorArc1.start();
        } else {
            this.currentProgress = progress;
            postInvalidate();
        }
    }

    private void drawArc(Canvas canvas, float startAngle, float sweepDegrees, int color, int alpha) {
        if (sweepDegrees <= 0 || sweepDegrees > 360) {
            return;
        }

        Path path = new Path();
        path.reset();

        // calculating
        float localRadius = ((center) - (arcThickness / 2));
        Point startPoint = calculatePointOnArc(center, center, localRadius, startAngle);
        Point endPoint = calculatePointOnArc(center, center, localRadius, startAngle + sweepDegrees);

        // drawing the arc
        RectF innerCircle = new RectF(arcThickness, arcThickness, canvasRect.right - arcThickness, canvasRect.bottom - arcThickness);
        path.arcTo(canvasRect, startAngle, sweepDegrees);
        path.arcTo(innerCircle, startAngle + sweepDegrees, -sweepDegrees);

        // drawing the circle at both the end point of the arc to get it rounded look.
        path.addCircle(startPoint.x, startPoint.y, arcThickness / 2, Path.Direction.CW);
        path.addCircle(endPoint.x, endPoint.y, arcThickness / 2, Path.Direction.CW);

        path.close();

        canvas.drawPath(path, getPaintArc1(color, alpha));
    }

    @NonNull
    private Paint getPaintMain(int color) {
        if (paintMain == null) {
            paintMain = new Paint();
            paintMain.setStrokeWidth(0);
            paintMain.setStyle(Paint.Style.FILL);
            paintMain.setAntiAlias(true);
        }
        if (paintMain.getColor() != color) {
            paintMain.setColor(color);
        }
        return paintMain;
    }

    @NonNull
    private Paint getPaintArc1(int color, int alpha) {
        if (paintArc1 == null) {
            paintArc1 = new Paint();
            paintArc1.setStrokeWidth(0);
            paintArc1.setStyle(Paint.Style.FILL);
            paintArc1.setAntiAlias(true);
        }
        if (paintArc1.getColor() != color) {
            paintArc1.setColor(color);
        }
        paintArc1.setAlpha(alpha);
        return paintArc1;
    }

    private Point calculatePointOnArc(float circleCeX, float circleCeY, float circleRadius, float endAngle) {
        Point point = new Point();
        double endAngleRadian = endAngle * (Math.PI / 180);

        int pointX = (int) Math.round((circleCeX + circleRadius * Math.cos(endAngleRadian)));
        int pointY = (int) Math.round((circleCeY + circleRadius * Math.sin(endAngleRadian)));

        point.x = pointX;
        point.y = pointY;

        return point;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        int size = Math.min(width, height);
        if (size == 0) {
            size = Math.max(width, height);
        }

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
//        setMeasuredDimension(size, size);

        if (canvasRect == null) {
            canvasRect = new RectF(0, 0, size, size);
        } else {
            canvasRect.set(0, 0, size, size);
        }
        center = size / 2;
        arcThickness = size / ARK_THICKNESS_COEF;
    }

}
