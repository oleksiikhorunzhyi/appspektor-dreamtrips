package com.techery.spares.module;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;

import com.techery.spares.loader.LoaderFactory;
import com.techery.spares.ui.activity.InjectingActivity;
import com.techery.spares.utils.AnnotationsHelper;
import com.techery.spares.utils.BinderRetriever;
import com.techery.spares.utils.ObjectStateHelper;
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
    @Singleton
    ObjectStateHelper provideAnnotationsHelper(AnnotationsHelper annotationsHelper) {
        return new ObjectStateHelper(annotationsHelper);
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
