package com.otus.alexshr.sample;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import timber.log.Timber;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/*
A sample to use in subclasses
@HeaderBehavior.ToolbarAnimationViews(
        toolbar = R.id.widget_toolbar,
        pinTitle = R.id.pin_title,
        pinSubtitle = R.id.pin_subtitle,
        floatToolbar = R.id.float_toolbar,
        floatTitle = R.id.float_title,
        floatSubtitle = R.id.float_subtitle
)*/

/**
 * Animate title ("floatTitle") and subtitle ("floatSubtitle") (each contains any lines numbers, any text size, any location)
 * to standard title ("pinTitle") and subtitle ("pinSubtitle") of toolbar (toolbar)
 * "floatToolbar" - is just a container which used only for "floatTitle" and "floatSubtitle" convenient location
 */
public abstract class HeaderBehavior extends CoordinatorLayout.Behavior<ViewGroup> {

    private Float ratio;

    private CoordinatorLayout parent;
    private AppBarLayout appBarLayout;
    private TextViewAnimator titleAnimator;
    private TextViewAnimator subTitleAnimator;

    private ToolbarAnimationViews ids;

    private ViewGroup floatToolbar;
    private Toolbar pinToolbar;
    private int[] offset;

    public HeaderBehavior(Context context, AttributeSet attrs) {
        ids = getClass().getAnnotation(ToolbarAnimationViews.class);
    }

    public HeaderBehavior(CoordinatorLayout parent, ViewGroup child, AppBarLayout appBarLayout) {
        ids = getClass().getAnnotation(ToolbarAnimationViews.class);
        onDependentViewChanged(parent, child, appBarLayout);
    }

    public HeaderBehavior() {
        ids = getClass().getAnnotation(ToolbarAnimationViews.class);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ViewGroup child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout coordinator, ViewGroup child, View dependency) {
        Timber.d("started");

        if (parent == null) init(coordinator, dependency);

        if (isDataAvailable()) {
            if (titleAnimator.rectExp == null) prepare();

            calcRatio();

            subTitleAnimator.refresh(ratio);
            titleAnimator.refresh(ratio);
        }
        return true;
    }

    private boolean isDataAvailable() {
        TextView floatTitle = parent.findViewById(ids.floatTitle());
        return floatTitle.getText() != null && floatTitle.getText().length() > 0;
    }

    private void calcRatio() {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) layoutParams.getBehavior();
        int offset = -behavior.getTopAndBottomOffset();
        int maxScroll = appBarLayout.getTotalScrollRange();
        ratio = (float) (maxScroll - offset) / (float) maxScroll;
    }

    private void init(CoordinatorLayout coordinator, View appBar) {
        parent = coordinator;
        appBarLayout = (AppBarLayout) appBar;
        pinToolbar = parent.findViewById(ids.toolbar());
        floatToolbar = parent.findViewById(ids.floatToolbar());

        floatToolbar.setVisibility(INVISIBLE);

        measureOffset();

        titleAnimator = new TextViewAnimator(ids.pinTitle(), ids.floatTitle());
        subTitleAnimator = new TextViewAnimator(ids.pinSubtitle(), ids.floatSubtitle());
    }

    private void prepare() {
        moveFloatToolbarDown();
        titleAnimator.prepare();
        subTitleAnimator.prepare();
    }

    private void measureOffset() {
        offset = new int[2];
        parent.getLocationOnScreen(offset);
    }

    private void moveFloatToolbarDown() {

        Rect floatToolbarRect = measure(floatToolbar);
        Rect pinToolbarRect = measure(pinToolbar);

        int appBarExpBottom = pinToolbarRect.bottom + appBarLayout.getTotalScrollRange();

        int translationY = appBarExpBottom - floatToolbarRect.bottom;
        floatToolbar.setTranslationY(translationY);
        return;
    }

    private Rect measure(View view) {

        int[] pos = new int[2];
        view.getLocationOnScreen(pos);
        int left = pos[0] - offset[0];
        int top = pos[1] - offset[1];
        Rect rect = new Rect(left, top, left + view.getWidth(), top + view.getHeight());
        Timber.d("view=%s, rect=%s", view, rect);
        return rect;
    }

    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface ToolbarAnimationViews {
        int toolbar();

        int pinTitle();

        int pinSubtitle();

        int floatToolbar();

        int floatTitle();

        int floatSubtitle();
    }

    private class TextViewAnimator {

        private AppCompatTextView tvPin, tvFloat;
        private Float ratio;
        private Rect rectPin;
        private Rect rectExp;
        private float textSizePin, textSizeExp;
        private int linesPin, linesExp;

        private TextViewAnimator(int idPin, int idFloat) {

            tvPin = parent.findViewById(idPin);
            tvFloat = parent.findViewById(idFloat);
            tvFloat.setVisibility(INVISIBLE);
            tvPin.setVisibility(INVISIBLE);
        }

        private void prepare() {
            rectPin = measure(tvPin);
            textSizePin = tvPin.getTextSize();
            linesPin = tvPin.getLineCount();

            rectExp = measure(tvFloat);
            textSizeExp = tvFloat.getTextSize();
            linesExp = tvFloat.getLineCount();

            floatToolbar.removeView(tvFloat);

            parent.addView(tvFloat);
        }

        private int calc(int start, int end) {
            return (int) (start + (end - start) * ratio);
        }

        private float calc(float start, float end) {
            return (int) (start + (end - start) * ratio);
        }

        private void refresh(float ratio) {
            this.ratio = ratio;

            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) tvFloat.getLayoutParams();
            params.width = calc(rectPin.width(), rectExp.width());
            params.leftMargin = calc(rectPin.left, rectExp.left);
            params.topMargin = calc(rectPin.top, rectExp.top);
            tvFloat.setTextSize(calc(textSizePin, textSizeExp));
            tvFloat.setMaxLines(calc(linesPin, linesExp));

            tvPin.setVisibility(ratio == 0 ? VISIBLE : INVISIBLE);
            tvFloat.setVisibility(ratio > 0 ? VISIBLE : INVISIBLE);

            Timber.d("%s", this);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TextViewAnimator{");
            sb.append("tvPin=").append(tvPin);
            sb.append(", tvFloat=").append(tvFloat);
            sb.append(", ratio=").append(ratio);
            sb.append(", rectPin=").append(rectPin);
            sb.append(", rectExp=").append(rectExp);
            sb.append(", textSizePin=").append(textSizePin);
            sb.append(", textSizeExp=").append(textSizeExp);
            sb.append(", linesPin=").append(linesPin);
            sb.append(", linesExp=").append(linesExp);
            sb.append('}');
            return sb.toString();
        }
    }
}
