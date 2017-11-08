package com.worldventures.wallet.util

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.worldventures.wallet.R
import com.worldventures.wallet.domain.entity.record.FinancialService
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.domain.entity.record.RecordType
import com.worldventures.wallet.ui.records.model.RecordViewModel
import java.lang.String.format

class WalletRecordUtil {

   private fun obtainFinancialServiceType(context: Context, financialService: FinancialService): String {
      return when (financialService) {
         FinancialService.VISA -> context.getString(R.string.wallet_card_financial_service_visa)
         FinancialService.MASTERCARD -> context.getString(R.string.wallet_card_financial_service_master_card)
         FinancialService.DISCOVER -> context.getString(R.string.wallet_card_financial_service_discover)
         FinancialService.AMEX -> context.getString(R.string.wallet_card_financial_service_amex)
         else -> ""
      }
   }

   fun financialServiceWithCardNumber(context: Context, record: Record): String {
      return format("%s •••• %s", obtainFinancialServiceType(context, record.financialService), record.numberLastFourDigits)
   }

   // utils
   fun obtainRecordType(context: Context, cardType: RecordType?): String? {
      if (cardType == null) {
         return null
      }
      return when (cardType) {
         RecordType.CREDIT -> context.getString(R.string.wallet_record_type_credit)
         RecordType.DEBIT -> context.getString(R.string.wallet_record_type_debit)
         else -> context.getString(R.string.wallet_empty)
      }
   }

   fun obtainFullCardNumber(numberLastFourDigits: String): CharSequence {
      return "•••• •••• •••• " + numberLastFourDigits
   }

   fun obtainShortCardNumber(numberLastFourDigits: String): CharSequence {
      val lastDigits = SpannableString(numberLastFourDigits)
      lastDigits.setSpan(RelativeSizeSpan(.6f), 0, lastDigits.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
      return SpannableStringBuilder()
            .append("••••")
            .append(lastDigits)
   }

   fun toBoldSpannable(text: CharSequence): CharSequence {
      if (TextUtils.isEmpty(text)) {
         return ""
      }
      val boldSpannable = SpannableString(text)
      boldSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, boldSpannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
      return boldSpannable
   }

   fun formattedBankNameWithCardNumber(record: Record): CharSequence {
      val bankName = toBoldSpannable(record.bankName)

      val cardNumber = SpannableString(format(" •••• %s", record.numberLastFourDigits))
      cardNumber.setSpan(RelativeSizeSpan(0.8f), 0, cardNumber.length, 0)

      return if (bankName.isEmpty()) {
         cardNumber
      } else SpannableStringBuilder()
            .append(bankName)
            .append(cardNumber)

   }

   fun goodThrough(context: Context, date: String): CharSequence {
      val goodThru = SpannableString(context.getString(R.string.wallet_bank_card_good_thru))
      goodThru.setSpan(RelativeSizeSpan(.65f), 0, goodThru.length, 0)
      return SpannableStringBuilder()
            .append(goodThru)
            .append(" ")
            .append(date)
   }

   companion object {

      fun obtainLastCardDigits(cardNumber: String): String {
         return if (cardNumber.length <= 4) {
            cardNumber
         } else cardNumber.substring(cardNumber.length - 4)
      }

      fun obtainIin(swipedCardPan: String): Long {
         return java.lang.Long.parseLong(swipedCardPan.substring(0, 6))
      }

      fun obtainRequiredCvvLength(cardNumber: String): Int {
         return if (isAmexBank(cardNumber)) 4 else 3
      }

      /*
      If card number begins with 34 or 37 and is 15 digits in length
      then it is an American express and should have 4 digits of cvv.
    */
      fun isAmexBank(cardNumber: String): Boolean {
         val amexPrefix = cardNumber.startsWith("34") || cardNumber.startsWith("37")
         return cardNumber.length == 15 && amexPrefix
      }

      fun bankNameWithCardNumber(record: Record) = format("%s •••• %s", record.bankName, record.numberLastFourDigits)!!

      fun isRealRecord(record: Record?): Boolean {
         return record != null && isRealRecordId(record.id)
      }

      private fun isRealRecordId(recordId: String?): Boolean {
         return recordId != null
      }

      @Deprecated("")
      fun equals(recordId: String, record: Record): Boolean {
         return equalsRecordId(recordId, record)
      }

      fun equalsRecordId(recordId: String?, record: Record?): Boolean {
         return recordId != null && record != null && equalsRecordId(recordId, record.id)
      }

      fun equalsRecordId(recordId1: String, recordId2: String?): Boolean {
         return recordId2 != null && recordId2 == recordId1
      }

      fun findRecord(records: List<Record>, recordId: String): Record? {
         return records.firstOrNull { (id) -> equalsRecordId(recordId, id) }
      }


      fun fetchFullName(card: Record): String {
         return format("%s %s", card.cardHolderFirstName,
               if (card.cardHolderMiddleName.isEmpty())
                  card.cardHolderLastName
               else
                  format("%s %s", card.cardHolderMiddleName, card.cardHolderLastName)
         )
      }

      fun validationMandatoryFields(number: String, cvv: String): Boolean {
         return cvv.length == WalletRecordUtil.obtainRequiredCvvLength(number)
      }

      fun prepareRecordViewModel(record: Record): RecordViewModel {
         val cvvLength = obtainRequiredCvvLength(record.number)
         val ownerName = fetchFullName(record)
         return RecordViewModel(record.id, cvvLength,
               record.nickname, ownerName, record.numberLastFourDigits,
               record.expDate, record.recordType)
      }
   }
}
