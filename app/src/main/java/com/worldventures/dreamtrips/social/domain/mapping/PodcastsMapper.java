package com.worldventures.dreamtrips.social.domain.mapping;

import com.worldventures.core.model.CachedModel;
import com.worldventures.dreamtrips.modules.dtl.model.mapping.Mapper;
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast;

public class PodcastsMapper implements Mapper<com.worldventures.dreamtrips.api.podcasts.model.Podcast, Podcast> {

   @Override
   public Podcast map(com.worldventures.dreamtrips.api.podcasts.model.Podcast source) {
      CachedModel cachedModel = new CachedModel(source.audioURL(), source.audioURL(), source.title());
      cachedModel.setEntityClass(Podcast.class);

      Podcast podcast = new Podcast();
      podcast.setTitle(source.title());
      podcast.setCategory(source.category());
      podcast.setDate(source.date());
      podcast.setDescription(source.description());
      podcast.setDuration(source.duration());
      podcast.setFileUrl(source.audioURL());
      podcast.setImageUrl(source.imageURL());
      podcast.setCachedModel(cachedModel);
      return podcast;
   }
}
