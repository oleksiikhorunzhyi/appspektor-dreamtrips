package com.worldventures.dreamtrips.api.http.executor;

import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.api.http.provider.JanetProvider;

import java.net.SocketTimeoutException;

import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import io.techery.janet.Janet;
import io.techery.janet.converter.ConverterException;
import ru.yandex.qatools.allure.annotations.Step;
import rx.Observable;

import static com.worldventures.dreamtrips.api.api_common.error.ErrorResponse.BASIC_ERROR;

public class BaseActionExecutor<T extends JanetProvider> implements ActionExecutor {

    private T janetProvider;
    private Janet janet;

    public BaseActionExecutor(T janetProvider) {
        this.janetProvider = janetProvider;
        this.janet = janetProvider.provide();
    }

    protected final T getJanetProvider() {
        return janetProvider;
    }

    @Step("Execute: {0}")
    public <T extends BaseHttpAction> T execute(T action) {
        final ActionPipe<T> pipe = (ActionPipe<T>) janet.createPipe(action.getClass());
        final Observable<ActionState<T>> observable = pipe.createObservable(action);
        ActionState<T> state = observable.toBlocking().last();
        T result = state.action;
        if (result.statusCode() >= 500) {
            throw new AssertionError("Server failed to process request, code=" + result.statusCode());
        }
        if (state.exception != null) {
            if (state.exception.getCause() instanceof ConverterException) {
                throw new AssertionError("Conversion failed, models or data is incorrect", state.exception);
            }
            if (state.exception.getCause() instanceof SocketTimeoutException) {
                throw new RuntimeException("Connection timeout, check your connection", state.exception);
            }
        }
        if (result.statusCode() > 400) {
            if ("Url does not exist".equals(result.errorResponse().reasonFor(BASIC_ERROR))) {
                throw new RuntimeException("Request is not supported on Server / Wrong url");
            }
        }
        return result;
    }
}
