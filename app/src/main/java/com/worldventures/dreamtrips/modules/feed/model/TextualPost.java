package com.worldventures.dreamtrips.modules.feed.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.modules.common.model.BaseEntity;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.Date;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class TextualPost extends BaseFeedObject {

    private String description;

    public String getDescription() {
        return description;
    }


}
