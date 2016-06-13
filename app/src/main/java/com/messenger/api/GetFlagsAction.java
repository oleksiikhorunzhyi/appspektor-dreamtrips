package com.messenger.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/flag_reasons", method = HttpAction.Method.GET)
public class GetFlagsAction extends AuthorizedHttpAction implements UiErrorAction {

    @Response
    List<Flag> flags;

    public List<Flag> getFlags() {
        return flags;
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_flag_reason;
    }
}
