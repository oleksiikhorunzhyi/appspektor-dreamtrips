package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.settings.customer_support.Contact;
import com.worldventures.dreamtrips.wallet.domain.entity.settings.customer_support.ImmutableContact;

import io.techery.mappery.MapperyContext;

public class CustomerSupportContactConverter implements Converter<com.worldventures.dreamtrips.api.smart_card.documents.customer_support.model.Contact, Contact> {

   @Override
   public Class<com.worldventures.dreamtrips.api.smart_card.documents.customer_support.model.Contact> sourceClass() {
      return com.worldventures.dreamtrips.api.smart_card.documents.customer_support.model.Contact.class;
   }

   @Override
   public Class<Contact> targetClass() {
      return Contact.class;
   }

   @Override
   public Contact convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.smart_card.documents.customer_support.model.Contact contact) {
      return ImmutableContact.builder()
            .contactAddress(contact.contactAddress())
            .email(contact.email())
            .fax(contact.fax())
            .formattedAddress(contact.formattedAddress())
            .phone(contact.phone())
            .build();
   }
}