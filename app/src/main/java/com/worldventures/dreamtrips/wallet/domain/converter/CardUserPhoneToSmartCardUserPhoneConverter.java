package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.api.smart_card.user_info.model.CardUserPhone;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUserPhone;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;

import io.techery.mappery.MapperyContext;

public class CardUserPhoneToSmartCardUserPhoneConverter implements Converter<CardUserPhone, SmartCardUserPhone> {

   @Override
   public SmartCardUserPhone convert(MapperyContext mapperyContext, CardUserPhone cardUserPhone) {
      return ImmutableSmartCardUserPhone.of(cardUserPhone.number(), cardUserPhone.code());
   }

   @Override
   public Class<CardUserPhone> sourceClass() {
      return CardUserPhone.class;
   }

   @Override
   public Class<SmartCardUserPhone> targetClass() {
      return SmartCardUserPhone.class;
   }
}
