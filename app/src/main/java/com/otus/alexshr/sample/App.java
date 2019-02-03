package com.otus.alexshr.sample;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.annotation.NonNull;

import timber.log.Timber;
import timber.log.Timber.DebugTree;

/**
 * Created by alexshr on 28.01.2019.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree() {
                @SuppressLint("DefaultLocale")
                @Override
                protected String createStackElementTag(@NonNull StackTraceElement element) {
                    return String.format("%s: %s:%d (%s)",
                            super.createStackElementTag(element),
                            element.getMethodName(),
                            element.getLineNumber(),
                            Thread.currentThread().getName());
                }
            });
        }
    }
}
