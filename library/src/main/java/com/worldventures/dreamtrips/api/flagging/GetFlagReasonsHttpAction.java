package com.worldventures.dreamtrips.api.flagging;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.flagging.model.FlagReason;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/flag_reasons", method = HttpAction.Method.GET)
public class GetFlagReasonsHttpAction extends AuthorizedHttpAction {

    @Response
    List<FlagReason> flagReasons;

    public List<FlagReason> getFlagReasons() {
        return flagReasons;
    }

}