package com.worldventures.dreamtrips.wallet.di.external;

import android.support.annotation.Nullable;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;

class WalletSocialInfoProviderImpl implements WalletSocialInfoProvider {

   private final SessionHolder<UserSession> sessionHolder;

   WalletSocialInfoProviderImpl(SessionHolder<UserSession> sessionHolder) {
      this.sessionHolder = sessionHolder;
   }

   @Override
   public boolean hasUser() {
      return sessionHolder.get().isPresent();
   }

   @Override
   public Integer userId() {
      return hasUser() ? sessionHolder.get().get().getUser().getId() : 0;
   }

   @Nullable
   @Override
   public String firstName() {
      return hasUser() ? sessionHolder.get().get().getUser().getFirstName() : null;
   }

   @Nullable
   @Override
   public String lastName() {
      return hasUser() ? sessionHolder.get().get().getUser().getLastName() : null;
   }

   @Nullable
   @Override
   public String fullName() {
      return hasUser() ? sessionHolder.get().get().getUser().getFullName() : null;
   }

   @Nullable
   @Override
   public String username() {
      return hasUser() ? sessionHolder.get().get().getUser().getUsername() : null;
   }

   @Nullable
   @Override
   public String apiToken() {
      return hasUser() ? sessionHolder.get().get().getApiToken() : null;
   }

   @Nullable
   @Override
   public String photoThumb() {
      if (!hasUser()) return null;
      final User.Avatar avatar = sessionHolder.get().get().getUser().getAvatar();
      return avatar != null ? avatar.getThumb() : null;
   }

   @Nullable
   @Override
   public io.techery.janet.smartcard.model.User.MemberStatus memberStatus() {
      final User user = sessionHolder.get().get().getUser();
      if (!hasUser()) return null;
      if (user.isGold()) return io.techery.janet.smartcard.model.User.MemberStatus.GOLD;
      if (user.isGeneral() || user.isPlatinum()) return io.techery.janet.smartcard.model.User.MemberStatus.ACTIVE;
      return io.techery.janet.smartcard.model.User.MemberStatus.INACTIVE;
   }
}
