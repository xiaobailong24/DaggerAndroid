package me.xiaobailong24.daggerandroid.di.module;

import dagger.Module;
import dagger.Provides;
import me.xiaobailong24.daggerandroid.entry.Person;
import me.xiaobailong24.daggerlibrary.di.scope.FragmentScope;

/**
 * Created by xiaobailong24 on 2017/9/6.
 * Dagger Module
 */
@Module
public class JordonModule {
    @FragmentScope
    @Provides
    public Person provideJordon() {
        return new Person("Jordon", 48);
    }

}
