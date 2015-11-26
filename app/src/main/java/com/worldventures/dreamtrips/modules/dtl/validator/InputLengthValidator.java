package com.worldventures.dreamtrips.modules.dtl.validator;

import android.support.annotation.NonNull;

import com.rengwuxian.materialedittext.validation.METValidator;

/**
 * Validator to check maximum length of view's input.<br />
 * {@link #isValid(CharSequence, boolean)} returns true if input is in bounds [0;maxLength], false otherwise.
 */
public class InputLengthValidator extends METValidator {

    private int maxLength;

    public InputLengthValidator(int maxLength, @NonNull String errorMessage) {
        super(errorMessage);
        this.maxLength = maxLength;
    }

    @Override
    public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
        return text.toString().trim().length() <= maxLength;
    }
}
