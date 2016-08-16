package com.worldventures.dreamtrips.wallet.domain.converter;

import android.text.TextUtils;

import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;

import java.util.HashMap;
import java.util.Map;

import io.techery.janet.smartcard.model.ImmutableRecord;
import io.techery.janet.smartcard.model.Record;

public class BankCardConverter implements Converter<Record, BankCard> {

    private final static String ADDRESS1_FIELD = "address1";
    private final static String ADDRESS2_FIELD = "address2";
    private final static String CITY_FIELD = "city";
    private final static String STATE_FIELD = "state";
    private final static String ZIP_FIELD = "zip";

    private final static String TYPE_CARD_FIELD = "type_card";

    @Override
    public BankCard from(Record record) {
        Map<String, String> metadata = record.metadata();
        AddressInfo addressInfo = null;
        if (record.metadata() != null && !TextUtils.isEmpty(record.metadata().get(CITY_FIELD))) {
            addressInfo = ImmutableAddressInfo.builder()
                    .address1(metadata.get(ADDRESS1_FIELD))
                    .address2(metadata.get(ADDRESS2_FIELD))
                    .city(metadata.get(CITY_FIELD))
                    .state(metadata.get(STATE_FIELD))
                    .zip(metadata.get(ZIP_FIELD))
                    .build();
        }

        return ImmutableBankCard.builder()
                .id(String.valueOf(record.id()))
                .title(record.title())
                .cvv(Integer.parseInt(record.cvv()))
                .number(Long.parseLong(record.cardNumber()))
                .type(record.financialService())
                .expiryMonth(record.expiryMonth())
                .expiryYear(record.expiryYear())
                .addressInfo(addressInfo)
                .cardType(BankCard.CardType.valueOf(metadata.get(TYPE_CARD_FIELD)))
                .build();
    }

    @Override
    public Record to(BankCard card) {
        AddressInfo addressInfo = card.addressInfo();
        HashMap<String, String> metadata = new HashMap<>(5);
        metadata.put(ADDRESS1_FIELD, addressInfo.address1());
        metadata.put(ADDRESS2_FIELD, addressInfo.address2());
        metadata.put(CITY_FIELD, addressInfo.city());
        metadata.put(STATE_FIELD, addressInfo.state());
        metadata.put(ZIP_FIELD, addressInfo.zip());

        metadata.put(TYPE_CARD_FIELD, card.cardType().name());

        // TODO: use normal id
        return ImmutableRecord.builder()
                .id(Integer.parseInt(card.id()))
                .title(card.title())
                .cardNumber(String.valueOf(card.number()))
                .cvv(String.valueOf(card.cvv()))
                .expiryMonth(card.expiryMonth())
                .expiryYear(card.expiryYear())
                .financialService(Record.FinancialService.MASTERCARD)
                .t1("")
                .t2("")
                .t3("")
                .metadata(metadata)
                .build();
    }
}