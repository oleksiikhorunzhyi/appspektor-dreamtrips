package com.messenger.messengerservers.xmpp.providers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.model.ImageAttachment;
import com.messenger.messengerservers.model.LocationAttachment;

import java.lang.reflect.Type;

public class GsonAttachmentAdapter implements JsonSerializer<AttachmentHolder>, JsonDeserializer<AttachmentHolder> {
    @Override
    public AttachmentHolder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            JsonElement jsonItem = jsonObject.get("item");

            AttachmentHolder holder = new AttachmentHolder();
            // noinspection all
            holder.setType(type);
            switch (type) {
                case AttachmentType.IMAGE:
                    holder.setItem(context.deserialize(jsonItem, ImageAttachment.class));
                    break;
                case AttachmentType.LOCATION:
                    holder.setItem(context.deserialize(jsonItem, LocationAttachment.class));
                    break;
                default:
                    holder.setItem(null);
                    holder.setType(AttachmentType.UNSUPPORTED);
                    return holder;
            }
            return holder;
    }

    @Override
    public JsonElement serialize(AttachmentHolder src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src);
    }
}
