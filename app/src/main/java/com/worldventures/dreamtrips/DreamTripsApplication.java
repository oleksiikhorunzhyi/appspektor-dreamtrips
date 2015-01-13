package com.worldventures.dreamtrips;

import com.techery.spares.application.BaseApplicationWithInjector;
import com.techery.spares.module.Annotations.UseModule;
import com.worldventures.dreamtrips.core.module.DTModule;

@UseModule(DTModule.class)
public class DreamTripsApplication extends BaseApplicationWithInjector {

}
