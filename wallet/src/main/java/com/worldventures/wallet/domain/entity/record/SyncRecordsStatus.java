package com.worldventures.wallet.domain.entity.record;

public enum SyncRecordsStatus {
   SUCCESS, FAIL_AFTER_PROVISION;

   public boolean isSuccess() {
      return this == SUCCESS;
   }

   public boolean isFailAfterProvision() {
      return this == FAIL_AFTER_PROVISION;
   }
}
