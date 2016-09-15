package com.worldventures.dreamtrips.modules.membership.view.fragment;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;

@Layout(R.layout.fragment_preview_template)
public class PreviewTemplateFragment extends StaticInfoFragment.BundleUrlFragment {

   @Override
   protected void lockOrientationIfNeeded() {
      // don't lock
   }
}
