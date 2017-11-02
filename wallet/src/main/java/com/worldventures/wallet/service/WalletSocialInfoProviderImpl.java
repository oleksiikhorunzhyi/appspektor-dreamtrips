package com.worldventures.wallet.service;

import android.support.annotation.Nullable;

import com.worldventures.core.model.User;
import com.worldventures.core.model.session.SessionHolder;

public class WalletSocialInfoProviderImpl implements WalletSocialInfoProvider {

   private final SessionHolder sessionHolder;

   public WalletSocialInfoProviderImpl(SessionHolder sessionHolder) {
      this.sessionHolder = sessionHolder;
   }

   @Override
   public boolean hasUser() {
      return sessionHolder.get().isPresent();
   }

   @Override
   public Integer userId() {
      return hasUser() ? sessionHolder.get().get().user().getId() : 0;
   }

   @Nullable
   @Override
   public String firstName() {
      return hasUser() ? sessionHolder.get().get().user().getFirstName() : null;
   }

   @Nullable
   @Override
   public String lastName() {
      return hasUser() ? sessionHolder.get().get().user().getLastName() : null;
   }

   @Nullable
   @Override
   public String fullName() {
      return hasUser() ? sessionHolder.get().get().user().getFullName() : null;
   }

   @Nullable
   @Override
   public String username() {
      return hasUser() ? sessionHolder.get().get().user().getUsername() : null;
   }

   @Nullable
   @Override
   public String apiToken() {
      return hasUser() ? sessionHolder.get().get().apiToken() : null;
   }

   @Nullable
   @Override
   public String photoThumb() {
      if (!hasUser()) {
         return null;
      }
      final User.Avatar avatar = sessionHolder.get().get().user().getAvatar();
      return avatar != null ? avatar.getThumb() : null;
   }

   @Nullable
   @Override
   public io.techery.janet.smartcard.model.User.MemberStatus memberStatus() {
      final User user = sessionHolder.get().get().user();
      if (!hasUser()) {
         return null;
      }
      if (user.isGold()) {
         return io.techery.janet.smartcard.model.User.MemberStatus.GOLD;
      }
      if (user.isGeneral() || user.isPlatinum()) {
         return io.techery.janet.smartcard.model.User.MemberStatus.ACTIVE;
      }
      return io.techery.janet.smartcard.model.User.MemberStatus.INACTIVE;
   }
}
