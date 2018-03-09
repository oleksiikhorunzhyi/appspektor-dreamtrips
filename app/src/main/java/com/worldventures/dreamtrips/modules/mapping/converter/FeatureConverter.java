package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.session.model.Feature;

import org.jetbrains.annotations.NotNull;

import io.techery.mappery.MapperyContext;

public class FeatureConverter implements Converter<Feature, com.worldventures.core.model.session.Feature> {
   @Override
   public Class<Feature> sourceClass() {
      return Feature.class;
   }

   @Override
   public Class<com.worldventures.core.model.session.Feature> targetClass() {
      return com.worldventures.core.model.session.Feature.class;
   }

   @Override
   public com.worldventures.core.model.session.Feature convert(@NotNull MapperyContext mapperyContext, Feature apiFeature) {
      switch (apiFeature.name()) {
         case TRIPS:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.TRIPS);
         case REP_TOOLS:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.REP_TOOLS);
         case SOCIAL:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.SOCIAL);
         case DTL:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.DTL);
         case REP_SUGGEST_MERCHANT:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.REP_SUGGEST_MERCHANT);
         case BOOK_TRIP:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.BOOK_TRIP);
         case BOOK_TRAVEL:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.BOOK_TRAVEL);
         case MEMBERSHIP:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.MEMBERSHIP);
         case WALLET:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.WALLET);
         case WALLET_PROVISIONING:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.WALLET_PROVISIONING);
         case TRIP_IMAGES:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.TRIP_IMAGES);
         case BUCKET_LIST:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.BUCKET_LIST);
         case SEND_FEEDBACK:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.SEND_FEED_BACK);
         case INVITATIONS:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.INVITATIONS);
         case SETTINGS:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.SETTINGS);
         case DREAM_LIFE_CLUB:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.DLC);
         default:
            return new com.worldventures.core.model.session.Feature(com.worldventures.core.model.session.Feature.UNKNOWN);
      }
   }
}
