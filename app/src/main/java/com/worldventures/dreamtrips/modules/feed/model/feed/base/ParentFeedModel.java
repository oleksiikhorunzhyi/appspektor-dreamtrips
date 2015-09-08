package com.worldventures.dreamtrips.modules.feed.model.feed.base;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;

import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class ParentFeedModel {

    private String type;
    List<BaseEventModel<IFeedObject>> items;

    public List<BaseEventModel<IFeedObject>> getItems() {
        return items;
    }

    public String getType() {
        return type;
    }

    public boolean isSingle() {
        return type.equals("Single");
    }

    public void setItems(List<BaseEventModel<IFeedObject>> items) {
        this.items = items;
    }
}
