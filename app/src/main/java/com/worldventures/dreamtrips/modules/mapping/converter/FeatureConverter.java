package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.session.model.Feature;

import org.jetbrains.annotations.NotNull;

import io.techery.mappery.MapperyContext;

public class FeatureConverter implements Converter<Feature, com.worldventures.dreamtrips.core.session.acl.Feature> {
   @Override
   public Class<Feature> sourceClass() {
      return Feature.class;
   }

   @Override
   public Class<com.worldventures.dreamtrips.core.session.acl.Feature> targetClass() {
      return com.worldventures.dreamtrips.core.session.acl.Feature.class;
   }

   @Override
   public com.worldventures.dreamtrips.core.session.acl.Feature convert(@NotNull MapperyContext mapperyContext, Feature apiFeature) {
      switch (apiFeature.name()) {
         case TRIPS:
            return new com.worldventures.dreamtrips.core.session.acl.Feature(com.worldventures.dreamtrips.core.session.acl.Feature.TRIPS);
         case REP_TOOLS:
            return new com.worldventures.dreamtrips.core.session.acl.Feature(com.worldventures.dreamtrips.core.session.acl.Feature.REP_TOOLS);
         case SOCIAL:
            return new com.worldventures.dreamtrips.core.session.acl.Feature(com.worldventures.dreamtrips.core.session.acl.Feature.SOCIAL);
         case DTL:
            return new com.worldventures.dreamtrips.core.session.acl.Feature(com.worldventures.dreamtrips.core.session.acl.Feature.DTL);
         case REP_SUGGEST_MERCHANT:
            return new com.worldventures.dreamtrips.core.session.acl.Feature(com.worldventures.dreamtrips.core.session.acl.Feature.REP_SUGGEST_MERCHANT);
         case BOOK_TRIP:
            return new com.worldventures.dreamtrips.core.session.acl.Feature(com.worldventures.dreamtrips.core.session.acl.Feature.BOOK_TRIP);
         case BOOK_TRAVEL:
            return new com.worldventures.dreamtrips.core.session.acl.Feature(com.worldventures.dreamtrips.core.session.acl.Feature.BOOK_TRAVEL);
         case MEMBERSHIP:
            return new com.worldventures.dreamtrips.core.session.acl.Feature(com.worldventures.dreamtrips.core.session.acl.Feature.MEMBERSHIP);
         case WALLET:
            return new com.worldventures.dreamtrips.core.session.acl.Feature(com.worldventures.dreamtrips.core.session.acl.Feature.WALLET);
         case WALLET_PROVISIONING:
            return new com.worldventures.dreamtrips.core.session.acl.Feature(com.worldventures.dreamtrips.core.session.acl.Feature.WALLET_PROVISIONING);
         default:
            return new com.worldventures.dreamtrips.core.session.acl.Feature(com.worldventures.dreamtrips.core.session.acl.Feature.UNKNOWN);
      }
   }
}
