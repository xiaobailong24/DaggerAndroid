package me.xiaobailong24.daggerandroid.di.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import me.xiaobailong24.daggerandroid.MainActivity;
import me.xiaobailong24.daggerandroid.di.scope.ActivityScope;

/**
 * Created by xiaobailong24 on 2017/9/6.
 * Dagger.Android Module
 */
@Module
public abstract class MainActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = KobeModule.class)//Data Module
    abstract MainActivity contributeMainActivity();

}
