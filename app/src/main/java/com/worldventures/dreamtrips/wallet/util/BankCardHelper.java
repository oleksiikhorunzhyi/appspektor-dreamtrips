package com.worldventures.dreamtrips.wallet.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
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
         default:
            throw new IllegalStateException("Incorrect Financial Service");
      }
   }

   public String financialServiceWithCardNumber(BankCard bankCard) {
      return format("%s •••• %s", obtainFinancialServiceType(bankCard.type()), obtainLastCardDigits(bankCard.number()));
   }

   public String bankNameWithCardNumber(BankCard bankCard) {
      // // TODO: 9/13/16 bank name instead of ServiceType
      return format("%s •••• %s", obtainFinancialServiceType(bankCard.type()), obtainLastCardDigits(bankCard.number()));
   }

   public CharSequence formattedBankNameWithCardNumber(BankCard bankCard) {
      // TODO: 9/13/16 replace mock Bank Name
      SpannableString bankName = new SpannableString("Bank Name");
      bankName.setSpan(new RelativeSizeSpan(1.2f), 0, bankName.length(), 0);
      bankName.setSpan(new StyleSpan(Typeface.BOLD), 0, bankName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

      SpannableString cardNumber = new SpannableString(format(" •••• %s", obtainLastCardDigits(bankCard.number())));
      cardNumber.setSpan(new RelativeSizeSpan(0.8f), 0, cardNumber.length(), 0);

      return new SpannableStringBuilder()
            .append(bankName)
            .append(cardNumber);
   }
}
