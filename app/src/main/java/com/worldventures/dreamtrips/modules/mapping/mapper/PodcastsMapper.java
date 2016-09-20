package com.worldventures.dreamtrips.modules.mapping.mapper;

import com.worldventures.dreamtrips.modules.dtl.model.mapping.Mapper;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;

public class PodcastsMapper implements Mapper<com.worldventures.dreamtrips.api.podcasts.model.Podcast, Podcast> {

   @Override
   public Podcast map(com.worldventures.dreamtrips.api.podcasts.model.Podcast source) {
      Podcast podcast = new Podcast();
      podcast.setTitle(source.title());
      podcast.setCategory(source.category());
      podcast.setDate(source.date());
      podcast.setDescription(source.description());
      podcast.setDuration(source.duration());
      podcast.setFileUrl(source.audioURL());
      podcast.setImageUrl(source.imageURL());
      podcast.setSize(source.size());
      podcast.setSpeaker(source.speaker());
      return podcast;
   }
}
