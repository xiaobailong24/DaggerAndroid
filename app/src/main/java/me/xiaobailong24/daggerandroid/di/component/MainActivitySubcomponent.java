package me.xiaobailong24.daggerandroid.di.component;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import me.xiaobailong24.daggerandroid.MainActivity;
import me.xiaobailong24.daggerandroid.di.module.MainActivityModule;
import me.xiaobailong24.daggerandroid.di.scope.ActivityScope;

/**
 * Created by xiaobailong24 on 2017/9/6.
 * Dagger.Android Subcomponent
 */
@ActivityScope
@Subcomponent(modules = MainActivityModule.class)
public interface MainActivitySubcomponent extends AndroidInjector<MainActivity> {

    @Subcomponent.Builder
    public abstract class Builder extends AndroidInjector.Builder<MainActivity> {
    }
}