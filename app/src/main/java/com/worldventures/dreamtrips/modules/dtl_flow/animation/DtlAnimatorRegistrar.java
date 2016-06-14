package com.worldventures.dreamtrips.modules.dtl_flow.animation;

import android.util.Pair;

import com.worldventures.dreamtrips.core.flow.animation.BaseAnimatorRegistrar;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.fullscreen_image.DtlFullscreenImagePath;

public class DtlAnimatorRegistrar extends BaseAnimatorRegistrar {

    public DtlAnimatorRegistrar() {
        super();
        animators.put(new Pair<>(DtlMerchantDetailsPath.class, DtlFullscreenImagePath.class),
                new FadeAnimatorFactory());
        animators.put(new Pair<>(DtlFullscreenImagePath.class, DtlMerchantDetailsPath.class),
                new FadeAnimatorFactory());
    }
}
