package co.neatapps.buttoncircularanimation.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import java.util.Date;

import co.neatapps.buttoncircularanimation.R;

public class ProgressButtonView extends View implements View.OnTouchListener {

    public static final float LOCK_MARGIN_COEF = 2.2f;
    public static final int ARK_THICKNESS_COEF = 15;

    private RectF canvasRect;
    private float arcThickness;
    private float startAngle = -90;
    private float endAngle = startAngle + 90;
    private float activationDelay = 1.2f;
    private float spacing = 2;
    private float center;
    private Paint paintMain;
    private Paint paintArc1;
    private Paint paintArc2;
    private Paint paintArc3;

    private int colorBlue;
    private int colorGray;


    private boolean locked = false;
    private boolean longPressCompleted = false;
    private Date pressStartTime;
    private boolean allowsTap = true;
    private boolean allowsLongPress = true;


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
                // todo
                break;

            case MotionEvent.ACTION_UP:
                // todo
                break;
        }

        return true;
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
        Bitmap bitmap = getScaledResourceBitmap(resId, getContext(), lockSize);
        float leftIndent = ((canvasRect.right - margin * 2f) - (float) bitmap.getWidth()) / 2f;
        float topIndent = ((canvasRect.bottom - margin * 2f) - (float) bitmap.getHeight()) / 2f;
        canvas.drawBitmap(bitmap, margin + leftIndent, margin + topIndent, null);
    }

    public Bitmap getScaledResourceBitmap(int resId, Context context, int size) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        Bitmap scaledBitmap = null;
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
            } catch (OutOfMemoryError e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
            }
        } else {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();

            int width;
            int height;

            if (intrinsicWidth != intrinsicHeight) {
                if (intrinsicWidth > intrinsicHeight) {
                    float coef = size / intrinsicWidth;
                    width = size;
                    height = (int) (intrinsicHeight * coef);
                } else {
                    double coef = (double) size / (double) intrinsicHeight;
                    height = size;
                    width = (int) ((double) intrinsicWidth * coef);
                }
            } else {
                width = size;
                height = size;
            }

            try {
                scaledBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(scaledBitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            } catch (OutOfMemoryError e) {
                Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
        return scaledBitmap;
    }

    private void drawText(Canvas canvas) {
        if (locked) {
            // todo
        } else {
            // todo
        }
    }

    private void drawArcs(Canvas canvas) {
        // todo
        ScaleAnimation showScaleAnimation = new ScaleAnimation(0.2f, 1.4f, 0.2f, 1.4f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f);
        showScaleAnimation.setDuration(1000);
        AnimationSet showAnimationSet = new AnimationSet(false);
        showAnimationSet.addAnimation(showScaleAnimation);

//        drawArc(canvas, 230, 100, colorBlue, 100);
//        drawArc(canvas, 0, 80, colorBlue, 160);
        drawArc(canvas, -90, 100, colorBlue, 255);
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
