package co.neatapps.buttoncircularanimation.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import co.neatapps.buttoncircularanimation.R;

public class ProgressIndicator extends View {

    private Paint paintArc1 = new Paint();
    private float startAngleArc1 = 0;
    private float endAngleArc1 = 0;
    private AnimatorSet animatorSet;
    private float indeterminateSweep;
    private float indeterminateRotateOffset;
    private float startAngle;

    public ProgressIndicator(Context context) {
        super(context);
        init(context);
    }

    public ProgressIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        paintArc1.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        paintArc1.setStrokeWidth(25);
        paintArc1.setStyle(Paint.Style.STROKE);
        paintArc1.setAntiAlias(true);
        paintArc1.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        RectF rect = new RectF(20, 20, getWidth() - 20, getHeight() - 20);
        drawArc(canvas, rect, startAngleArc1, endAngleArc1, paintArc1);
    }

    private void drawArc(Canvas canvas, RectF rect, float startAngle, float endAngle, Paint paint) {
        Path path = new Path();
        path.addArc(rect, startAngle, endAngle - startAngle);
        canvas.drawPath(path, paint);
    }

    public void animateGrowing(boolean isForward) {
        if (animatorSet != null) {
            return;
        }

        animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorSet = null;
                System.out.println("ENDED");
            }

        });

        final ObjectAnimator endObjectAnimator;
        if (isForward) {
            endObjectAnimator = ObjectAnimator.ofFloat(this, "endAngleArc1", -90, 270);
        } else {
            endObjectAnimator = ObjectAnimator.ofFloat(this, "endAngleArc1", 270, -90);
        }

        animatorSet.playTogether(ObjectAnimator.ofFloat(this, "startAngleArc1", -90, -90), endObjectAnimator);
        animatorSet.setDuration(2000);
        animatorSet.start();
    }

    public void startIndeterminantAnimation() {
        if (animatorSet != null) {
            return;
        }

        int animSteps = 3;
        animatorSet = new AnimatorSet();
        AnimatorSet prevSet = null, nextSet;
        for (int k = 0; k < animSteps; k++) {
            nextSet = createIndeterminateAnimator(k);
            AnimatorSet.Builder builder = animatorSet.play(nextSet);
            if (prevSet != null) {
                builder.after(prevSet);
            }
            prevSet = nextSet;
        }

//        if (animatorSet != null) {
//            return;
//        }
//
//        animatorSet = new AnimatorSet();
//        animatorSet.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                System.out.println("ENDED");
//            }
//
//        });
//
//        final ObjectAnimator endObjectAnimator;
//            endObjectAnimator = ObjectAnimator.ofFloat(this, "endAngleArc1", 0, 360);
//
//        animatorSet.playTogether(ObjectAnimator.ofFloat(this, "startAngleArc1", 15, 360 + 15), endObjectAnimator);
//        animatorSet.setDuration(2000);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animatorSet != null) {
                    animatorSet = null;

                    startIndeterminantAnimation();
                }
            }

        });
        animatorSet.start();
    }

    public void cancelAnimation() {
        if (animatorSet != null) {
            AnimatorSet a = this.animatorSet;

            animatorSet = null;
            a.cancel();
            startAngleArc1 = -90;
            endAngleArc1 = -90;


            invalidate();
        }
    }

    private AnimatorSet createIndeterminateAnimator(float step) {
        int animSteps = 3;
        float INDETERMINANT_MIN_SWEEP = 15f;
        indeterminateSweep = 15f;
        int animDuration = 4000;

        final float maxSweep = 360f * (animSteps - 1) / animSteps + INDETERMINANT_MIN_SWEEP;
        final float start = -90f + step * (maxSweep - INDETERMINANT_MIN_SWEEP);
        setAngles(startAngle + indeterminateRotateOffset, indeterminateSweep);
        // Extending the front of the arc
        ValueAnimator frontEndExtend = ValueAnimator.ofFloat(INDETERMINANT_MIN_SWEEP, maxSweep);
        frontEndExtend.setDuration(animDuration / animSteps / 2);
        frontEndExtend.setInterpolator(new DecelerateInterpolator(1));
        frontEndExtend.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                indeterminateSweep = (Float) animation.getAnimatedValue();
                setAngles(startAngle + indeterminateRotateOffset, indeterminateSweep);
            }
        });

        // Overall rotation
        ValueAnimator rotateAnimator1 = ValueAnimator.ofFloat(step * 720f / animSteps, (step + .5f) * 720f / animSteps);
        rotateAnimator1.setDuration(animDuration / animSteps / 2);
        rotateAnimator1.setInterpolator(new LinearInterpolator());
        rotateAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                indeterminateRotateOffset = (Float) animation.getAnimatedValue();
                setAngles(startAngle + indeterminateRotateOffset, indeterminateSweep);
                invalidate();
            }
        });

        // Followed by...

        // Retracting the back end of the arc
        ValueAnimator backEndRetract = ValueAnimator.ofFloat(start, start + maxSweep - INDETERMINANT_MIN_SWEEP);
        backEndRetract.setDuration(animDuration / animSteps / 2);
        backEndRetract.setInterpolator(new DecelerateInterpolator(1));
        backEndRetract.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startAngle = (Float) animation.getAnimatedValue();
                indeterminateSweep = maxSweep - startAngle + start;
                setAngles(startAngle + indeterminateRotateOffset, indeterminateSweep);
                invalidate();
            }
        });

        // More overall rotation
        ValueAnimator rotateAnimator2 = ValueAnimator.ofFloat((step + .5f) * 720f / animSteps, (step + 1) * 720f / animSteps);
        rotateAnimator2.setDuration(animDuration / animSteps / 2);
        rotateAnimator2.setInterpolator(new LinearInterpolator());
        rotateAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                indeterminateRotateOffset = (Float) animation.getAnimatedValue();
                setAngles(startAngle + indeterminateRotateOffset, indeterminateSweep);
                invalidate();
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(frontEndExtend).with(rotateAnimator1);
        set.play(backEndRetract).with(rotateAnimator2).after(rotateAnimator1);
        return set;
    }

    private void setAngles(float start, float sweep) {
        startAngleArc1 = start;
        endAngleArc1 = start + sweep;
    }

    public float getStartAngleArc1() {
        return startAngleArc1;
    }

    public void setStartAngleArc1(float startAngleArc1) {
        this.startAngleArc1 = startAngleArc1;
        invalidate();
    }

    public float getEndAngleArc1() {
        return endAngleArc1;
    }

    public void setEndAngleArc1(float endAngleArc1) {
        this.endAngleArc1 = endAngleArc1;
        invalidate();
    }
}
