package com.worldventures.dreamtrips.modules.trips.model.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.modules.trips.model.ClusterHolder;
import com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder;
import com.worldventures.dreamtrips.modules.trips.model.PinHolder;
import com.worldventures.dreamtrips.modules.trips.model.UndefinedMapObjectHolder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder.Type.CLUSTER;
import static com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder.Type.PIN;
import static com.worldventures.dreamtrips.modules.trips.model.MapObjectHolder.Type.UNDEFINED;

public class MapObjectDeserializer<T extends MapObjectHolder> implements JsonDeserializer<T> {

    private Map<MapObjectHolder.Type, Class<? extends MapObjectHolder>> modelByType = new HashMap<>();

    {
        modelByType.put(PIN, PinHolder.class);
        modelByType.put(CLUSTER, ClusterHolder.class);
        modelByType.put(UNDEFINED, UndefinedMapObjectHolder.class);
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        MapObjectHolder.Type type = null;
        JsonElement typeElement = json.getAsJsonObject().get("type");
        if (!typeElement.isJsonNull()) {
            type = context.deserialize(typeElement.getAsJsonPrimitive(), MapObjectHolder.Type.class);
        }
        if (type == null) type = UNDEFINED;
        MapObjectHolder model = context.deserialize(json, modelByType.get(type));
        return (T) model;
    }
}
