package com.worldventures.dreamtrips.social.domain.converter


import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.api.invitation.model.InvitationTemplate
import com.worldventures.dreamtrips.social.domain.entity.InviteTemplate

import io.techery.mappery.MapperyContext

class InviteTemplateConverter : Converter<InvitationTemplate, InviteTemplate> {

   override fun sourceClass() = InvitationTemplate::class.java

   override fun targetClass() = InviteTemplate::class.java

   override fun convert(context: MapperyContext, source: InvitationTemplate) =
         InviteTemplate(source.id(), source.title(), source.coverImage().url(), source.content(), source.category())
}
