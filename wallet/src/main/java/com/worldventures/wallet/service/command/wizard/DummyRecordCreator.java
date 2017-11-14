package com.worldventures.wallet.service.command.wizard;

import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.domain.entity.record.ImmutableRecord;
import com.worldventures.wallet.domain.entity.record.Record;

import java.util.Arrays;
import java.util.List;

import static com.worldventures.wallet.domain.entity.record.FinancialService.GENERIC;

public final class DummyRecordCreator {

   private DummyRecordCreator() {
   }

   public static String defaultRecordId() {
      return "0";
   }

   public static List<Record> createRecords(SmartCardUser user, String version) {
      final Record dummyCard1 = ImmutableRecord.builder()
            .id("0")
            .number("9999999999994984")
            .numberLastFourDigits("4984")
            .financialService(GENERIC)
            .expDate("02/19")
            .cvv("748")
            .version(version)
            .track1("B1234567890123445^FLYE/TEST CARD^23045211000000827000000")
            .track2("1234567890123445=230452110000827")
            .nickName("Credit Card")
            .cardHolderLastName(user.lastName())
            .cardHolderMiddleName(user.middleName())
            .cardHolderFirstName(user.firstName())
            .build();

      final Record dummyCard2 = ImmutableRecord.builder()
            .id("1")
            .number("9999999999999274")
            .numberLastFourDigits("9274")
            .expDate("06/21")
            .cvv("582")
            .version(version)
            .track1("B1234567890123445^FLYE/TEST CARD^23045211000000827000000")
            .track2("1234567890123445=230452110000827")
            .financialService(GENERIC)
            .cardHolderLastName(user.lastName())
            .cardHolderMiddleName(user.middleName())
            .cardHolderFirstName(user.firstName())
            .nickName("Credit Card")
            .build();

      return Arrays.asList(dummyCard1, dummyCard2);
   }
}
