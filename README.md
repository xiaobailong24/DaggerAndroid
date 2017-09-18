# 前言
距离首次接触 [Dagger2](https://github.com/google/dagger) 已经有半年的时间了，从最初的一脸懵逼，到慢慢的熟练使用，这个过程真的感谢 [MVPArms](https://github.com/JessYanCoding/MVPArms)，这半年在 **MVPArms** 真的学到很多东西，由此演变出的 [MVVMArms](https://github.com/xiaobailong24/MVVMArms) 可以说是这半年学习的结晶。其中在构建 **MVVMArms** 的过程中，采用了最新的 Dagger2.11，更好的支持了 Android 的依赖注入。好了，废话就说这么多，下面来通过一个例子来对 Dagger.Android 有更进一步的认识。

下载源码一起看会更好！
Github：[DaggerAndroid](https://github.com/xiaobailong24/DaggerAndroid)

> 如果你还没接触过 Dagger2，可以看我之前转载的一篇文章 - [Dagger2 学习](https://xiaobailong24.me/2017/03/21/Android-Dagger2/)，里面概念讲得很清晰。
> 目前大多数文章还是讲解简单使用 Dagger2，但是对于多 Module 下，怎么通过 Dagger 管理他们之间的依赖关系，还没有这样的文章，我会把在 MVVMArms 中探索出的一种 Dagger.Android 多 Module 管理方案分享给大家。


# Gradle 配置
要在 Android 中使用 **Dagger2** , 先添加 **Gradle** 配置，最新的版本可在 [GitHub](https://github.com/google/dagger/releases) 找到。这里使用了 **Android Studio 3.0 Beta6**。
```
  //dagger.android
  implementation 'com.google.dagger:dagger:2.11'
  annotationProcessor 'com.google.dagger:dagger-compiler:2.11'
  implementation 'com.google.dagger:dagger-android:2.11'
  implementation 'com.google.dagger:dagger-android-support:2.11'
  annotationProcessor 'com.google.dagger:dagger-android-processor:2.11'
```

# 入门篇
Dagger.Android 可以有两种注入方式，下面分别通过 Activity 和 Fragment 来看一下。

## Activity 依赖注入（第一种注入方式）
### AndroidInjectionModule
在整个 Application 的 Component 中添加 **AndroidInjectionModule**。

***AppComponent***
```java
  @Singleton
  @Component(modules = AndroidInjectionModule.class)
  public interface AppComponent {
      void inject(MainApp mainApp);
  }
```
这样就可以确保使用最新的 **Dagger.Android**。

### @Subcomponent
为 Activity 编写 **Subcomponent**，该接口需要继承 ***public interface AndroidInjector<T>***；该接口内有一个被 **@Subcomponent.Builder** 注解的继承于 ***AndroidInjector.Builder<T>*** 的抽象类，其中泛型 T 为要注入的目标 Activity。

***MainActivitySubcomponent***
```java
  @ActivityScope
  @Subcomponent(modules = KobeModule.class)//DataModule
  public interface MainActivitySubcomponent extends AndroidInjector<MainActivity> {
      @Subcomponent.Builder
      abstract class Builder extends AndroidInjector.Builder<MainActivity> {
      }
  }
```
> 一些需要注入的数据类型可以包含在 **@Subcomponent(modules = {})** 中。

***KobeModule***
```java
@Module
  public class KobeModule {
      @ActivityScope
      @Provides
      public Person provideKobe() {
          return new Person("Kobe", 39);
      }
  }
```
***Person***
```java
  public class Person {
      private String name;
      private int age;

      @Inject
      public Person(String name, int age) {
          this.name = name;
          this.age = age;
      }

      public String getName() {
          return name;
      }

      public int getAge() {
          return age;
      }
  }
```

### @Module
接下来，编写 Activity 的 **Module**，绑定上一步新建的 Subcomponent，然后将其添加到全局 Component 中，即上文中的 ***AppComponent***。

***MainActivityModule***
```java
  @Module(subcomponents = MainActivitySubcomponent.class)
  public abstract class MainActivityModule {
      /**
       * 第一种注入方式。需要 Subcomponent
       */
      @Binds
      @IntoMap
      @ActivityKey(MainActivity.class)
      abstract AndroidInjector.Factory<? extends Activity>
      bindActivityInjectorFactory(MainActivitySubcomponent.Builder builder);
  }
```

***AppComponent*** 改写为：
```java
  @Singleton
  @Component(modules = {AndroidInjectionModule.class,
          MainActivityModule.class)
  public interface AppComponent {
      void inject(MainApp mainApp);
  }
```

### HasActivityInjector
让 **MainApp** 实现 **HasActivityInjector** 接口，并注入  **DispatchingAndroidInjector<Activity>**。

***MainApp***
```java
  public class MainApp extends Application implements HasActivityInjector {
      @Inject
      DispatchingAndroidInjector<Activity> mActivityInjector;

      private AppComponent mAppComponent;

      @Override
      public void onCreate() {
          super.onCreate();

          mAppComponent = DaggerAppComponent.builder()
                  .daggerComponent(getDaggerComponent())
                  .build();
          mAppComponent.inject(this);
      }

      public AppComponent getAppComponent() {
          return mAppComponent;
      }

      @Override
      public AndroidInjector<Activity> activityInjector() {
          return mActivityInjector;
      }
  }
```

### AndroidInjection
最后，在目标 Activity 的 **onCreate()** 方法中进行注入，需要注意的是应该在 super.onCreate() 调用前注入。

***MainActivity***
```java
  public class MainActivity extends AppCompatActivity {
    @Inject
    Person mKobe;//依赖注入

    public void onCreate(Bundle savedInstanceState) {
      AndroidInjection.inject(this);
      super.onCreate(savedInstanceState);
    }
  }
```
> 最后不要忘记在 **AndroidManifest.xml** 中指定 **MainApp** 。

### 源码解析
**AndroidInjection.inject()** 从 **MainApp** 获得一个 **DispatchingAndroidInjector<Activity>** 对象，并将 **MainActivity** 传入到 **inject(Activity activity)** 方法。 DispatchingAndroidInjector 为 MainActivity 类查找 **AndroidInjector.Factory** 的实现类，即 **MainActivitySubcomponent.Builder**；接着会创建一个 **AndroidInjector**，即 **MainActivitySubcomponent**，并将 **MainActivity** 传入到 **inject(Activity activity)** 方法。

***AndroidInjection#inject(Activity activity)*** 源码如下：
```java
  public static void inject(Activity activity) {
    checkNotNull(activity, "activity");
    Application application = activity.getApplication();
    //判断 Application 是否实现了 HasActivityInjector 接口
    if (!(application instanceof HasActivityInjector)) {
      throw new RuntimeException(
          String.format(
              "%s does not implement %s",
              application.getClass().getCanonicalName(),
              HasActivityInjector.class.getCanonicalName()));
    }

    //从 Application 中获取 AndroidInjector 对象，即 DispatchingAndroidInjector<Activity> mActivityInjector
    AndroidInjector<Activity> activityInjector =
        ((HasActivityInjector) application).activityInjector();
    checkNotNull(
        activityInjector,
        "%s.activityInjector() returned null",
        application.getClass().getCanonicalName());

    //最后注入到 MainActivity 中，此处是在 Dagger 编译生成的 DaggerAppComponent.MainActivitySubcomponentImpl 类中实现的。
    activityInjector.inject(activity);
  }
```

## Fragment 依赖注入（第二种注入方式）
Fragment 使用的是v4兼容包中的 **android.support.v4.app.Fragment**。
### @Subcomponent
由于在 Activity 依赖注入的第一步已经添加 **AndroidInjectionModule**，所以这里可以直接使用。这种方式其实是第一种方式的简化，如果 **MainFragmentSubcomponent** 和 **MainFragmentSubcomponent.Builder** 没有其他的方法或超类型，如下，
***MainFragmentSubcomponent***
```
  @FragmentScope
  @Subcomponent
  public interface MainFragmentSubcomponent extends AndroidInjector<MainFragment> {
      @Subcomponent.Builder
      public abstract class Builder extends AndroidInjector.Builder<MainFragment> {
      }
  }
```
这时可以省略 **MainFragmentSubcomponent**，也就是说，可以直接不用定义 **MainFragmentSubcomponent**。

### @Module
由于省略了 **Subcomponent**，这时可以使用 **@ContributesAndroidInjector** 注解来自动生成。
***MainFragmentModule***
```java
  @Module
  public abstract class MainFragmentModule {
      /**
       * 第二种注入方式。当 Subcomponent 和 它的 Builder 没有其它方法或超类型时，可以不再需要 Subcomponent
       */
      @FragmentScope
      @ContributesAndroidInjector(modules = JordonModule.class)//DataModule
      abstract MainFragment contributeMainFragment();
  }
```
> 一些需要注入的数据类型可以包含在 **@ContributesAndroidInjector(modules = {})** 中。

将**MainFragmentModule** 添加到 ***AppComponent*** 中：
```java
  @Singleton
  @Component(modules = {AndroidInjectionModule.class,
          MainActivityModule.class,
          MainFragmentModule.class})
  public interface AppComponent {
      void inject(MainApp mainApp);
  }
```

### HasSupportFragmentInjector
让要依赖注入的目标 Fragment(即 MainFragment) 的宿主 Activity(即 MainActivity) 实现 **HasSupportFragmentInjector** 接口

***MainActivity***
```java
  public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    @Inject
    DispatchingAndroidInjector<Fragment> mFragmentInjector;
    //...

    public void onCreate(Bundle savedInstanceState) {
      AndroidInjection.inject(this);
      super.onCreate(savedInstanceState);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return this.mFragmentInjector;
    }
  }
```

> 如果使用 **android.app.Fragment**，Activity 应该实现 **HasFragmentInjector** 接口，并注入  **DispatchingAndroidInjector<Fragment>**。

### AndroidInjection
最后，在目标 Fragment 的 **onAttach()** 方法中进行注入。

***MainFragment***
```java
  public class MainFragment extends Fragment {
    @Inject
    Person mJordon;//依赖注入

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
    }

    //...
  }
```

### 源码解析
Fragment 注入的原理与 Activity 的类似，这里再强调一遍，其实第二种方式是第一种方式的简化，使用 **@ContributesAndroidInjector** 注解来自动生成 **Subcomponent**。
***AndroidSupportInjection#inject(Fragment fragment)*** 源码如下：
```java
  public static void inject(Fragment fragment) {
    checkNotNull(fragment, "fragment");
    //获取 HasSupportFragmentInjector
    HasSupportFragmentInjector hasSupportFragmentInjector = findHasFragmentInjector(fragment);
    Log.d(
        TAG,
        String.format(
            "An injector for %s was found in %s",
            fragment.getClass().getCanonicalName(),
            hasSupportFragmentInjector.getClass().getCanonicalName()));
    //从 Activity 中获取 HasSupportFragmentInjector 对象，即 DispatchingAndroidInjector<Fragment> mFragmentInjector
    AndroidInjector<Fragment> fragmentInjector =
        hasSupportFragmentInjector.supportFragmentInjector();
    checkNotNull(
        fragmentInjector,
        "%s.supportFragmentInjector() returned null",
        hasSupportFragmentInjector.getClass().getCanonicalName());
    //最后注入到 MainFragment 中，此处是在 Dagger 编译生成的 DaggerAppComponent.MainFragmentSubcomponentImpl 类中实现的。
    fragmentInjector.inject(fragment);
  }
```
***AndroidSupportInjection#findHasFragmentInjector(Fragment fragment)***
```
  private static HasSupportFragmentInjector findHasFragmentInjector(Fragment fragment) {
      Fragment parentFragment = fragment;
      while ((parentFragment = parentFragment.getParentFragment()) != null) {
        if (parentFragment instanceof HasSupportFragmentInjector) {
          return (HasSupportFragmentInjector) parentFragment;
        }
      }
      Activity activity = fragment.getActivity();
      if (activity instanceof HasSupportFragmentInjector) {
        return (HasSupportFragmentInjector) activity;
      }
      if (activity.getApplication() instanceof HasSupportFragmentInjector) {
        return (HasSupportFragmentInjector) activity.getApplication();
      }
      throw new IllegalArgumentException(
          String.format("No injector was found for %s", fragment.getClass().getCanonicalName()));
    }
```
> 由源码可知，如果需要在 Fragment 中进行依赖注入，可以有两种实现方式：一种是宿主 Activity 实现 HasSupportFragmentInjector，另一种是 Application 实现 HasSupportFragmentInjector。

## 其他组件
**Service**、**BroadcastReceiver** 和 **ContentProvider** 的注入方式与此类似。
为了方便，Dagger.Android 为我们提供了一些封装好的组件类，下面引用官方文档的一段话，如果有需要，可以直接使用这些组件类。

Because DispatchingAndroidInjector looks up the appropriate AndroidInjector.Factory by the class at runtime, a base class can implement HasActivityInjector/HasFragmentInjector/etc as well as call AndroidInjection.inject(). All each subclass needs to do is bind a corresponding @Subcomponent. Dagger provides a few base types that do this, such as [DaggerActivity](https://google.github.io/dagger/api/latest/dagger/android/DaggerActivity.html) and [DaggerFragment](https://google.github.io/dagger/api/latest/dagger/android/DaggerFragment.html), if you don’t have a complicated class hierarchy. Dagger also provides a [DaggerApplication](https://google.github.io/dagger/api/latest/dagger/android/DaggerApplication.html) for the same purpose — all you need to do is to extend it and override the applicationInjector() method to return the component that should inject the Application.

The following types are also included:

- [DaggerService](https://google.github.io/dagger/api/latest/dagger/android/DaggerService.html) and [DaggerIntentService](https://google.github.io/dagger/api/latest/dagger/android/DaggerIntentService.html)
- [DaggerBroadcastReceiver](https://google.github.io/dagger/api/latest/dagger/android/DaggerBroadcastReceiver.html)
- [DaggerContentProvider](https://google.github.io/dagger/api/latest/dagger/android/DaggerContentProvider.html)

> Note: [DaggerBroadcastReceiver](https://google.github.io/dagger/api/latest/dagger/android/DaggerBroadcastReceiver.html) should only be used when the BroadcastReceiver is registered in the AndroidManifest.xml. When the BroadcastReceiver is created in your own code, prefer constructor injection instead.


# 多 Module 实战
上面只是简单的介绍了Dagger.Android 的使用，下面重点来了，还是将通过一个例子，详解怎么利用 Dagger 构建多 Module 依赖关系，从而实现组件化。
这里，将上述对 Activity 和 Fragment 的依赖注入分离到一个 Library Module 中，利用 **Application.ActivityLifecycleCallbacks** 和 **FragmentManager.FragmentLifecycleCallbacks** 监听，构建全局的依赖注入。

> 这里提前提一下本方案下，Dagger 的依赖关系：
DaggerComponent -> AppComponent -> MainActivitySubcomponent/MainFragmentSubcomponent
其中：**DaggerComponent** 为 library 的注入器，它的作用域是 **@Singleton**；
**AppComponent** 为主 Module 的全局注入器，它的作用域是 **@AppScope**，并且 AppComponent 是**依赖**于 DaggerComponent，也就是说，DaggerComponent 顶级注入器，AppComponent 是主 Module 的注入器；
**MainActivitySubcomponent/MainFragmentSubcomponent** 是 Activity/Fragment 的注入器，作用域为 **@ActivityScope/@FragmentScope**，这里是 @Subcomponent，通过**继承**的方式实现层级依赖，为 AppComponent 的下一级。

***AppScope***
```java
  @Scope
  @Retention(RUNTIME)
  public @interface AppScope {
  }
```
## Library Module
### DaggerFragmentLifecycleCallbacks - 全局 Fragment 依赖注入
***DaggerFragmentLifecycleCallbacks***
```java
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
```
> 可以看到，**DaggerFragmentLifecycleCallbacks** 也是通过进行管理的，在 **onFragmentAttached()** 方法中进行 Fragment 的依赖注入；并且使用 [Timber](https://github.com/JakeWharton/timber) 打印了几个关键生命周期回调Log。

### DaggerActivityLifecycleCallbacks - 全局 Activity 依赖注入
***DaggerActivityLifecycleCallbacks***
```java
  public class DaggerActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
      @Inject
      DaggerFragmentLifecycleCallbacks mFragmentLifecycleCallbacks;

      @Inject
      public DaggerActivityLifecycleCallbacks() {
      }

      @Override
      public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
          Timber.w(activity + " ---> onActivityCreated");
          AndroidInjection.inject(activity);//Dagger.Android Inject for Activity
          if (activity instanceof HasSupportFragmentInjector && activity instanceof FragmentActivity) {
            //如果该 Activity 的 Fragment 需要 Dagger 注入，
            //即实现了 HasSupportFragmentInjector，就会注册上一步的 DaggerFragmentLifecycleCallbacks 来实现 Dagger 注入。
              ((FragmentActivity) activity).getSupportFragmentManager()
                      .registerFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks, true);
          }
      }

      @Override
      public void onActivityStarted(Activity activity) {

      }

      @Override
      public void onActivityResumed(Activity activity) {

      }

      @Override
      public void onActivityPaused(Activity activity) {

      }

      @Override
      public void onActivityStopped(Activity activity) {

      }

      @Override
      public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

      }

      @Override
      public void onActivityDestroyed(Activity activity) {
          Timber.w(activity + " ---> onActivityDestroyed");
      }
  }
```

### DaggerComponent - 顶级注入器
***DaggerComponent***
```java
  @Singleton
  @Component(modules = {AndroidInjectionModule.class,
          DaggerModule.class})
  public interface DaggerComponent {
      Application application();

      void inject(DaggerDelegate daggerDelegate);
  }
```
其中 **DaggerModule** 主要提供一些全局依赖，这里只有一个 **provideApplication()** 方法，可以自行添加需要的东西。
***DaggerModule***
```java
  @Module
  public class DaggerModule {
      private final Application mApplication;

      public DaggerModule(Application application) {
          mApplication = application;
      }

      @Singleton
      @Provides
      public Application provideApplication() {
          return this.mApplication;
      }
  }
```

### DaggerDelegate - 开始注入
***DaggerDelegate***
```
  public class DaggerDelegate {
      @Inject
      DaggerActivityLifecycleCallbacks mActivityLifecycleCallbacks;

      private DaggerComponent mComponent;
      private final Application mApplication;

      public DaggerDelegate(Application application) {
          mApplication = application;
      }

      public void onCreate() {
          Timber.plant(new Timber.DebugTree());

          mComponent = DaggerDaggerComponent.builder()
                  .daggerModule(new DaggerModule(mApplication))
                  .build();
          mComponent.inject(this);

          mApplication.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
      }


      public DaggerComponent getComponent() {
          return mComponent;
      }
  }
```
这里的 **DaggerDelegate** 是一个代理类，为了克服 Application 继承的问题，通过封装一个代理类来对 library 的 Dagger 注入进行管理，然后在需要的 Module 里使用。

> 至此，Library Module 的依赖注入结构搭建完成。

## App Module
由上一大节可知，Library Module 中是没有 Application 的，它是一个 **library**，如果想使用它，需要在 **主 Module** 中进行依赖。再说一遍，**DaggerComponent** 是顶级注入器，**AppComponent** 主 Module 的全局注入器，它仅限定于 **@AppScope**。
首先在 **app/build.gradle** 中添加上节中的 Library Moduel 依赖：
```gradle
dependencies {
    //library
    implementation project(':library')

    //...
}
```

### AppComponent - 主 Module 的全局注入器
***AppComponent***
```java
  @AppScope
  @Component(dependencies = DaggerComponent.class,
          modules = {AppModule.class,
                  MainActivityModule.class,
                  MainFragmentModule.class})
  public interface AppComponent {
      void inject(MainApp mainApp);
  }
```
可以看到，**dependencies = DaggerComponent.class**，这就是上文所说的：**AppComponent 是依赖于 DaggerComponent，也就是说，DaggerComponent 顶级注入器，AppComponent 是主 Module 的注入器**。
其中，**AppModule** 主要提供主 Module 的一些全局依赖，可自行扩展。
***AppModule***
```
  @Module
  public class AppModule {
      private Application mApplication;

      public AppModule(Application application) {
          mApplication = application;
      }
  }
```

## MainApp - 真正注入的地方
```java
  public class MainApp extends Application implements HasActivityInjector {
      @Inject
      DispatchingAndroidInjector<Activity> mActivityInjector;

      private DaggerDelegate mDaggerDelegate;
      private AppComponent mAppComponent;

      @Override
      public void onCreate() {
          super.onCreate();

          //Library 的依赖注入（顶级）
          mDaggerDelegate = new DaggerDelegate(this);
          mDaggerDelegate.onCreate();

          //注入主 Module 中（该 Module 全局）
          mAppComponent = DaggerAppComponent.builder()
                  .daggerComponent(getDaggerComponent())
                  .build();
          mAppComponent.inject(this);

      }

      public DaggerComponent getDaggerComponent() {
          return mDaggerDelegate.getComponent();
      }

      public AppComponent getAppComponent() {
          return mAppComponent;
      }

      @Override
      public AndroidInjector<Activity> activityInjector() {
          return mActivityInjector;
      }
  }
```
**MainApp** 是真正进行依赖注入的地方，首先使用上一节 Library Module 中的 **DaggerDelegate** 进行顶级依赖注入，然后进行 主Module 的依赖注入。需要注意的是，MainApp 必须实现 **HasActivityInjector** 接口，才能进行 Dagger.Android 注入。

> 最后不要忘记在 **AndroidManifest.xml** 中指定 **MainApp** 。

### MainActivityModule/MainFragmentModule
由第一节的例子可知：当 Subcomponent 和 它的 Builder 没有其它方法或超类型时，可以不再需要手写 Subcomponent，而是通过 **@ContributesAndroidInjector** 注解来自动生成。
所以，这里的 MainActivitySubcomponent/MainFragmentSubcomponent 可以省略；这样，MainActivityModule/MainFragmentModule 如下：
***可以省略；这样，MainActivityModule***
```
  @Module
  public abstract class MainActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = KobeModule.class)//DataModule
    abstract MainActivity contributeMainActivity();
  }
```
***MainFragmentModule***
```
  @Module
  public abstract class MainFragmentModule {
      @FragmentScope
      @ContributesAndroidInjector(modules = JordonModule.class)//DataModule
      abstract MainFragment contributeMainFragment();
  }
```
> KobeModule 和 JordonModule 依旧是第一大节中的，就不再重复贴代码了。至此就可以在 Activity/Fragment 中进行依赖注入了。Dagger.Adnroid 注入是在 Library Module 中的 **DaggerActivityLifecycleCallbacks/DaggerFragmentLifecycleCallbacks** 完成的，这是一个全局监听器，使用 **DaggerDelegate** 在 **MainApp** 中进行注册的。

### 总结
再来强调一下，本方案的 Dagger 层级依赖关系：
> DaggerComponent -> AppComponent -> MainActivitySubcomponent/MainFragmentSubcomponent
其中：**DaggerComponent** 为 library 的注入器，它的作用域是 **@Singleton**；
**AppComponent** 为主 Module 的全局注入器，它的作用域是 **@AppScope**，并且 AppComponent 是**依赖**于 DaggerComponent，也就是说，DaggerComponent 顶级注入器，AppComponent 是主 Module 的注入器；
**MainActivitySubcomponent/MainFragmentSubcomponent** 是 Activity/Fragment 的注入器，作用域为 **@ActivityScope/@FragmentScope**，这里是 @Subcomponent，通过**继承**的方式实现层级依赖，为 AppComponent 的下一级。

# 总结
上面两个例子，第一个介绍了 Dagger.Android 的简单使用，这是 Dagger2.11 的新姿势；这样不需要每个 Activity/Fragment 中再重复手写一串的依赖注入代码；而是通过实现 **HasActivityInjector/HasSupportFragmentInjector** 接口，通过生命周期的监听，使用 **AndroidInjection.inject()** 自动注入。
以上是 **MVVMArms** 框架的基本的 Dagger 层级依赖关系，更详细的使用可以查看 [MVVMArms](https://github.com/xiaobailong24/MVVMArms)。
如果各位有任何疑问，欢迎交流。如果文中有不正之处，也请不吝赐教。
知识分享才会有快乐，后面我会继续分解 MVVMArms 的关键模块，如果各位对 MVVMArms 有任何问题或建议，欢迎一起交流。

# Github
以上方案的源码都可以在 Github 查看。
- [DaggerAndroid](https://github.com/xiaobailong24/DaggerAndroid)
- [MVVMArms](https://github.com/xiaobailong24/MVVMArms)

# 相关资源
1. [Dagger 2](https://github.com/google/dagger)
2. [Dagger & Android](https://google.github.io/dagger/android.html)
3. [Dagger2 学习](https://xiaobailong24.me/2017/03/21/Android-Dagger2)
