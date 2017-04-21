package com.worldventures.dreamtrips.modules.feed.service.command;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.action.ValueCommandAction;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class PostCreatedCommand extends ValueCommandAction<TextualPost> implements InjectableAction {

   @Inject SessionHolder<UserSession> sessionHolder;

   public PostCreatedCommand(TextualPost value) {
      super(value);
   }

   public FeedItem<TextualPost> getFeedItem() {
      return FeedItem.create(getResult(), sessionHolder.get().get().getUser());
   }
}
