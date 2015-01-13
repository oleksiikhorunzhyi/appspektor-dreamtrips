package com.techery.spares.utils;


import com.techery.spares.module.Annotations.UseModule;

public class ModuleHelper {
    public static Object getUsedModule(Object object) {
        try {
            UseModule useModule = object.getClass().getAnnotation(UseModule.class);
            if (useModule != null) {
                return useModule.value().newInstance();
            } else {
                return null;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }
}
