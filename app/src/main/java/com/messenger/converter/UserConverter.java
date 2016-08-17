package com.messenger.converter;

import com.messenger.entities.DataUser;
import com.worldventures.dreamtrips.modules.common.model.User.Relationship;

public final class UserConverter {

   public static DataUser convert(com.worldventures.dreamtrips.modules.common.model.User user) {
      DataUser messengerUser = new DataUser(user.getUsername());
      messengerUser.setSocialId(user.getId());
      messengerUser.setFirstName(user.getFirstName());
      messengerUser.setLastName(user.getLastName());
      messengerUser.setAvatarUrl(user.getAvatar() == null ? null : user.getAvatar().getThumb());
      messengerUser.setFriend(user.getRelationship() == Relationship.FRIEND);
      return messengerUser;
   }

}
