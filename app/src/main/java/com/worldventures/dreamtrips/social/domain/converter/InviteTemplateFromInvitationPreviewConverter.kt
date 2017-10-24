package com.worldventures.dreamtrips.social.domain.converter


import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.invitation.model.InvitationPreview
import com.worldventures.dreamtrips.social.domain.entity.InviteTemplate

import io.techery.mappery.MapperyContext

class InviteTemplateFromInvitationPreviewConverter : Converter<InvitationPreview, InviteTemplate> {

   override fun sourceClass() = InvitationPreview::class.java

   override fun targetClass() = InviteTemplate::class.java

   override fun convert(context: MapperyContext, source: InvitationPreview) =
         InviteTemplate(source.id(), source.title(), source.coverImage().url(), source.content(), null, source.link())
}
