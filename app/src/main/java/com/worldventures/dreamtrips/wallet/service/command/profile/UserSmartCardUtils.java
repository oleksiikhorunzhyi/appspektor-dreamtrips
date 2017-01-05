package com.worldventures.dreamtrips.wallet.service.command.profile;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import io.techery.janet.smartcard.model.User;

public class UserSmartCardUtils {

   public static User.MemberStatus obtainMemberStatus(SessionHolder<UserSession> userSessionHolder) {
      com.worldventures.dreamtrips.modules.common.model.User user = userSessionHolder.get().get().getUser();
      if (user.isGold()) return User.MemberStatus.GOLD;
      if (user.isGeneral() || user.isPlatinum()) return User.MemberStatus.ACTIVE;
      return User.MemberStatus.INACTIVE;
   }
}
