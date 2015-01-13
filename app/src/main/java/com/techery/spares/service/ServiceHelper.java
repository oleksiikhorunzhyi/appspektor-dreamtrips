package com.techery.spares.service;


import android.app.Service;

import com.techery.spares.module.Annotations.UseModule;

public class ServiceHelper {
    public static Object getServiceModule(Service service) {
        try {
            UseModule useModule = service.getClass().getAnnotation(UseModule.class);
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
