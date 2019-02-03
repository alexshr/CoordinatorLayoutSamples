package com.otus.alexshr.sample;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by alexshr on 28.01.2019.
 */
@HeaderBehavior.ToolbarAnimationViews(
        toolbar = R.id.toolbar,
        pinTitle = R.id.pin_title,
        pinSubtitle = R.id.pin_subtitle,
        floatToolbar = R.id.float_toolbar,
        floatTitle = R.id.float_title,
        floatSubtitle = R.id.float_subtitle
)
public class MyHeaderBehavior extends HeaderBehavior {

    public MyHeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
