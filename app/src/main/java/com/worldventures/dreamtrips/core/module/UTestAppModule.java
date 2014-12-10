package com.worldventures.dreamtrips.core.module;

import com.worldventures.dreamtrips.core.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Trip;

import org.json.JSONObject;

import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Callback;
import retrofit.http.Field;

@Module(
        overrides = true,
        library = true,
        complete = false
)
public class UTestAppModule {
    @Provides
    @Singleton
    DreamTripsApi provideMockClient() {
        return new DreamTripsApi() {
            @Override
            public void token(@Field("username") String username, @Field("password") String password, Callback<JSONObject> callback) {

            }

            @Override
            public void trips(Callback<List<Trip>> callback) {

            }
        };
    }
}