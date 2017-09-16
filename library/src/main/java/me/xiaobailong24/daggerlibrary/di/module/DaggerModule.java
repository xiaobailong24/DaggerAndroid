package me.xiaobailong24.daggerlibrary.di.module;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by xiaobailong24 on 2017/9/16.
 * Dagger Library Module
 */
@Module
public class DaggerModule {
    private final Application mApplication;

    public DaggerModule(Application application) {
        mApplication = application;
    }

    @Singleton
    @Provides
    public Application provideApplication() {
        return this.mApplication;
    }

}
