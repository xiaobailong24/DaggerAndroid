package me.xiaobailong24.daggerandroid;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import me.xiaobailong24.daggerandroid.databinding.ActivityMainBinding;
import me.xiaobailong24.daggerandroid.entry.Person;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    @Inject
    DispatchingAndroidInjector<Fragment> mFragmentInjector;

    private ActivityMainBinding mBinding;

    @Inject
    Person mKobe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(mBinding.toolbar);

        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kobe = String.format(Locale.CHINESE, "Activity Inject ---> Name: %s, Age: %d", mKobe.getName(), mKobe.getAge());
                Toast.makeText(MainActivity.this, kobe, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return this.mFragmentInjector;
    }
}
