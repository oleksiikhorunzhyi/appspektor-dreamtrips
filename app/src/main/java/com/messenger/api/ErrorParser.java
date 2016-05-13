package com.messenger.api;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.BaseHttpAction;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Iterator;

import javax.inject.Inject;

import io.techery.janet.http.exception.HttpException;
import io.techery.janet.http.exception.HttpServiceException;
import io.techery.janet.http.model.Response;
import timber.log.Timber;

public class ErrorParser {

    Context context;

    @Inject
    public ErrorParser(@ForApplication Context context) {
        this.context = context;
    }

    /**
     * Port of error parsing logic from ErrorParser in DreamSpiceManager adapted for Janet
     */
    public String getErrorMessage(BaseHttpAction action, Throwable error) {
        if (error instanceof HttpServiceException) {
            Throwable cause = error.getCause();
            if (cause instanceof UnknownHostException || cause instanceof ConnectException) {
                return context.getResources().getString(R.string.no_connection);
            }
            if (cause instanceof HttpException) {
                String httpExceptionMessage = handleHttpException(action, (HttpException) cause);
                if (!TextUtils.isEmpty(httpExceptionMessage)) return httpExceptionMessage;
            }
        }
        return context.getString(R.string.smth_went_wrong);
    }

    @Nullable
    private String handleHttpException(BaseHttpAction action, HttpException cause) {
        HttpException httpException = cause;
        if (shouldBeProcessedLocally(action, httpException)) {
            int errorMessageRes = ((UiErrorAction) action).getErrorMessage();
            return context.getString(errorMessageRes);
        }
        if (httpException.getResponse() != null) {
            Response response = httpException.getResponse();
            String message = getMessageFromResponse(response);
            if (!TextUtils.isEmpty(message)) return message;
        }
        return null;
    }

    private boolean shouldBeProcessedLocally(BaseHttpAction action, HttpException exception) {
        if (!(action instanceof UiErrorAction)) return false;
        return ((exception.getResponse() == null
                || exception.getResponse().getStatus() != HttpStatus.SC_UNPROCESSABLE_ENTITY))
                && ((UiErrorAction) action).getErrorMessage() > 0;
    }

    private String getMessageFromResponse(Response response) {
        String body = response.getBody().toString();
        if (TextUtils.isEmpty(body)) return null;
        try {
            JSONObject parent = new JSONObject(body);
            JSONObject errors = parent.getJSONObject("errors");
            Iterator<?> keys = errors.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                try {
                    JSONArray arr = errors.getJSONArray(key);
                    return arr.getString(0);
                } catch (JSONException e) {
                    return errors.getString(key);
                }
            }
        } catch (Exception e) {
            Timber.e(e, "Can't get error message from response");
        }
        return null;
    }
}
