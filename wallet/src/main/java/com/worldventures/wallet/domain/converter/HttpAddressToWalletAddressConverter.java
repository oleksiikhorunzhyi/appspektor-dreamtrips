package com.worldventures.wallet.domain.converter;


import android.support.annotation.NonNull;

import com.innahema.collections.query.functions.Function2;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.converter.Converter;
import com.worldventures.wallet.domain.entity.lostcard.ImmutableWalletAddress;
import com.worldventures.wallet.domain.entity.lostcard.WalletAddress;
import com.worldventures.wallet.service.lostcard.command.http.model.AddressRestResponse;

import java.util.ArrayList;
import java.util.List;

import io.techery.mappery.MapperyContext;

public class HttpAddressToWalletAddressConverter implements Converter<AddressRestResponse, WalletAddress> {

   private static final String STREET_NUMBER = "street_number";
   private static final String ROUTE = "route";
   private static final String COUNTRY = "country";
   private static final String ADMINISTRATIVE_AREA_LEVEL_1 = "administrative_area_level_1";
   private static final String POSTAL_CODE = "postal_code";

   @Override
   public Class<AddressRestResponse> sourceClass() {
      return AddressRestResponse.class;
   }

   @Override
   public Class<WalletAddress> targetClass() {
      return WalletAddress.class;
   }

   @Override
   public WalletAddress convert(MapperyContext mapperyContext, AddressRestResponse addressRestResponse) {
      if (isResponseInvalid(addressRestResponse)) {
         return null;
      }

      ArrayList<AddressRestResponse.AddComponent> addComponents = getListOfAddressComponents(addressRestResponse);

      return ImmutableWalletAddress.builder()
            .addressLine(getAddress(addComponents))
            .countryName(getFieldFromAddressResponse(addComponents, COUNTRY))
            .adminArea(getFieldFromAddressResponse(addComponents, ADMINISTRATIVE_AREA_LEVEL_1))
            .postalCode(getFieldFromAddressResponse(addComponents, POSTAL_CODE))
            .build();
   }

   private ArrayList<AddressRestResponse.AddComponent> getListOfAddressComponents(AddressRestResponse addressRestResponse) {
      return Queryable.from(addressRestResponse.results())
            .fold(new ArrayList<>(),
                  new Function2<ArrayList<AddressRestResponse.AddComponent>,
                        AddressRestResponse.AddressComponents,
                        ArrayList<AddressRestResponse.AddComponent>>() {
                     @Override
                     public ArrayList<AddressRestResponse.AddComponent> apply(
                           ArrayList<AddressRestResponse.AddComponent> addComponents,
                           AddressRestResponse.AddressComponents addressComponents) {
                        addComponents.addAll(addressComponents.components());
                        return addComponents;
                     }
                  });
   }

   private boolean isResponseInvalid(AddressRestResponse addressRestResponse) {
      return addressRestResponse.status() == null
            || (addressRestResponse.status() != null && !addressRestResponse.status().equals("OK"))
            || (addressRestResponse.results() == null)
            || (addressRestResponse.results().isEmpty());
   }


   @NonNull
   private String getAddress(ArrayList<AddressRestResponse.AddComponent> addComponents) {
      String number = getFieldFromAddressResponse(addComponents, STREET_NUMBER);
      String street = getFieldFromAddressResponse(addComponents, ROUTE);
      StringBuilder addressBuilder = new StringBuilder();
      if (number != null) {
         addressBuilder.append(number).append(", ");
      }
      addressBuilder.append(street);
      return addressBuilder.toString();
   }

   private String getFieldFromAddressResponse(List<AddressRestResponse.AddComponent> components,
         final String fieldName) {
      return Queryable.from(components)
            .filter(element -> element.types().contains(fieldName))
            .map(element -> element != null ? element.longName() : "")
            .firstOrDefault();
   }
}
