package com.worldventures.wallet.domain.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.CardUserPhone;
import com.worldventures.wallet.domain.entity.SmartCardUserPhone;

import io.techery.mappery.MapperyContext;

public class CardUserPhoneToSmartCardUserPhoneConverter implements Converter<CardUserPhone, SmartCardUserPhone> {

   @Override
   public SmartCardUserPhone convert(MapperyContext mapperyContext, CardUserPhone cardUserPhone) {
      return new SmartCardUserPhone(cardUserPhone.code(), cardUserPhone.number());
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
