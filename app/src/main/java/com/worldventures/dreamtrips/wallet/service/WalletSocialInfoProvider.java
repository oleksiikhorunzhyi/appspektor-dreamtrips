package com.worldventures.dreamtrips.wallet.service;

import android.support.annotation.Nullable;

import io.techery.janet.smartcard.model.User;

public interface WalletSocialInfoProvider {

   boolean hasUser();

   Integer userId();

   @Nullable
   String firstName();

   @Nullable
   String lastName();

   @Nullable
   String fullName();

   @Nullable
   String username();

   @Nullable
   String apiToken();

   @Nullable
   String photoThumb();

   @Nullable
   User.MemberStatus memberStatus();
}
