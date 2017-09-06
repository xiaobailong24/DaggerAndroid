package me.xiaobailong24.daggerandroid;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import dagger.android.AndroidInjection;
import timber.log.Timber;

/**
 * Created by xiaobailong24 on 2017/9/6.
 * Dagger.Android Inject
 */

public class DaggerActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Timber.w(activity + " ---> onActivityCreated");
        AndroidInjection.inject(activity);//Dagger.Android Inject for Activity
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Timber.w(activity + " ---> onActivityDestroyed");
    }
}
