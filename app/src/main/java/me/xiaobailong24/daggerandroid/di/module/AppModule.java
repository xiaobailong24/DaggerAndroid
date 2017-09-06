package me.xiaobailong24.daggerandroid.di.module;

import android.app.Application;

import dagger.Module;

/**
 * Created by xiaobailong24 on 2017/9/6.
 * Dagger AppModule
 */
@Module
public class AppModule {
    private Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }
}
