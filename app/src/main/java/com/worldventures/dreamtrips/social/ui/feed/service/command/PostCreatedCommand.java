package com.worldventures.dreamtrips.social.ui.feed.service.command;

import com.worldventures.core.janet.ValueCommandAction;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class PostCreatedCommand extends ValueCommandAction<TextualPost> implements InjectableAction {

   @Inject SessionHolder sessionHolder;

   public PostCreatedCommand(TextualPost value) {
      super(value);
   }

   public FeedItem<TextualPost> getFeedItem() {
      return FeedItem.create(getResult(), sessionHolder.get().get().getUser());
   }
}
