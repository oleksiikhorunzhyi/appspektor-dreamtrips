package com.worldventures.dreamtrips.api.entity.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.worldventures.dreamtrips.api.api_common.model.UniqueIdentifiable;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSimple;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSocialized;
import com.worldventures.dreamtrips.api.entity.model.BaseEntityHolder;
import com.worldventures.dreamtrips.api.entity.model.EntityHolder;
import com.worldventures.dreamtrips.api.photos.model.PhotoAttachment;
import com.worldventures.dreamtrips.api.photos.model.PhotoSimple;
import com.worldventures.dreamtrips.api.photos.model.PhotoSocialized;
import com.worldventures.dreamtrips.api.post.model.response.PostSimple;
import com.worldventures.dreamtrips.api.post.model.response.PostSocialized;
import com.worldventures.dreamtrips.api.trip.model.TripWithDetails;

import org.immutables.gson.Gson.TypeAdapters;
import org.immutables.value.Value;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.worldventures.dreamtrips.api.entity.model.BaseEntityHolder.Type.BUCKET_LIST_ITEM;
import static com.worldventures.dreamtrips.api.entity.model.BaseEntityHolder.Type.PHOTO;
import static com.worldventures.dreamtrips.api.entity.model.BaseEntityHolder.Type.POST;
import static com.worldventures.dreamtrips.api.entity.model.BaseEntityHolder.Type.TRIP;

@TypeAdapters
public class EntityDeserializer implements JsonDeserializer<EntityHolder> {

    private Gson gson;
    private Map<BaseEntityHolder.Type, DeserializationClass> entityTypes;

    public EntityDeserializer(GsonBuilder gson) {
        this.gson = gson.registerTypeAdapter(EntityHolder.class, this).create();
        entityTypes = new HashMap<BaseEntityHolder.Type, DeserializationClass>();
        entityTypes.put(TRIP, new DeserializationClass(TripWithDetails.class));
        entityTypes.put(POST, new DeserializationClass(PostSocialized.class, PostSimple.class));
        entityTypes.put(PHOTO, new DeserializationClass(PhotoSocialized.class, PhotoSimple.class, PhotoAttachment.class));
        entityTypes.put(BUCKET_LIST_ITEM, new DeserializationClass(BucketItemSocialized.class, BucketItemSimple.class));
    }

    @Override
    public EntityHolder<? extends UniqueIdentifiable> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonEntityHolder jsonHolder = gson.fromJson(json, JsonEntityHolder.class);
        DeserializationClass clazz = entityTypes.get(jsonHolder.type());
        if (clazz == null) {
            return ImmutableObjEntityHolder.builder().type(jsonHolder.type()).entity(null).build();
        } else {
            UniqueIdentifiable entity = null;
            List<Throwable> issues = new ArrayList<Throwable>();
            for (Class<? extends UniqueIdentifiable> c : clazz.candidates) {
                try {
                    entity = gson.fromJson(jsonHolder.entity(), c);
                    break;
                } catch (Throwable e) {
                    issues.add(e);
                }
            }
            if (entity == null) {
                throw new JsonParseException("Can't parse entity with " + clazz + ".\nJson: " + jsonHolder.entity() + "\nIssues: \n" + issues);
            }
            return ImmutableObjEntityHolder.builder().type(jsonHolder.type()).entity(entity).build();
        }
    }

    @TypeAdapters
    @Value.Immutable
    interface ObjEntityHolder<T extends UniqueIdentifiable> extends EntityHolder<T> {}

    @TypeAdapters
    @Value.Immutable
    interface JsonEntityHolder extends BaseEntityHolder<JsonElement> {}

    @TypeAdapters
    @Value.Immutable
    public interface UnknownUniqueIdentifiable extends UniqueIdentifiable {}

    static class DeserializationClass {
        final Class<? extends UniqueIdentifiable>[] candidates;

        public DeserializationClass(Class<? extends UniqueIdentifiable>... candidates) {
            this.candidates = candidates;
        }

        @Override
        public String toString() {
            return "DeserializationClass{" +
                    "candidates=" + Arrays.toString(candidates) +
                    '}';
        }
    }

}
