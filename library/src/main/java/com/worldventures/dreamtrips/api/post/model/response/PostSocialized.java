package com.worldventures.dreamtrips.api.post.model.response;

import com.worldventures.dreamtrips.api.comment.model.Commentable;
import com.worldventures.dreamtrips.api.likes.model.Likeable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface PostSocialized extends Post, Likeable, Commentable {

}
