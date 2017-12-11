package com.worldventures.dreamtrips.social.friends.util

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.model.User
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.model.session.UserSession
import com.worldventures.core.storage.complex_objects.Optional
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate
import com.worldventures.dreamtrips.api.friends.model.ImmutableFriendCandidate
import com.worldventures.dreamtrips.api.session.model.ImmutableAvatar


object MockUtil {

   @JvmStatic
   fun mockFriendCandidate(number: Int): FriendCandidate {
      val avatar = ImmutableAvatar.builder()
            .medium("test")
            .original("test")
            .thumb("test")
            .build()

      return ImmutableFriendCandidate.builder()
            .id(number)
            .username(number.toString())
            .firstName("first_name")
            .lastName("last_name")
            .avatar(avatar)
            .location("test")
            .badges(ArrayList())
            .build()
   }

   @JvmStatic
   fun mockSessionHolder(): SessionHolder {
      val sessionHolder: SessionHolder = mock()
      val userSession: UserSession = mock()
      val user: User = mock()
      whenever(sessionHolder.get()).thenReturn(Optional.of(userSession))
      whenever(userSession.user()).thenReturn(user)
      return sessionHolder
   }

   fun mockUser(id: Int): User {
      val user = User()
      user.firstName = "Name"
      user.lastName = "LastName"
      user.id = id
      return user
   }


}