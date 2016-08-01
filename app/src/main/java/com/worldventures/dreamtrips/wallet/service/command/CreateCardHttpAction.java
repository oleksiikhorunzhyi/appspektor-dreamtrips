package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.wallet.domain.entity.Provision;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(method = HttpAction.Method.PUT, value = "/create_card")
public class CreateCardHttpAction {
    @Response
    Provision response;

    String code;

    public CreateCardHttpAction(String code) {
        this.code = code;
    }

    public Provision getResponse() {
        return response;
    }
}