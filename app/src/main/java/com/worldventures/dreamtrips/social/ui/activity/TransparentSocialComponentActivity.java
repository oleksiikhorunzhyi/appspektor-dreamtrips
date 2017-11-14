package com.worldventures.dreamtrips.social.ui.activity;

import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.AnimationConfig;

import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;

@Layout(R.layout.activity_component)
public class TransparentSocialComponentActivity extends SocialComponentActivity {

   public void moveTo(Class<? extends Fragment> fragmentClazz, Parcelable args) {
      router.moveTo(fragmentClazz, NavigationConfigBuilder.forFragment()
            .fragmentManager(getSupportFragmentManager())
            .containerId(R.id.container_main)
            .backStackEnabled(true)
            .animationConfig(new AnimationConfig(R.anim.fade_in, R.anim.fade_out))
            .data(args)
            .build());
   }
}
