package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo

class TestAddressInfo(
      private val address1: String = "test address 1",
      private val address2: String = "test address 2",
      private val city: String = "test city",
      private val state: String = "test state",
      private val zip: String = "test zip"): AddressInfo() {

   override fun address1() = address1

   override fun address2() = address2

   override fun city() = city

   override fun state() = state

   override fun zip() = zip
}