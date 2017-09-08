package com.worldventures.dreamtrips.modules.infopages;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.delegate.system.DeviceInfoProvider;

import org.immutables.value.Value;

@Value.Immutable
public interface StaticPageProviderConfig {

   SessionHolder appSessionHolder();
   DeviceInfoProvider deviceInfoProvider();
   String apiUrl();
   String backofficeUrl();
   String uploaderyUrl();
}
