package com.worldventures.dreamtrips.modules.feed.converter;

import com.worldventures.dreamtrips.api.feed.model.FeedItemLinks;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.feed.item.Links;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class LinksConverter implements Converter<FeedItemLinks, Links> {

   @Override
   public Class<FeedItemLinks> sourceClass() {
      return FeedItemLinks.class;
   }

   @Override
   public Class<Links> targetClass() {
      return Links.class;
   }

   @Override
   public Links convert(MapperyContext mapperyContext, FeedItemLinks feedItemLinks) {
      Links links = new Links();
      links.setUsers(mapperyContext.convert(feedItemLinks.users(), User.class));
      return links;
   }
}
