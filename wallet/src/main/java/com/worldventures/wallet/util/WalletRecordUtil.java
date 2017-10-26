package com.worldventures.wallet.util;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.utils.ProjectTextUtils;
import com.worldventures.wallet.R;
import com.worldventures.wallet.domain.entity.record.FinancialService;
import com.worldventures.wallet.domain.entity.record.Record;
import com.worldventures.wallet.domain.entity.record.RecordType;
import com.worldventures.wallet.ui.records.model.RecordViewModel;

import java.util.List;

import static java.lang.String.format;

public class WalletRecordUtil {

   public WalletRecordUtil() {
   }

   public static String obtainLastCardDigits(String cardNumber) {
      if (cardNumber.length() <= 4) return cardNumber;
      return cardNumber.substring(cardNumber.length() - 4);
   }

   public static long obtainIin(String swipedCardPan) {
      return Long.parseLong(swipedCardPan.substring(0, 6));
   }

   public static int obtainRequiredCvvLength(String cardNumber) {
      return isAmexBank(cardNumber) ? 4 : 3;
   }

   /*
      If card number begins with 34 or 37 and is 15 digits in length
      then it is an American express and should have 4 digits of cvv.
    */
   public static boolean isAmexBank(String cardNumber) {
      boolean amexPrefix = cardNumber.startsWith("34") || cardNumber.startsWith("37");
      return cardNumber.length() == 15 && amexPrefix;
   }

   public String obtainFinancialServiceType(Context context, FinancialService financialService) {
      switch (financialService) {
         case VISA:
            return context.getString(R.string.wallet_card_financial_service_visa);
         case MASTERCARD:
            return context.getString(R.string.wallet_card_financial_service_master_card);
         case DISCOVER:
            return context.getString(R.string.wallet_card_financial_service_discover);
         case AMEX:
            return context.getString(R.string.wallet_card_financial_service_amex);
         case GENERIC: {
            return "";
         }
         default:
            throw new IllegalStateException("Incorrect Financial Service");
      }
   }

   public String financialServiceWithCardNumber(Context context, Record record) {
      return format("%s •••• %s", obtainFinancialServiceType(context, record.financialService()), record.numberLastFourDigits());
   }

   public static String bankNameWithCardNumber(Record record) {
      String bankName = record.bankName();
      bankName = (bankName == null) ? "" : bankName;
      return format("%s •••• %s", bankName, record.numberLastFourDigits());
   }

   // utils
   public String obtainRecordType(Context context, RecordType cardType) {
      if (cardType == null) {
         return null;
      }
      switch (cardType) {
         case CREDIT:
            return context.getString(R.string.wallet_record_type_credit);
         case DEBIT:
            return context.getString(R.string.wallet_record_type_debit);
         default:
            return context.getString(R.string.wallet_empty);
      }
   }

   public CharSequence obtainFullCardNumber(String numberLastFourDigits) {
      return "•••• •••• •••• " + numberLastFourDigits;
   }

   public CharSequence obtainShortCardNumber(String numberLastFourDigits) {
      SpannableString lastDigits = new SpannableString(numberLastFourDigits);
      lastDigits.setSpan(new RelativeSizeSpan(.6f), 0, lastDigits.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      return new SpannableStringBuilder()
            .append("••••")
            .append(lastDigits);
   }

   public CharSequence toBoldSpannable(CharSequence text) {
      if (TextUtils.isEmpty(text)) return "";
      SpannableString boldSpannable = new SpannableString(text);
      boldSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, boldSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      return boldSpannable;
   }

   @SuppressWarnings("unused")
   public CharSequence formattedBankNameWithCardNumber(Record record) {
      CharSequence bankName = toBoldSpannable(record.bankName());

      SpannableString cardNumber = new SpannableString(format(" •••• %s", record.numberLastFourDigits()));
      cardNumber.setSpan(new RelativeSizeSpan(0.8f), 0, cardNumber.length(), 0);

      if (TextUtils.isEmpty(bankName)) return cardNumber;

      return new SpannableStringBuilder()
            .append(bankName)
            .append(cardNumber);
   }

   @SuppressWarnings("unused")
   public int obtainFinancialServiceImageRes(io.techery.janet.smartcard.model.Record.FinancialService financialService) {
      if (financialService == null) {
         return 0;
      }
      switch (financialService) {
         case VISA:
            return R.drawable.wallet_finansial_service_visa;
         case MASTERCARD:
            return R.drawable.wallet_finansial_service_mastercard;
         case DISCOVER:
            return R.drawable.wallet_finansial_service_discover;
         case AMEX:
            return R.drawable.wallet_finansial_service_amex;
         case GENERIC:
            return 0;
         default:
            throw new IllegalStateException("Incorrect Financial Service");
      }
   }

   public static boolean isRealRecord(@Nullable Record record) {
      return record != null && isRealRecordId(record.id());
   }

   public static boolean isRealRecordId(@Nullable String recordId) {
      return recordId != null;
   }

   @Deprecated
   public static boolean equals(String recordId, Record record) {
      return equalsRecordId(recordId, record);
   }

   public static boolean equalsRecordId(String recordId, Record record) {
      return (recordId != null && record != null) && equalsRecordId(recordId, record.id());
   }

   public static boolean equalsRecordId(@NonNull String recordId1, @Nullable String recordId2) {
      return recordId2 != null && recordId2.equals(recordId1);
   }

   public static Record findRecord(List<Record> records, String recordId) throws IllegalStateException {
      return Queryable.from(records).first(card -> equalsRecordId(recordId, card.id()));
   }

   public static String fetchFullName(Record card) {
      return String.format("%s %s", card.cardHolderFirstName(),
            (ProjectTextUtils.isEmpty(card.cardHolderMiddleName()) ?
                  card.cardHolderLastName() :
                  String.format("%s %s", card.cardHolderMiddleName(), card.cardHolderLastName()))
      );
   }

   public static boolean validationMandatoryFields(String number, String cvv) {
      return cvv.length() == WalletRecordUtil.obtainRequiredCvvLength(number);
   }

   public CharSequence goodThrough(Context context, String date) {
      SpannableString goodThru = new SpannableString(context.getString(R.string.wallet_bank_card_good_thru));
      goodThru.setSpan(new RelativeSizeSpan(.65f), 0, goodThru.length(), 0);
      return new SpannableStringBuilder()
            .append(goodThru)
            .append(" ")
            .append(date);
   }

   public static RecordViewModel prepareRecordViewModel(Record record) {
      final int cvvLength = obtainRequiredCvvLength(record.number());
      final String ownerName = fetchFullName(record);
      return new RecordViewModel(record.id(), cvvLength, record.nickName(), ownerName, record.numberLastFourDigits(),
            record.expDate(), record.recordType());
   }
}
