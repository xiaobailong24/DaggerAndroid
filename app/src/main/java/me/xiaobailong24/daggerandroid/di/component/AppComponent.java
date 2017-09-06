package me.xiaobailong24.daggerandroid.di.component;

import javax.inject.Singleton;

import dagger.Component;
import me.xiaobailong24.daggerandroid.MainApp;
import me.xiaobailong24.daggerandroid.di.module.AppModule;
import me.xiaobailong24.daggerandroid.di.module.MainActivityModule;

/**
 * Created by xiaobailong24 on 2017/9/6.
 * Dagger AppComponent
 */
@Singleton
@Component(modules = {AppModule.class,
        MainActivityModule.class})
public interface AppComponent {
    void inject(MainApp mainApp);
}
