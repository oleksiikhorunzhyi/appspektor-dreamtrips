package com.worldventures.wallet.domain

import com.worldventures.wallet.domain.entity.SDKRecord

internal const val SDK_RECORD_METADATA_BANK_NAME = "bank_name"

internal fun SDKRecord.bankName(): String = this.metadata()[SDK_RECORD_METADATA_BANK_NAME] ?: ""
