package com.worldventures.dreamtrips.social.ui.membership.view.fragment;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.BundleUrlFragment;

@Layout(R.layout.fragment_preview_template)
public class PreviewTemplateFragment extends BundleUrlFragment {

   @Override
   protected void lockOrientationIfNeeded() {
      // don't lock
   }
}
