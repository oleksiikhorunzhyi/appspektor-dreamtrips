package com.worldventures.dreamtrips.modules.membership.model.converter;


import com.worldventures.dreamtrips.api.invitation.model.InvitationPreview;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;

import io.techery.mappery.MapperyContext;

public class InviteTemplateFromInvitationPreviewConverter implements Converter<InvitationPreview, InviteTemplate> {
   @Override
   public Class<InvitationPreview> sourceClass() {
      return InvitationPreview.class;
   }

   @Override
   public Class<InviteTemplate> targetClass() {
      return InviteTemplate.class;
   }

   @Override
   public InviteTemplate convert(MapperyContext mapperyContext, InvitationPreview invitationTemplate) {
      return new InviteTemplate(invitationTemplate.id(), invitationTemplate.title(),
            invitationTemplate.coverImage().url(), invitationTemplate.video(),
            invitationTemplate.link(), invitationTemplate.locale(), invitationTemplate.content());
   }
}
