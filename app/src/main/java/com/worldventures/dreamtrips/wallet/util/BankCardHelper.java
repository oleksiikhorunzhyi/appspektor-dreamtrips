package com.worldventures.dreamtrips.wallet.util;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.DrawableRes;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.smartcard.model.Record;

import static java.lang.String.format;

@Singleton
public class BankCardHelper {

   private final Context context;

   @Inject
   public BankCardHelper(@ForApplication Context appContext) {
      this.context = appContext;
   }

   public static String obtainLastCardDigits(long cardNumber) {
      String number = Long.toString(cardNumber);
      // TODO: 9/6/16 or throw exception
      if (number.length() <= 4) return number;
      return number.substring(number.length() - 4);
   }

   public static long obtainIin(String swipedCardPan) {
      return Long.parseLong(swipedCardPan.substring(0, 6));
   }

   public static int obtainRequiredCvvLength(long cardNumber) {
      return isAmexBank(cardNumber) ? 4 : 3;
   }

   /*
      If card number begins with 34 or 37 and is 15 digits in length
      then it is an American express and should have 4 digits of cvv.
    */
   public static boolean isAmexBank(long cardNumber) {
      String number = String.valueOf(cardNumber);
      boolean amexPrefix = number.startsWith("34") || number.startsWith("37");
      return number.length() == 15 && amexPrefix;
   }

   public String obtainFinancialServiceType(Record.FinancialService financialService) {
      switch (financialService) {
         case VISA:
            return context.getString(R.string.wallet_card_financial_service_visa);
         case MASTERCARD:
            return context.getString(R.string.wallet_card_financial_service_master_card);
         case DISCOVER:
            return context.getString(R.string.wallet_card_financial_service_discover);
         case AMEX:
            return context.getString(R.string.wallet_card_financial_service_amex);
         case SAMPLE: {
            return context.getString(R.string.wallet_card_financial_service_sample);
         }
         default:
            throw new IllegalStateException("Incorrect Financial Service");
      }
   }

   @DrawableRes
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
         case SAMPLE:
            return 0;
         default:
            throw new IllegalStateException("Incorrect Financial Service");
      }
   }

   public String financialServiceWithCardNumber(BankCard bankCard) {
      return format("%s •••• %s", obtainFinancialServiceType(bankCard.issuerInfo()
            .financialService()), obtainLastCardDigits(bankCard.number()));
   }

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

   public String bankNameWithCardNumber(BankCard bankCard) {
      String bankName = bankCard.issuerInfo().bankName();
      bankName = (bankName == null) ? "" : bankName;
      return format("%s •••• %s", bankName, obtainLastCardDigits(bankCard.number()));
   }

   public CharSequence formattedBankNameWithCardNumber(BankCard bankCard) {
      SpannableString bankName = formattedBankName(bankCard);

      SpannableString cardNumber = new SpannableString(format(" •••• %s", obtainLastCardDigits(bankCard.number())));
      cardNumber.setSpan(new RelativeSizeSpan(0.8f), 0, cardNumber.length(), 0);

      if (TextUtils.isEmpty(bankName)) return cardNumber;

      return new SpannableStringBuilder()
            .append(bankName)
            .append(cardNumber);
   }

   public SpannableString formattedBankName(BankCard bankCard) {
      if (TextUtils.isEmpty(bankCard.issuerInfo().bankName())) return null;
      SpannableString bankName = new SpannableString(bankCard.issuerInfo().bankName());
      bankName.setSpan(new StyleSpan(Typeface.BOLD), 0, bankName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      return bankName;
   }
}