package com.techery.spares.utils;

import android.os.Bundle;

import com.techery.spares.annotations.State;

import java.lang.reflect.Field;
import java.util.List;

import javax.inject.Inject;

public class ObjectStateHelper {

    private final AnnotationsHelper annotationsHelper;

    public ObjectStateHelper(AnnotationsHelper annotationsHelper) {
        this.annotationsHelper = annotationsHelper;
    }

    public void restoreState(Object target, Bundle savedInstanceState) {
        List<Field> fields = this.annotationsHelper.getFieldsWithAnnotation(target, State.class);

    }

    public void saveState(Object target, Bundle outState) {

    }
}
