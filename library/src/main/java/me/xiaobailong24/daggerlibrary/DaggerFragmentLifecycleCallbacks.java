package me.xiaobailong24.daggerlibrary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

/**
 * Created by xiaobailong24 on 2017/9/16.
 * Dagger.Android Inject to Fragment
 */
@Singleton
public class DaggerFragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {

    @Inject
    public DaggerFragmentLifecycleCallbacks() {
    }

    @Override
    public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
        super.onFragmentAttached(fm, f, context);
        Timber.i(f.toString() + " ---> onFragmentAttached");
        AndroidSupportInjection.inject(f);//Dagger.Android Inject for Fragment
    }

    @Override
    public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        super.onFragmentActivityCreated(fm, f, savedInstanceState);
        Timber.i(f.toString() + " ---> onFragmentActivityCreated");
    }

    @Override
    public void onFragmentDetached(FragmentManager fm, Fragment f) {
        super.onFragmentDetached(fm, f);
        Timber.i(f.toString() + " ---> onFragmentDetached");
    }

}
