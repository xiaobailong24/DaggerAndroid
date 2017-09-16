package me.xiaobailong24.daggerandroid.di.module;

import dagger.Module;
import dagger.Provides;
import me.xiaobailong24.daggerandroid.entry.Person;
import me.xiaobailong24.daggerlibrary.di.scope.ActivityScope;

/**
 * Created by xiaobailong24 on 2017/9/6.
 * Dagger Module
 */
@Module
public class KobeModule {
    @ActivityScope
    @Provides
    public Person provideKobe() {
        return new Person("Kobe", 39);
    }
}
