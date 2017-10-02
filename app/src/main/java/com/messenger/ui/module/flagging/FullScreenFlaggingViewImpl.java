package com.messenger.ui.module.flagging;

import android.view.View;

import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.flags.view.FlagPopupMenu;
import com.worldventures.dreamtrips.social.ui.flags.view.FlagView;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Flag;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FullScreenFlaggingViewImpl extends FlaggingViewImpl {

   @InjectView(R.id.flag) FlagView flagView;

   public FullScreenFlaggingViewImpl(View view, Injector injector) {
      super(view, injector);
      ButterKnife.inject(this, view);
   }

   @Override
   public void showFlagsListDialog(List<Flag> flags) {
      FlagPopupMenu popupMenu = new FlagPopupMenu(getContext(), flagView);
      flagView.post(() -> popupMenu.show(flags, flag -> getPresenter().onFlagTypeChosen(flag)));
   }
}
