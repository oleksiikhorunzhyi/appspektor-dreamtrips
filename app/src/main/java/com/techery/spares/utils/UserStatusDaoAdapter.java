package com.techery.spares.utils;

import com.messenger.storage.dao.UsersDAO;

import rx.Observable;

public class UserStatusDaoAdapter implements UserStatusAdapter {
   private UsersDAO usersDao;

   public UserStatusDaoAdapter(UsersDAO usersDAO) {
      this.usersDao = usersDAO;
   }

   @Override
   public Observable<Boolean> getUserHolder(String username) {
      return usersDao.getUserById(username)
            .filter(dataUser -> dataUser != null)
            .flatMap(dataUser -> Observable.just(dataUser.isOnline()));
   }
}
