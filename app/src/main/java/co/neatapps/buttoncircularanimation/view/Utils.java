package co.neatapps.buttoncircularanimation.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class Utils {

    public static Bitmap getScaledResourceBitmap(int resId, int size, Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        Bitmap scaledBitmap = null;
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
            } catch (OutOfMemoryError e) {
                Log.e(ProgressButtonView.class.getSimpleName(), e.getMessage(), e);
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
                Log.e(ProgressButtonView.class.getSimpleName(), e.getMessage(), e);
            }
        }
        return scaledBitmap;
    }

}
