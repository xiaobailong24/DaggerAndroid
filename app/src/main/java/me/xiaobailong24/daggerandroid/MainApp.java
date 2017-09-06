package me.xiaobailong24.daggerandroid;

import android.app.Activity;
import android.app.Application;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import me.xiaobailong24.daggerandroid.di.component.DaggerAppComponent;
import me.xiaobailong24.daggerandroid.di.module.AppModule;
import timber.log.Timber;

/**
 * Created by xiaobailong24 on 2017/9/6.
 * Android Application
 */

public class MainApp extends Application implements HasActivityInjector {
    @Inject
    DispatchingAndroidInjector<Activity> mDispatchingAndroidInjector;//Dagger.Android注入

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());

        DaggerAppComponent.builder()    //Dagger Inject
                .appModule(new AppModule(this))
                .build()
                .inject(this);

        registerActivityLifecycleCallbacks(new DaggerActivityLifecycleCallbacks());
    }


    @Override
    public AndroidInjector<Activity> activityInjector() {
        return this.mDispatchingAndroidInjector;
    }
}
