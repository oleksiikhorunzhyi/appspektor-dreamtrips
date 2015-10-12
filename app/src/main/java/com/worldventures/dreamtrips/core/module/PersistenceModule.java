package com.worldventures.dreamtrips.core.module;

import android.content.Context;
import android.content.SharedPreferences;

import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.TermsConditionsValidator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class PersistenceModule {

    @Provides
    @Singleton
    public Prefs providePrefs(SharedPreferences sharedPreferences) {
        return new Prefs(sharedPreferences);
    }

    @Provides
    @Singleton
    public TermsConditionsValidator provideTermsConditionsValidator(SharedPreferences sharedPreferences){
        return new TermsConditionsValidator(sharedPreferences);
    }

    @Provides
    @Singleton
    public SnappyRepository provideDB(Context context) {
        return new SnappyRepository(context);
    }
}
