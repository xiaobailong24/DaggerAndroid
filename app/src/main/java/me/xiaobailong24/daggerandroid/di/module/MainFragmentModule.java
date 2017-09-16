package me.xiaobailong24.daggerandroid.di.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import me.xiaobailong24.daggerandroid.MainFragment;
import me.xiaobailong24.daggerandroid.di.scope.FragmentScope;

/**
 * Created by xiaobailong24 on 2017/9/14.
 * Dagger Fragment Module
 */
@Module
public abstract class MainFragmentModule {
    /**
     * 第二种注入方式。当 Subcomponent 和 它的 Builder 没有其它方法或超类型时：
     * {@link me.xiaobailong24.daggerandroid.di.component.MainActivitySubcomponent}
     * 可以不再需要 Subcomponent
     * <p>
     * 第一种{@link MainActivityModule}
     */
    @FragmentScope
    @ContributesAndroidInjector(modules = JordonModule.class)//DataModule
    abstract MainFragment contributeMainFragment();
}
