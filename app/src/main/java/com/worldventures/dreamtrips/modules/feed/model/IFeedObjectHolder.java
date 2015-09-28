package com.worldventures.dreamtrips.modules.feed.model;

public interface IFeedObjectHolder<T extends IFeedObject> {

    BaseEventModel.Type getType();

    T getItem();
}
