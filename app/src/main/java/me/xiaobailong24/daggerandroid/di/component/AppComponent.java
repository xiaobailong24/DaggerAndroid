package me.xiaobailong24.daggerandroid.di.component;

import dagger.Component;
import me.xiaobailong24.daggerandroid.MainApp;
import me.xiaobailong24.daggerandroid.di.module.AppModule;
import me.xiaobailong24.daggerandroid.di.module.MainActivityModule;
import me.xiaobailong24.daggerandroid.di.module.MainFragmentModule;
import me.xiaobailong24.daggerlibrary.di.component.DaggerComponent;
import me.xiaobailong24.daggerlibrary.di.scope.AppScope;

/**
 * Created by xiaobailong24 on 2017/9/6.
 * Dagger AppComponent
 */
@AppScope
@Component(dependencies = DaggerComponent.class,
        modules = {AppModule.class,
                MainActivityModule.class,
                MainFragmentModule.class})
public interface AppComponent {
    void inject(MainApp mainApp);
}
