package com.worldventures.dreamtrips.wallet.util;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.wallet.domain.entity.record.FinancialService;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.domain.entity.record.RecordType;

import static android.text.TextUtils.getTrimmedLength;
import static java.lang.String.format;

public class WalletRecordUtil {

   private final Context context;

   public WalletRecordUtil(@ForApplication Context appContext) {
      this.context = appContext;
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

   public String obtainFinancialServiceType(FinancialService financialService) {
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

   public String financialServiceWithCardNumber(Record record) {
      return format("%s •••• %s", obtainFinancialServiceType(record.financialService()), record.numberLastFourDigits());
   }

   public String bankNameWithCardNumber(Record record) {
      String bankName = record.bankName();
      bankName = (bankName == null) ? "" : bankName;
      return format("%s •••• %s", bankName, record.numberLastFourDigits());
   }

   // utils
   public String obtainRecordType(RecordType cardType) {
      if (cardType == null) {
         return null;
      }
      switch (cardType) {
         case CREDIT:
            return context.getString(R.string.wallet_record_type_credit);
         case DEBIT:
            return context.getString(R.string.wallet_record_type_debit);
         default:
            return context.getString(R.string.empty);
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

   public static boolean equals(String recordId, Record record) {
      return (recordId != null && record != null) && record.id().equals(recordId);
   }

   public static String fetchFullName(Record card) {
      return String.format("%s %s", card.cardHolderFirstName(),
            (ProjectTextUtils.isEmpty(card.cardHolderMiddleName()) ?
                  card.cardHolderLastName() :
                  String.format("%s %s", card.cardHolderMiddleName(), card.cardHolderLastName()))
      );
   }

   public static boolean validationMandatoryFields(String number, String address1, String city, String zipCode, String state, String cvv) {
      return getTrimmedLength(address1) > 0
            && getTrimmedLength(city) > 0
            && getTrimmedLength(zipCode) > 0
            && getTrimmedLength(state) > 0
            && cvv.length() == WalletRecordUtil.obtainRequiredCvvLength(number);
   }

   public CharSequence goodThrough(String date) {
      SpannableString goodThru = new SpannableString(context.getString(R.string.wallet_bank_card_good_thru));
      goodThru.setSpan(new RelativeSizeSpan(.65f), 0, goodThru.length(), 0);
      return new SpannableStringBuilder()
            .append(goodThru)
            .append(" ")
            .append(date);
   }
}
