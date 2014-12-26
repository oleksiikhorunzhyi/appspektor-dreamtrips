package com.techery.spares.module;

import android.content.Context;

import com.techery.spares.adapter.AdapterBuilder;
import com.techery.spares.ui.activity.BaseActivity;
import com.techery.spares.utils.TabsController;
import com.techery.spares.utils.params.ParamsBuilderCreator;
import com.techery.spares.utils.params.ParamsExtractor;

import dagger.Module;
import dagger.Provides;

@Module(library = true, overrides = true)
public class InjectingActivityModule {
    private final BaseActivity activity;
    private final Injector injector;

    public InjectingActivityModule(BaseActivity activity, Injector injector) {
        this.activity = activity;
        this.injector = injector;
    }

    @Provides
    Context provideActivityContext() {
        return activity;
    }

    @Provides
    BaseActivity provideActivity() {
        return activity;
    }

    @Provides
    Injector provideActivityInjector() {
        return injector;
    }

    @Provides
    AdapterBuilder provideAdapterBuilder(Context context, Injector injector) {
        return new AdapterBuilder(injector, context);
    }

    @Provides
    TabsController provideTabsController(BaseActivity activity) {
        return new TabsController(activity);
    }

    @Provides
    ParamsExtractor provideParamsExtractor(BaseActivity activity) {
        return new ParamsExtractor(activity);
    }

    @Provides
    ParamsBuilderCreator provideParamsBuilderCreator() {
        return new ParamsBuilderCreator();
    }
}
