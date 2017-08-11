package com.worldventures.dreamtrips.core.janet.api_lib;

import com.worldventures.dreamtrips.api.session.model.Device;

public class CredentialsStorage {

   public final String userName;
   public final String password;
   public final Device device;

   public CredentialsStorage(String userName, String password, Device device) {
      this.userName = userName;
      this.password = password;
      this.device = device;
   }
}
