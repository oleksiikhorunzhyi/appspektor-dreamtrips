package com.worldventures.dreamtrips.social.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.invitation.model.Invitation
import com.worldventures.dreamtrips.social.domain.entity.SentInvite

import io.techery.mappery.MapperyContext

class SentInviteConverter : Converter<Invitation, SentInvite> {

   override fun sourceClass() = Invitation::class.java

   override fun targetClass() = SentInvite::class.java

   override fun convert(context: MapperyContext, source: Invitation) = SentInvite(source.contact(), source.date())
}
