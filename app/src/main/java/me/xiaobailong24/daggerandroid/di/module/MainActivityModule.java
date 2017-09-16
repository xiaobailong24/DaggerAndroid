package me.xiaobailong24.daggerandroid.di.module;

import android.app.Activity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;
import me.xiaobailong24.daggerandroid.MainActivity;
import me.xiaobailong24.daggerandroid.di.component.MainActivitySubcomponent;

/**
 * Created by xiaobailong24 on 2017/9/6.
 * Dagger.Android Module
 */
@Module(subcomponents = MainActivitySubcomponent.class)
public abstract class MainActivityModule {
    /**
     * 第一种注入方式。需要 Subcomponent
     * <p>
     * 第二种{@link MainFragmentModule}
     */
    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindActivityInjectorFactory(MainActivitySubcomponent.Builder builder);
}
