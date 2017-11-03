package com.worldventures.wallet.domain.converter

import com.worldventures.core.converter.Converter
import com.worldventures.wallet.domain.entity.settings.customer_support.Contact
import io.techery.mappery.MapperyContext

typealias ApiContact = com.worldventures.dreamtrips.api.smart_card.documents.customer_support.model.Contact

class CustomerSupportContactConverter : Converter<ApiContact, Contact> {

   override fun sourceClass() = ApiContact::class.java

   override fun targetClass() = Contact::class.java

   override fun convert(context: MapperyContext, source: ApiContact) =
         Contact(
               contactAddress = source.contactAddress(),
               email = source.email(),
               fax = source.fax(),
               formattedAddress = source.formattedAddress(),
               phone = source.phone()
         )
}