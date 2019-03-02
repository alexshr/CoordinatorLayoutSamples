package com.otus.alexshr.sample;

/**
 * Created by alexshr on 18.12.2018.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import timber.log.Timber;

public class SquareImageView extends AppCompatImageView {

    private float hwRatio = 1f;

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray array = context.getTheme()
                    .obtainStyledAttributes(attrs, R.styleable.SquareImageView, 0, 0);
            hwRatio = array.getFloat(R.styleable.SquareImageView_hwRatio, 1f);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if ((width != 0 && height == 0) || (width == 0 && height != 0)) {

            if (height == 0) height = (int) (width * hwRatio);
            else width = (int) (height / hwRatio);

            Timber.d("hwRatio=%s; width: %d -> %d, height: %d -> %d", hwRatio, getMeasuredWidth(), width, getMeasuredHeight(), height);

            setMeasuredDimension(width, height);
        } else {
            Timber.i("not resized: resizable size should be 0dp (layout_width or layout_height)");
        }
    }

    public float getHwRatio() {
        return hwRatio;
    }

    public void setHwRatio(float hwRatio) {
        this.hwRatio = hwRatio;
        invalidate();
    }
}