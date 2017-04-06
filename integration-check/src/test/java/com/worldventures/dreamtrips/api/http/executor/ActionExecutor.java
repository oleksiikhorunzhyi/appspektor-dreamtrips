package com.worldventures.dreamtrips.api.http.executor;

import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;

import ru.yandex.qatools.allure.annotations.Step;

public interface ActionExecutor {

    @Step("Execute: {0}")
    <T extends BaseHttpAction> T execute(T action);
}
