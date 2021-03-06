package com.worldventures.wallet.domain.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.CardUserPhone;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableCardUserPhone;
import com.worldventures.wallet.domain.entity.SmartCardUserPhone;

import io.techery.mappery.MapperyContext;

public class SmartCardUserPhoneToCardUserPhoneConverter implements Converter<SmartCardUserPhone, CardUserPhone> {

   @Override
   public CardUserPhone convert(MapperyContext mapperyContext, SmartCardUserPhone smartCardUserPhone) {
      return ImmutableCardUserPhone.builder()
            .code(smartCardUserPhone.getCode())
            .number(smartCardUserPhone.getNumber())
            .build();
   }

   @Override
   public Class<SmartCardUserPhone> sourceClass() {
      return SmartCardUserPhone.class;
   }

   @Override
   public Class<CardUserPhone> targetClass() {
      return CardUserPhone.class;
   }
}
