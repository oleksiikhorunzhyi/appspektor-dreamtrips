package com.messenger.delegate.user;

import android.text.TextUtils;
import android.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.api.GetShortProfilesCommand;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.storage.dao.UsersDAO;
import com.worldventures.core.model.User;
import com.worldventures.core.utils.BadgeHelper;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.Observable;

class UserDataFetcher {

   private final ActionPipe<GetShortProfilesCommand> shortProfilesPipe;
   private final UsersDAO usersDAO;
   private final BadgeHelper badgeHelper;

   @Inject
   public UserDataFetcher(Janet janet, UsersDAO usersDAO, BadgeHelper badgeHelper) {
      this.usersDAO = usersDAO;
      this.shortProfilesPipe = janet.createPipe(GetShortProfilesCommand.class);
      this.badgeHelper = badgeHelper;
   }

   Observable<List<DataUser>> fetchUserData(List<MessengerUser> messengerUsers) {
      if (messengerUsers.isEmpty()) {
         return Observable.just(Collections.emptyList());
      }

      List<String> userNames = Queryable.from(messengerUsers).map(MessengerUser::getName).toList();

      return shortProfilesPipe.createObservableResult(new GetShortProfilesCommand(userNames))
            .map(action -> composeDataUsers(messengerUsers, action.getResult()));
   }

   private List<DataUser> composeDataUsers(List<MessengerUser> messengerUsers, List<User> socialUsers) {
      return Queryable.from(messengerUsers)
            .map(messengerUser -> pairUserProfiles(messengerUser, socialUsers))
            .filter(pair -> pair.second != null)
            .map(this::prepareDataUser)
            .toList();
   }

   private Pair<MessengerUser, User> pairUserProfiles(MessengerUser messengerUser, List<User> socialUsers) {
      String messengerName = messengerUser.getName();
      User user = Queryable.from(socialUsers).firstOrDefault(temp -> TextUtils.equals(temp.getUsername()
            .toLowerCase(), messengerName));
      return new Pair<>(messengerUser, user);
   }

   private DataUser prepareDataUser(Pair<MessengerUser, User> pair) {
      MessengerUser messengerUser = pair.first;
      User socialUser = pair.second;
      DataUser cachedUser = usersDAO.getUserById(messengerUser.getName()).toBlocking().first();

      DataUser user = new DataUser();
      user.setId(messengerUser.getName().toLowerCase());
      user.setSocialId(socialUser.getId());
      user.setFirstName(socialUser.getFirstName());
      user.setLastName(socialUser.getLastName());
      user.setHost(badgeHelper.hasTripChatHost(socialUser));
      user.setOnline(messengerUser.isOnline());
      user.setFriend(messengerUser.getType() != null ? true : null);

      if (cachedUser != null) {
         if (cachedUser.isFriendSet() && !user.isFriendSet()) {
            user.setFriend(cachedUser.isFriend());
         }
         if (cachedUser.isOnline() && !user.isOnline()) {
            user.setOnline(true);
         }
      }
      user.setAvatarUrl(socialUser.getAvatar() == null ? null : socialUser.getAvatar().getThumb());
      return user;
   }
}
