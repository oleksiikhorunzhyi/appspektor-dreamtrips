package com.worldventures.dreamtrips.api.api_common.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.worldventures.dreamtrips.api.entity.converter.EntityDeserializer;
import com.worldventures.dreamtrips.api.entity.model.EntityHolder;
import com.worldventures.dreamtrips.api.feed.converter.FeedItemDeserializer;
import com.worldventures.dreamtrips.api.feed.model.FeedItem;
import com.worldventures.dreamtrips.api.settings.converter.SettingsDeserializer;
import com.worldventures.dreamtrips.api.settings.converter.SettingsSerializer;
import com.worldventures.dreamtrips.api.settings.model.Setting;

import java.util.Date;
import java.util.ServiceLoader;

public class GsonProvider {

    public GsonBuilder provideBuilder() {
        GsonBuilder builder = new GsonBuilder()
                .setExclusionStrategies(new SerializedNameExclusionStrategy())
                //
                .registerTypeAdapterFactory(new SmartEnumTypeAdapterFactory("unknown"))
                .registerTypeAdapter(Date.class, new DateTimeSerializer())
                .registerTypeAdapter(Date.class, new DateTimeDeserializer());
        // models
        for (TypeAdapterFactory factory : ServiceLoader.load(TypeAdapterFactory.class)) {
            builder.registerTypeAdapterFactory(factory);
        }
        // generic models
        Gson gsonForAdapters = builder.create();
        EntityDeserializer entityDeserializer = new EntityDeserializer(builder);
        builder.registerTypeAdapter(EntityHolder.class, entityDeserializer);
        builder.registerTypeAdapter(FeedItem.class, new FeedItemDeserializer(gsonForAdapters, entityDeserializer));
        builder.registerTypeAdapter(Setting.class, new SettingsSerializer(gsonForAdapters));
        builder.registerTypeAdapter(Setting.class, new SettingsDeserializer(gsonForAdapters));
        //
        return builder;
    }


    public Gson provideGson() {
        return provideBuilder().create();
    }

}
