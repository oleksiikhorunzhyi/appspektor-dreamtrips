package com.worldventures.dreamtrips.modules.trips.model

import java.io.Serializable

class Price(val amount: Double = 0.0, val currency: String = USD) : Serializable {

   private val currencySymbol: String
      get() {
         return currency.let { if (it == USD) "$" else it }
      }

   override fun toString() = currencySymbol + String.format("%.02f", amount)

   companion object {
      val USD = "USD"
   }
}
