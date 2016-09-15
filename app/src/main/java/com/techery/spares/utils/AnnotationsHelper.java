package com.techery.spares.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AnnotationsHelper {
   public List<Field> getFieldsWithAnnotation(Object target, Class<? extends Annotation> annotationClass) {
      List<Field> foundedFields = new ArrayList<Field>();

      Field[] fields = target.getClass().getFields();

      for (Field f : fields) {
         Object annotation = f.getAnnotation(annotationClass);

         if (annotation != null) {
            foundedFields.add(f);
         }
      }
      return foundedFields;
   }
}
