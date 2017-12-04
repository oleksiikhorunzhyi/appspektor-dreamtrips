package com.worldventures.wallet.domain.entity.record

enum class SyncRecordsStatus {
   SUCCESS, FAIL_AFTER_PROVISION;

   val isSuccess: Boolean
      get() = this == SUCCESS

   val isFailAfterProvision: Boolean
      get() = this == FAIL_AFTER_PROVISION
}
