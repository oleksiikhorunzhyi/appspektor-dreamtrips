package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.profile.model.PrivateUserProfile;
import com.worldventures.dreamtrips.api.session.model.Subscription;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;
import java.util.List;

import io.techery.mappery.MapperyContext;

public class PrivateProfileConverter implements Converter<PrivateUserProfile, User> {

   @Override
   public Class<PrivateUserProfile> sourceClass() {
      return PrivateUserProfile.class;
   }

   @Override
   public Class<User> targetClass() {
      return User.class;
   }

   @Override
   public User convert(MapperyContext mapperyContext, PrivateUserProfile apiProfile) {
      User user = new User();

      user.setId(apiProfile.id());

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

      user.setDreamTripsPoints(apiProfile.dreamTripsPoints());
      user.setRoviaBucks(apiProfile.roviaBucks());

      user.setTripImagesCount(apiProfile.tripImagesCount());
      user.setBucketListItemsCount(apiProfile.bucketListItemsCount());
      user.setFriendsCount(apiProfile.friendsCount());
      user.setTermsAccepted(apiProfile.termsAccepted());

      user.setBackgroundPhotoUrl(apiProfile.backgroundPhotoUrl());

      List<Subscription> apiSubscriptions = apiProfile.subscriptions();
      List<String> subscriptions = new ArrayList<>();
      for (Subscription subscription : apiSubscriptions) {
         if (subscription != Subscription.UNKNOWN) {
            subscriptions.add(subscription.toString());
         }
      }
      user.setSubscriptions(subscriptions);

      return user;
   }
}
