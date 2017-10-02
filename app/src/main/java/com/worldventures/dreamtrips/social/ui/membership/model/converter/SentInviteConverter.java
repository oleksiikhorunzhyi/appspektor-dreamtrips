package com.worldventures.dreamtrips.social.ui.membership.model.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.invitation.model.Invitation;
import com.worldventures.dreamtrips.social.ui.membership.model.SentInvite;

import io.techery.mappery.MapperyContext;

public class SentInviteConverter implements Converter<Invitation, SentInvite> {

   @Override
   public Class<Invitation> sourceClass() {
      return Invitation.class;
   }

   @Override
   public Class<SentInvite> targetClass() {
      return SentInvite.class;
   }

   @Override
   public SentInvite convert(MapperyContext mapperyContext, Invitation invitation) {
      return new SentInvite(invitation.contact(), invitation.date());
   }
}
