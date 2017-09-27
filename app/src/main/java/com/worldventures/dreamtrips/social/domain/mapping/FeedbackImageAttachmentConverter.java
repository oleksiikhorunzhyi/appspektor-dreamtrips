package com.worldventures.dreamtrips.social.domain.mapping;

import com.worldventures.dreamtrips.api.feedback.model.FeedbackAttachment;
import com.worldventures.dreamtrips.api.feedback.model.ImmutableFeedbackAttachment;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.social.ui.infopages.model.FeedbackImageAttachment;

import io.techery.mappery.MapperyContext;

public class FeedbackImageAttachmentConverter implements Converter<FeedbackImageAttachment, FeedbackAttachment> {

   @Override
   public Class<FeedbackImageAttachment> sourceClass() {
      return FeedbackImageAttachment.class;
   }

   @Override
   public Class<FeedbackAttachment> targetClass() {
      return FeedbackAttachment.class;
   }

   @Override
   public FeedbackAttachment convert(MapperyContext mapperyContext, FeedbackImageAttachment attachment) {
      return ImmutableFeedbackAttachment.builder()
            .originUrl(attachment.getUrl())
            .type(attachment.getType() == com.worldventures.dreamtrips.social.ui.infopages.model.FeedbackAttachment.Type.IMAGE ?
                  FeedbackAttachment.FeedbackType.IMAGE : null)
            .build();
   }
}
