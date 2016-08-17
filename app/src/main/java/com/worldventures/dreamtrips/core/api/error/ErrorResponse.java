package com.worldventures.dreamtrips.core.api.error;

import com.innahema.collections.query.queriables.Queryable;

import java.util.HashMap;
import java.util.List;

public class ErrorResponse {

    HashMap<String, String[]> errors;

    transient List<FieldError> fieldErrors;

    public void setErrors(HashMap<String, String[]> errors) {
        this.errors = errors;
    }

    public List<FieldError> getErrors() {
        if (fieldErrors == null) {
            fieldErrors = Queryable.from(errors.entrySet()).map(FieldError::from).toList();
        }
        return fieldErrors;
    }

    public boolean containsField(String field) {
        return Queryable.from(getErrors()).firstOrDefault(element -> element.field.equals(field)) != null;
    }

    public String getMessageForField(String field) {
        FieldError fieldError = Queryable.from(getErrors()).firstOrDefault(element -> element.field.equals(field));
        return fieldError != null ? fieldError.errors[0] : null;
    }

    public String getFirstMessage() {
        return getErrors().get(0).errors[0];
    }
}
