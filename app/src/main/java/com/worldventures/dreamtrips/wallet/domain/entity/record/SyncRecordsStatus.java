package com.worldventures.dreamtrips.wallet.domain.entity.record;

public enum SyncRecordsStatus {
   SUCCESS, FAIL, FAIL_AFTER_PROVISION;

   public boolean isSuccess() {
      return this == SUCCESS;
   }

   public boolean isFailAfterProvision() {
      return this == FAIL_AFTER_PROVISION;
   }
}
