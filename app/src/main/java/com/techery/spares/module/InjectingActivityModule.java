package com.techery.spares.module;

import android.content.Context;

import com.techery.spares.adapter.AdapterBuilder;
import com.techery.spares.ui.activity.InjectingActivity;
import com.techery.spares.utils.TabsController;
import com.techery.spares.utils.params.ParamsBuilderCreator;
import com.techery.spares.utils.params.ParamsExtractor;

import dagger.Module;
import dagger.Provides;

@Module(library = true, overrides = true)
public class InjectingActivityModule {
    private final InjectingActivity activity;
    private final Injector injector;

    public InjectingActivityModule(InjectingActivity activity, Injector injector) {
        this.activity = activity;
        this.injector = injector;
    }

    @Provides
    Context provideActivityContext() {
        return activity;
    }

    @Provides
    InjectingActivity provideActivity() {
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
    TabsController provideTabsController(InjectingActivity activity) {
        return new TabsController(activity);
    }

    @Provides
    ParamsExtractor provideParamsExtractor(InjectingActivity activity) {
        return new ParamsExtractor(activity);
    }

    @Provides
    ParamsBuilderCreator provideParamsBuilderCreator() {
        return new ParamsBuilderCreator();
    }
}
