package me.xiaobailong24.daggerandroid.di.component;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import me.xiaobailong24.daggerandroid.MainActivity;
import me.xiaobailong24.daggerandroid.di.module.KobeModule;
import me.xiaobailong24.daggerlibrary.di.scope.ActivityScope;

/**
 * Created by xiaobailong24 on 2017/9/6.
 * Dagger.Android Subcomponent
 *
 * @see <a href="https://google.github.io/dagger/android.html">Dagger & Android</a>
 */
@ActivityScope
@Subcomponent(modules = KobeModule.class)//DataModule
public interface MainActivitySubcomponent extends AndroidInjector<MainActivity> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<MainActivity> {
    }
}