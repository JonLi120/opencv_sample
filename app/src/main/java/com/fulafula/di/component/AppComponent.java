package com.fulafula.di.component;

import android.app.Application;

import com.fulafula.FulaApplication;
import com.fulafula.di.module.ActivityModule;
import com.fulafula.di.module.AppModule;
import com.fulafula.di.module.FragmentModule;
import com.fulafula.di.module.ServiceModule;
import com.fulafula.di.module.ViewModelModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        ActivityModule.class,
        FragmentModule.class,
        ServiceModule.class,
        ViewModelModule.class
})
public interface AppComponent extends AndroidInjector<FulaApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application app);

        AppComponent build();
    }

    @Override
    void inject(FulaApplication instance);
}
