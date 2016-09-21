package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.profile.model.PublicUserProfile;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;
import java.util.List;

import io.techery.mappery.MapperyContext;

public class PublicProfileConverter implements Converter<PublicUserProfile, User> {

   @Override
   public Class<PublicUserProfile> sourceClass() {
      return PublicUserProfile.class;
   }

   @Override
   public Class<User> targetClass() {
      return User.class;
   }

   @Override
   public User convert(MapperyContext mapperyContext, PublicUserProfile apiProfile) {
      User user = new User();

      user.setUsername(apiProfile.username());
      user.setEmail(apiProfile.email());
      user.setCompany(apiProfile.company());

      user.setAvatar(mapperyContext.convert(apiProfile.avatar(), User.Avatar.class));

      user.setFirstName(apiProfile.firstName());
      user.setLastName(apiProfile.lastName());

      user.setLocation(apiProfile.location());
      user.setCountryCode(apiProfile.countryCode());

      user.setBadges(apiProfile.badges());

      user.setBirthDate(apiProfile.birthDate());
      user.setEnrollDate(apiProfile.enrollDate());

      user.setBadges(apiProfile.badges());

      user.setTripImagesCount(apiProfile.tripImagesCount());
      user.setBucketListItemsCount(apiProfile.bucketListItemsCount());
      user.setFriendsCount(apiProfile.friendsCount());
      user.setTermsAccepted(apiProfile.termsAccepted());

      if (apiProfile.relationship() != null) {
         user.setRelationship(mapperyContext.convert(apiProfile.relationship(), User.Relationship.class));
      }

      user.setBackgroundPhotoUrl(apiProfile.backgroundPhotoUrl());

      List<String> apiSubscriptions = apiProfile.subscriptions();
      List<String> subscriptions = new ArrayList<>();
      for (String subscription : apiSubscriptions) {
         subscriptions.add(subscription.toString());
      }
      user.setSubscriptions(subscriptions);

      if (apiProfile.circles() != null) {
         user.setCircles(mapperyContext.convert(apiProfile.circles(), Circle.class));
      }

      if (apiProfile.mutuals() != null) {
         user.setMutualFriends(new User.MutualFriends(apiProfile.mutuals().count()));
      }

      return user;
   }
}
