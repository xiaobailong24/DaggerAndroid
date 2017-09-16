package me.xiaobailong24.daggerandroid;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import me.xiaobailong24.daggerandroid.databinding.FragmentMainBinding;
import me.xiaobailong24.daggerandroid.entry.Person;

/**
 * Created by xiaobailong24 on 2017/9/14.
 * Dagger Fragment
 */

public class MainFragment extends Fragment {
    private FragmentMainBinding mBinding;

    @Inject
    Person mJordon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, true);
        mBinding.setJordon(mJordon);
        return mBinding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
