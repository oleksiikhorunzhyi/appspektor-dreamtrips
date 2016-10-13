package com.worldventures.dreamtrips.modules.membership.model.converter;


import com.worldventures.dreamtrips.api.invitation.model.InvitationTemplate;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;

import io.techery.mappery.MapperyContext;

public class InviteTemplateConverter implements Converter<InvitationTemplate, InviteTemplate> {
   @Override
   public Class<InvitationTemplate> sourceClass() {
      return InvitationTemplate.class;
   }

   @Override
   public Class<InviteTemplate> targetClass() {
      return InviteTemplate.class;
   }

   @Override
   public InviteTemplate convert(MapperyContext mapperyContext, InvitationTemplate invitationTemplate) {
      InviteTemplate.Type type;
      switch (invitationTemplate.type()) {
         case SMS:
            type = InviteTemplate.Type.SMS;
            break;
         case EMAIL:
         case UNKNOWN:
         default:
            type = InviteTemplate.Type.EMAIL;
      }
      return new InviteTemplate(invitationTemplate.id(), invitationTemplate.title(), type,
            invitationTemplate.category(), invitationTemplate.coverImage().url(), invitationTemplate.video(),
            invitationTemplate.locale(), invitationTemplate.content());
   }
}
