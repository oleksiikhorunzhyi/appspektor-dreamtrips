package com.worldventures.dreamtrips.api.photos.model;

import com.worldventures.dreamtrips.api.comment.model.Commentable;
import com.worldventures.dreamtrips.api.likes.model.Likeable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface PhotoSocialized extends PhotoWithAuthor, Likeable, Commentable {

}
