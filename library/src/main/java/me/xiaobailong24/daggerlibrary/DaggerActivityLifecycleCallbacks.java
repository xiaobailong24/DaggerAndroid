package me.xiaobailong24.daggerlibrary;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

/**
 * Created by xiaobailong24 on 2017/9/6.
 * Dagger.Android Inject to Activity
 */
public class DaggerActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    @Inject
    DaggerFragmentLifecycleCallbacks mFragmentLifecycleCallbacks;

    @Inject
    public DaggerActivityLifecycleCallbacks() {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Timber.w(activity + " ---> onActivityCreated");
        AndroidInjection.inject(activity);//Dagger.Android Inject for Activity
        //如果该 Activity 的 Fragment 需要 Dagger 注入，
        //即实现了 HasSupportFragmentInjector，就会注册上一步的 DaggerFragmentLifecycleCallbacks 来实现 Dagger 注入。
        if (activity instanceof HasSupportFragmentInjector && activity instanceof FragmentActivity) {
            ((FragmentActivity) activity).getSupportFragmentManager()
                    .registerFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks, true);
        }
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
