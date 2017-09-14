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
/*    @Binds
    @IntoMap
    @FragmentKey(MainFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment>
    bindYourFragmentInjectorFactory(MainFragmentSubcomponent.Builder builder);*/

    @FragmentScope
    @ContributesAndroidInjector(modules = JordonModule.class)
    abstract MainFragment contributeMainFragment();
}
