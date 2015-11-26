package com.worldventures.dreamtrips.modules.dtl.validator;

import android.support.annotation.NonNull;

import com.rengwuxian.materialedittext.validation.METValidator;

/**
 * Validator to check if input is empty.<br />
 * {@link #isValid(CharSequence, boolean)} will return true if non-empty, false otherwise.
 */
public class EmptyValidator extends METValidator {

    public EmptyValidator(@NonNull String errorMessage) {
        super(errorMessage);
    }

    @Override
    public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
        return text.toString().trim().length() > 0;
    }
}
