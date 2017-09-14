package me.xiaobailong24.daggerandroid.di.component;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import me.xiaobailong24.daggerandroid.MainFragment;
import me.xiaobailong24.daggerandroid.di.module.MainFragmentModule;
import me.xiaobailong24.daggerandroid.di.scope.FragmentScope;

/**
 * Created by xiaobailong24 on 2017/9/14.
 * Dagger Fragment Subcomponent
 */
@FragmentScope
//@Subcomponent
@Subcomponent(modules = MainFragmentModule.class)
public interface MainFragmentSubcomponent extends AndroidInjector<MainFragment> {
    @Subcomponent.Builder
    public abstract class Builder extends AndroidInjector.Builder<MainFragment> {
    }
}
