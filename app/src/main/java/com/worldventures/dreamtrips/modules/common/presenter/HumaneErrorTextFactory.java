package com.worldventures.dreamtrips.modules.common.presenter;

import com.messenger.api.UiErrorAction;
import com.worldventures.dreamtrips.R;

import java.io.IOException;

import io.techery.janet.helper.JanetActionException;

import static com.worldventures.dreamtrips.util.ThrowableUtils.getCauseByType;

public class HumaneErrorTextFactory {


    public int create(Throwable exception) {
        return create(null, exception);
    }

    /**
     * There are only 3 types of errors: no connection, message from UiErrorAction and smth went wrong
     */
    public int create(Object action, Throwable exception) {
        int result = R.string.smth_went_wrong;
        if (getCauseByType(IOException.class, exception.getCause()) != null) {
            result = R.string.no_connection;
        } else if (action instanceof UiErrorAction) {
            result = ((UiErrorAction) action).getErrorMessage();
        } else if (exception instanceof JanetActionException) {
            JanetActionException actionError = (JanetActionException) exception;
            return create(actionError.getAction(), actionError.getCause());
        } else if (exception.getCause() != null) {
            return create(action, exception.getCause());
        }
        return result;
    }
}