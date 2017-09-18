package me.xiaobailong24.daggerandroid;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;
import me.xiaobailong24.daggerandroid.di.component.AppComponent;
import me.xiaobailong24.daggerandroid.di.component.DaggerAppComponent;
import me.xiaobailong24.daggerlibrary.DaggerDelegate;
import me.xiaobailong24.daggerlibrary.di.component.DaggerComponent;

/**
 * Created by xiaobailong24 on 2017/9/6.
 * Android Application
 */

public class MainApp extends Application
        implements HasActivityInjector, HasSupportFragmentInjector {
    @Inject
    DispatchingAndroidInjector<Activity> mActivityInjector;
    @Inject
    DispatchingAndroidInjector<Fragment> mFragmentInjector;

    private DaggerDelegate mDaggerDelegate;
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        //Library 的依赖注入（顶级）
        mDaggerDelegate = new DaggerDelegate(this);
        mDaggerDelegate.onCreate();

        //注入主 Module 中（该 Module 全局）
        mAppComponent = DaggerAppComponent.builder()
                .daggerComponent(getDaggerComponent())
                .build();
        mAppComponent.inject(this);

    }

    public DaggerComponent getDaggerComponent() {
        return mDaggerDelegate.getComponent();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return mActivityInjector;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return this.mFragmentInjector;
    }
}
