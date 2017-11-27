package com.worldventures.wallet.domain.entity.settings.customer_support

data class Contact(val email: String = "",
                   val phone: String = "",
                   val fax: String = "",
                   val contactAddress: String? = null,
                   val formattedAddress: String? = null)
