package com.worldventures.dreamtrips.modules.infopages.model;


import com.worldventures.dreamtrips.api.feedback.model.FeedbackReason;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import io.techery.mappery.MapperyContext;

public class FeedbackTypeConverter implements Converter<FeedbackReason, FeedbackType> {
   @Override
   public Class<FeedbackReason> sourceClass() {
      return FeedbackReason.class;
   }

   @Override
   public Class<FeedbackType> targetClass() {
      return FeedbackType.class;
   }

   @Override
   public FeedbackType convert(MapperyContext mapperyContext, FeedbackReason feedbackReason) {
      return new FeedbackType(feedbackReason.id(), feedbackReason.text());
   }
}
