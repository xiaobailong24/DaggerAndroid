package me.xiaobailong24.daggerandroid.di.component;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import me.xiaobailong24.daggerandroid.MainFragment;
import me.xiaobailong24.daggerandroid.di.scope.FragmentScope;

/**
 * Created by xiaobailong24 on 2017/9/14.
 * Dagger Fragment Subcomponent
 * 可以不再需要{@link me.xiaobailong24.daggerandroid.di.module.MainFragmentModule}
 *
 * @see <a href="https://google.github.io/dagger/android.html">Dagger & Android</a>
 */
@FragmentScope
@Subcomponent
public interface MainFragmentSubcomponent extends AndroidInjector<MainFragment> {
    @Subcomponent.Builder
    public abstract class Builder extends AndroidInjector.Builder<MainFragment> {
    }
}
