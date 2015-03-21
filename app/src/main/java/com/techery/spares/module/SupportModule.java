package com.techery.spares.module;

import android.content.Context;

import com.techery.spares.utils.AnnotationsHelper;
import com.techery.spares.utils.BinderRetriever;
import com.techery.spares.utils.intent.IntentBuilder;
import com.techery.spares.utils.intent.ServiceLauncher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class SupportModule {
    @Provides
    @Singleton
    AnnotationsHelper provideAnnotationsHelper() {
        return new AnnotationsHelper();
    }

    @Provides
    ServiceLauncher provideServiceLauncher(Context context, IntentBuilder intentBuilder) {
        return new ServiceLauncher(context, intentBuilder);
    }

    @Provides
    IntentBuilder provideIntentBuilder(Context context) {
        return new IntentBuilder(context);
    }

    @Provides
    BinderRetriever provideBinderRetriever(Context context) {
        return new BinderRetriever(context);
    }
}
