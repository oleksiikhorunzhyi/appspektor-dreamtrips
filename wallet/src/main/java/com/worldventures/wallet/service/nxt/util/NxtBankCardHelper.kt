package com.worldventures.wallet.service.nxt.util

import android.util.Base64
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.nxt.model.ImmutableMultiRequestElement
import com.worldventures.wallet.service.nxt.model.MultiErrorResponse
import com.worldventures.wallet.service.nxt.model.MultiRequestElement
import java.util.ArrayList

object NxtBankCardHelper {

   val PAN = "number"
   val CVV = "cvv"
   val TRACK_1 = "track1"
   val TRACK_2 = "track2"
   val TRACK_3 = "track3"


   private val OPERATION_TOKENIZE = "tokenize"
   private val OPERATION_DETOKENIZE = "detokenize"

   private val TOKEN_NAME_GENERIC = "smartcardgeneric"

   @JvmOverloads
   fun getDataForTokenization(record: Record, refIdPrefix: String? = record.id): List<MultiRequestElement> {
      val elements = ArrayList<MultiRequestElement>()

      elements.add(ImmutableMultiRequestElement.builder()
            .operation(OPERATION_TOKENIZE).tokenName(TOKEN_NAME_GENERIC)
            .value(record.number).referenceId(prefixRefId(PAN, refIdPrefix))
            .build())
      elements.add(ImmutableMultiRequestElement.builder()
            .operation(OPERATION_TOKENIZE).tokenName(TOKEN_NAME_GENERIC)
            .value(record.cvv).referenceId(prefixRefId(CVV, refIdPrefix))
            .build())

      safelyAddEncodedElement(elements, OPERATION_TOKENIZE, TOKEN_NAME_GENERIC, record.track1, prefixRefId(TRACK_1, refIdPrefix))
      safelyAddEncodedElement(elements, OPERATION_TOKENIZE, TOKEN_NAME_GENERIC, record.track2, prefixRefId(TRACK_2, refIdPrefix))
      safelyAddEncodedElement(elements, OPERATION_TOKENIZE, TOKEN_NAME_GENERIC, record.track3, prefixRefId(TRACK_3, refIdPrefix))

      return elements
   }

   @JvmOverloads
   fun getDataForDetokenization(record: Record, refIdPrefix: String? = record.id): List<MultiRequestElement> {
      val elements = ArrayList<MultiRequestElement>()

      elements.add(ImmutableMultiRequestElement.builder()
            .operation(OPERATION_DETOKENIZE).tokenName(TOKEN_NAME_GENERIC)
            .value(record.number).referenceId(prefixRefId(PAN, refIdPrefix))
            .build())
      elements.add(ImmutableMultiRequestElement.builder()
            .operation(OPERATION_DETOKENIZE).tokenName(TOKEN_NAME_GENERIC)
            .value(record.cvv).referenceId(prefixRefId(CVV, refIdPrefix))
            .build())

      safelyAddElement(elements, OPERATION_DETOKENIZE, TOKEN_NAME_GENERIC, record.track1, prefixRefId(TRACK_1, refIdPrefix))
      safelyAddElement(elements, OPERATION_DETOKENIZE, TOKEN_NAME_GENERIC, record.track2, prefixRefId(TRACK_2, refIdPrefix))
      safelyAddElement(elements, OPERATION_DETOKENIZE, TOKEN_NAME_GENERIC, record.track3, prefixRefId(TRACK_3, refIdPrefix))

      return elements
   }

   fun getTokenizedRecord(card: TokenizedRecord, refIdPrefix: String?): Record {
      return card.record.copy(
            // Save tokenized track values into original values
            number = card.nxtValues[prefixRefId(PAN, refIdPrefix)]!!,
            cvv = card.nxtValues[prefixRefId(CVV, refIdPrefix)]!!,
            track1 = card.nxtValues[prefixRefId(TRACK_1, refIdPrefix)],
            track2 = card.nxtValues[prefixRefId(TRACK_2, refIdPrefix)],
            track3 = card.nxtValues[prefixRefId(TRACK_3, refIdPrefix)])
   }

   fun getDetokenizedRecord(card: DetokenizedRecord, refIdPrefix: String?): Record {
      return card.record.copy(
            number = card.nxtValues[prefixRefId(PAN, refIdPrefix)]!!,
            cvv = card.nxtValues[prefixRefId(CVV, refIdPrefix)]!!,
            track1 = getDecodedElement(card.nxtValues[prefixRefId(TRACK_1, refIdPrefix)]),
            track2 = getDecodedElement(card.nxtValues[prefixRefId(TRACK_2, refIdPrefix)]),
            track3 = getDecodedElement(card.nxtValues[prefixRefId(TRACK_3, refIdPrefix)]))
   }

   private fun getDecodedElement(value: String?): String? {
      return if (!value.isNullOrEmpty()) {
         String(Base64.decode(value!!.toByteArray(), Base64.DEFAULT))
      } else null
   }

   private fun safelyAddElement(list: MutableList<MultiRequestElement>,
                                operation: String, tokenName: String, value: String?, refId: String) {
      if (!value.isNullOrEmpty()) {
         list.add(ImmutableMultiRequestElement.builder()
               .operation(operation).tokenName(tokenName)
               .value(value!!).referenceId(refId)
               .build())
      }
   }

   private fun safelyAddEncodedElement(list: MutableList<MultiRequestElement>,
                                       operation: String, tokenName: String, value: String?, refId: String) {
      if (!value.isNullOrEmpty()) {
         val encodedValue = Base64.encode(value!!.toByteArray(), Base64.DEFAULT)
         list.add(ImmutableMultiRequestElement.builder()
               .operation(operation).tokenName(tokenName)
               .value(String(encodedValue)).referenceId(refId)
               .build())
      }
   }

   fun prefixRefId(refId: String, prefix: String?)
         = if (prefix == null) refId else String.format("%s_%s", prefix, refId)

   fun getResponseErrors(response: NxtRecordResponse, refIdPrefix: String?): List<MultiErrorResponse> =
         arrayOf(PAN, CVV, TRACK_1, TRACK_2, TRACK_3)
               .mapNotNull { refId -> response.nxtErrors[prefixRefId(refId, refIdPrefix)] }

   fun getResponseErrorMessage(errorResponseList: List<MultiErrorResponse>?): String? {
      if (errorResponseList == null || errorResponseList.isEmpty()) {
         return null
      }

      val sb = StringBuilder("[")
      for (i in errorResponseList.indices) {
         if (i > 0) {
            sb.append(", ")
         }
         val errorResponse = errorResponseList[i]
         sb.append(String.format("\"%s\" : \"%s\"", errorResponse.code(), errorResponse.message()))
      }
      sb.append(']')

      return sb.toString()
   }

}
