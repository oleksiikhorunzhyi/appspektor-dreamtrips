package com.worldventures.dreamtrips.api.session;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.api.session.model.Session;

import org.jetbrains.annotations.Nullable;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.RequestHeader;
import io.techery.janet.http.annotations.Response;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;


@HttpAction(value = "/api/sessions", method = POST)
public class LoginHttpAction extends BaseHttpAction {

    @RequestHeader("DT-App-DeviceModel")
    public final String device;

    @Body
    public final ActionBody body;

    @Response
    Session response;

    public LoginHttpAction(String username, String password) {
        this(username, password, null);
    }

    public LoginHttpAction(String username, String password, @Nullable Device device) {
        this.body = new ActionBody(username, password);
        this.device = createDeviceHeader(device);
    }

    private String createDeviceHeader(@Nullable Device device) {
        if (device == null || device.manufacturer() == null || device.model() == null) return null;
        else return device.manufacturer() + "//" + device.model();
    }

    public Session response() {
        return response;
    }

    private static class ActionBody {
        @SerializedName("username")
        public final String username;

        @SerializedName("password")
        public final String password;

        private ActionBody(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
