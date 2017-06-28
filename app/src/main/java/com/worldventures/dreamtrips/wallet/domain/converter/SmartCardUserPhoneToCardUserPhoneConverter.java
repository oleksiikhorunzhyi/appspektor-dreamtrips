package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.api.smart_card.user_info.model.CardUserPhone;
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableCardUserPhone;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;

import io.techery.mappery.MapperyContext;

public class SmartCardUserPhoneToCardUserPhoneConverter implements Converter<SmartCardUserPhone, CardUserPhone> {

   @Override
   public CardUserPhone convert(MapperyContext mapperyContext, SmartCardUserPhone smartCardUserPhone) {
      return ImmutableCardUserPhone.builder()
            .code(smartCardUserPhone.code())
            .number(smartCardUserPhone.number())
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
