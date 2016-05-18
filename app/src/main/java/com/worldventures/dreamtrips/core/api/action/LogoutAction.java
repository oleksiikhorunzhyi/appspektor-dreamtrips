package com.worldventures.dreamtrips.core.api.action;

import io.techery.janet.http.annotations.HttpAction;

@HttpAction(value = "/api/sessions", method = HttpAction.Method.DELETE)
public class LogoutAction extends AuthorizedHttpAction {

}
