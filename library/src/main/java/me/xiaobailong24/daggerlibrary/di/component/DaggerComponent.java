package me.xiaobailong24.daggerlibrary.di.component;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import me.xiaobailong24.daggerlibrary.DaggerDelegate;
import me.xiaobailong24.daggerlibrary.di.module.DaggerModule;

/**
 * Created by xiaobailong24 on 2017/9/16.
 * Dagger Library Component
 */
@Singleton
@Component(modules = {AndroidInjectionModule.class,
        DaggerModule.class})
public interface DaggerComponent {
    Application application();

    void inject(DaggerDelegate daggerDelegate);
}
