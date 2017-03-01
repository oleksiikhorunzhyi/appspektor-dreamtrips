package com.worldventures.dreamtrips.wallet.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.FinancialService;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import io.techery.janet.smartcard.model.Record;

import static java.lang.String.format;

public class BankCardHelper {

   private final Context context;

   public BankCardHelper(@ForApplication Context appContext) {
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

   public String financialServiceWithCardNumber(BankCard bankCard) {
      return format("%s •••• %s", obtainFinancialServiceType(bankCard.issuerInfo()
            .financialService()), bankCard.numberLastFourDigits());
   }

   public String bankNameWithCardNumber(BankCard bankCard) {
      String bankName = bankCard.issuerInfo().bankName();
      bankName = (bankName == null) ? "" : bankName;
      return format("%s •••• %s", bankName, bankCard.numberLastFourDigits());
   }

   // utils
   public String obtainCardType(BankCard.CardType cardType) {
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
   public CharSequence formattedBankNameWithCardNumber(BankCard bankCard) {
      CharSequence bankName = toBoldSpannable(bankCard.issuerInfo().bankName());

      SpannableString cardNumber = new SpannableString(format(" •••• %s", bankCard.numberLastFourDigits()));
      cardNumber.setSpan(new RelativeSizeSpan(0.8f), 0, cardNumber.length(), 0);

      if (TextUtils.isEmpty(bankName)) return cardNumber;

      return new SpannableStringBuilder()
            .append(bankName)
            .append(cardNumber);
   }

   @SuppressWarnings("unused")
   public int obtainFinancialServiceImageRes(Record.FinancialService financialService) {
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
}
