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

import java.lang.reflect.Type;

public class GsonAttachmentAdapter implements JsonSerializer<AttachmentHolder>, JsonDeserializer<AttachmentHolder> {
    @Override
    public AttachmentHolder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject =  json.getAsJsonObject();
        String type = jsonObject.get("type").toString();

        AttachmentHolder holder = new AttachmentHolder();
        holder.setType(type);
        Class<?> clazz;
        switch (type) {
            case AttachmentType.IMAGE:
                clazz = ImageAttachment.class;
                break;
            case AttachmentType.LOCATION:
            case AttachmentType.BUCKET_LIST:
            case AttachmentType.MERCHAN:
            default:
                return null;
        }

        holder.setItem(context.deserialize(jsonObject.get("item"), clazz));
        return holder;
    }

    @Override
    public JsonElement serialize(AttachmentHolder src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src);
    }
}
