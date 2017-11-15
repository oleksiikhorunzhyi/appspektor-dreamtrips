package com.worldventures.dreamtrips.social.domain.entity

data class Contact(val id: String,
                   val name: String,
                   val phone: String,
                   val email: String,
                   val emailIsMain: Boolean = false,
                   val sentInvite: SentInvite? = null,
                   var selected: Boolean = false)
