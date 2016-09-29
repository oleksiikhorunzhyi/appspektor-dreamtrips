package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.photos.model.PhotoTagPoint;
import com.worldventures.dreamtrips.api.photos.model.PhotoTagPosition;
import com.worldventures.dreamtrips.api.photos.model.TaggedUser;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.Position;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.TagPosition;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

import io.techery.mappery.MapperyContext;

public class PhotoTagConverter implements Converter<com.worldventures.dreamtrips.api.photos.model.PhotoTag, PhotoTag> {

   @Override
   public PhotoTag convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.photos.model.PhotoTag apiTag) {
      PhotoTag tag = new PhotoTag();

      PhotoTagPosition photoTagPosition = apiTag.position();
      PhotoTagPoint topLeftApiPosition = photoTagPosition.topLeft();
      Position topLeft = new Position((float)topLeftApiPosition.x(), (float)topLeftApiPosition.y());
      PhotoTagPoint rightBottomApiPosition = photoTagPosition.bottomRight();
      Position bottomRight = new Position((float) rightBottomApiPosition.x(), (float) rightBottomApiPosition.y());

      TagPosition tagPosition = new TagPosition(topLeft, bottomRight);
      tag.setTagPosition(tagPosition);

      User user = new User();
      TaggedUser apiUser = apiTag.user();
      user.setId(apiTag.id());
      user.setFirstName(apiUser.firstName());
      user.setLastName(apiUser.lastName());
      user.setUsername(apiUser.username());
      user.setAvatar(mapperyContext.convert(apiUser.avatar(), User.Avatar.class));
      user.setBadges(apiUser.badges());
      user.setLocation(apiUser.location());
      user.setCompany(apiUser.company());
      tag.setUser(user);

      return tag;
   }

   @Override
   public Class<com.worldventures.dreamtrips.api.photos.model.PhotoTag> sourceClass() {
      return com.worldventures.dreamtrips.api.photos.model.PhotoTag.class;
   }

   @Override
   public Class<PhotoTag> targetClass() {
      return PhotoTag.class;
   }
}
