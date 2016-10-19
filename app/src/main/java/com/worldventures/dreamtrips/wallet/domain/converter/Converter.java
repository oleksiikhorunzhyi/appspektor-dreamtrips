package com.worldventures.dreamtrips.wallet.domain.converter;

public interface Converter<T, R> {
   String ADDRESS1_FIELD = "address1";
   String ADDRESS2_FIELD = "address2";
   String CITY_FIELD = "city";
   String STATE_FIELD = "state";
   String ZIP_FIELD = "zip";

   String TYPE_CARD_FIELD = "type_card";
   String BANK_NAME_FIELD = "bank_name";
   String BANK_CARD_CATEGORY = "bank_card_category";

   R from(T object);

   T to(R object);
}

