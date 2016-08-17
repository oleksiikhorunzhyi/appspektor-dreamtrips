package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.repository.SnappyRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class PersistenceModule {

    @Provides
    @Singleton
    public SnappyRepository provideDB(Context context) {
        return new SnappyRepositoryImpl(context);
    }
}
