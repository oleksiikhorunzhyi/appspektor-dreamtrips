package com.worldventures.dreamtrips.api.session;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.HttpAction;

@HttpAction(value = "/api/sessions", method = HttpAction.Method.DELETE)
public class LogoutHttpAction extends AuthorizedHttpAction {

}
