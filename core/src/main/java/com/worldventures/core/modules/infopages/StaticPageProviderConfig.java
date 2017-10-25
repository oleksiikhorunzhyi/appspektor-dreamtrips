package com.worldventures.core.modules.infopages;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.service.DeviceInfoProvider;

import org.immutables.value.Value;

@Value.Immutable
public interface StaticPageProviderConfig {

   SessionHolder appSessionHolder();
   DeviceInfoProvider deviceInfoProvider();
   String apiUrl();
   String backofficeUrl();
   String forgotPasswordUrl();
   String uploaderyUrl();
}
