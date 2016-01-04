package com.messenger.converter;

import com.messenger.messengerservers.entities.User;
import com.worldventures.dreamtrips.core.utils.TextUtils;

public final class UserConverter {

    public static User convert (com.worldventures.dreamtrips.modules.common.model.User user){
        User messengerUser = new User(user.getUsername());
        messengerUser.setSocialId(user.getId());
        messengerUser.setName(TextUtils.join(" ", user.getFirstName(), user.getLastName()));
        messengerUser.setAvatarUrl(user.getAvatar() == null ? null : user.getAvatar().getThumb());
        return messengerUser;
    }

}
