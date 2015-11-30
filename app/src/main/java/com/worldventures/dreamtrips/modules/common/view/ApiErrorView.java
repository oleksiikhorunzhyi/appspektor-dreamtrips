package com.worldventures.dreamtrips.modules.common.view;

import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.api.error.FieldError;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.List;

public interface ApiErrorView extends Presenter.View {

    /**
     * Called in actual view implementation when api error occurs
     *
     * @param errorResponse list of which fields was failed {@link ErrorResponse}
     * @return {@code true} - if error was handled, {@code false} - if should be handled upper.
     */
    boolean onApiError(ErrorResponse errorResponse);

}
