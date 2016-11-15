package com.worldventures.dreamtrips.core.api.action;

import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.modules.common.model.Session;

import org.jetbrains.annotations.Nullable;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.RequestHeader;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/sessions", type = HttpAction.Type.SIMPLE, method = HttpAction.Method.POST)
public class LoginAction extends BaseHttpAction {

   @RequestHeader("DT-App-DeviceModel") String device;
   @Body LoginBody loginBody;
   @Response Session loginResponse;

   public LoginAction(String username, String password) {
      this(username, password, null);
   }

   public LoginAction(String username, String password, @Nullable Device device) {
      this.loginBody = new LoginAction.LoginBody(username, password);
      this.device = createDeviceHeader(device);
   }
   private String createDeviceHeader(@Nullable Device device) {
      if (device == null || device.manufacturer() == null || device.model() == null) return null;
      else return device.manufacturer() + "//" + device.model();
   }

   public Session getLoginResponse() {
      return loginResponse;
   }

   private static class LoginBody {

      private final String username;
      private final String password;

      private LoginBody(String username, String password) {
         this.username = username;
         this.password = password;
      }
   }
}
