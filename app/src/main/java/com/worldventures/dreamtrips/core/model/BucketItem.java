package com.worldventures.dreamtrips.core.model;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;
import com.google.common.base.Optional;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.view.adapter.item.Swipeable;

@DefaultSerializer(TaggedFieldSerializer.class)
public class BucketItem extends BaseEntity implements Swipeable {

    @TaggedFieldSerializer.Tag(1)
    private String name;

    @TaggedFieldSerializer.Tag(2)
    private boolean done;

    @TaggedFieldSerializer.Tag(3)
    private String description;

    @TaggedFieldSerializer.Tag(4)
    private String category;

    @TaggedFieldSerializer.Tag(5)
    private String friends;

    private boolean isPinned;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSPName() {
        return Prefs.PREFIX + getId();
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

    @Override
    public long getItemId() {
        return getId();
    }

    @Override
    public int getSwipeReactionType() {
        return RecyclerViewSwipeManager.REACTION_CAN_SWIPE_LEFT | RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_RIGHT;
    }

    @Override
    public void setPinnedToSwipeLeft(boolean pinned) {
        this.isPinned = pinned;
    }

    @Override
    public boolean isPinnedToSwipeLeft() {
        return isPinned;
    }
}
