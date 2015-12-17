package com.worldventures.dreamtrips.modules.dtl.validator;

import com.rengwuxian.materialedittext.validation.RegexpValidator;

public class DigitsValidator extends RegexpValidator {

    private static final String PATTERN = "\\d+";

    public DigitsValidator(String errorMessage) {
        super(errorMessage, PATTERN);
    }
}