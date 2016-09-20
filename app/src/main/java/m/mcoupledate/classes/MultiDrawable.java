package m.mcoupledate.classes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Created by user on 2016/7/22.
 */
public class MultiDrawable extends Drawable {

    private final List<Drawable> mDrawables;
    private final int mClusterSiteNum;

    public MultiDrawable(List<Drawable> drawables, int clusterSiteNum) {
        mDrawables = drawables;
        mClusterSiteNum = clusterSiteNum;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mDrawables.size() == 1) {
            mDrawables.get(0).draw(canvas);
            return;
        }
        int width = getBounds().width();
        int height = getBounds().height();

        canvas.save();
        canvas.clipRect(0, 0, width, height);

        if (mDrawables.size() == 2 || mDrawables.size() == 3) {
            // Paint left half
            canvas.save();
            canvas.clipRect(0, 0, width / 2, height);
            canvas.translate(-width / 4, 0);
            mDrawables.get(0).draw(canvas);
            canvas.restore();
        }
        if (mDrawables.size() == 2) {
            // Paint right half
            canvas.save();
            canvas.clipRect(width / 2, 0, width, height);
            canvas.translate(width / 4, 0);
            mDrawables.get(1).draw(canvas);
            canvas.restore();
        } else {
            // Paint top right
            canvas.save();
            canvas.scale(.5f, .5f);
            canvas.translate(width, 0);
            mDrawables.get(1).draw(canvas);

            // Paint bottom right
            canvas.translate(0, height);
            mDrawables.get(2).draw(canvas);
            canvas.restore();
        }

        if (mDrawables.size() >= 4) {
            // Paint top left
            canvas.save();
            canvas.scale(.5f, .5f);
            mDrawables.get(0).draw(canvas);

            // Paint bottom left
            canvas.translate(0, height);
            mDrawables.get(3).draw(canvas);
            canvas.restore();
        }

        Paint textPaint, circlePaint;
        textPaint = new Paint();
        circlePaint = new Paint();

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(16f);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        circlePaint.setColor(Color.parseColor("#4ddbff"));
        circlePaint.setAntiAlias(true);

        canvas.drawCircle((width*5/6)-2, (width/6)+2, (width/6), circlePaint);
        canvas.drawText(String.valueOf(mClusterSiteNum), (width*5/6)-2, (width/6)+8, textPaint);

        canvas.restore();
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
