package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;
import me.relex.circleindicator.CircleIndicator;

public class DisplayOptionsSettingsScreen extends WalletLinearLayout<DisplayOptionsSettingsPresenter.Screen, DisplayOptionsSettingsPresenter, DisplayOptionsSettingsPath> implements DisplayOptionsSettingsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.pager) ViewPager viewPager;
   @InjectView(R.id.indicator) CircleIndicator indicator;

   public DisplayOptionsSettingsScreen(Context context) {
      super(context);
   }

   public DisplayOptionsSettingsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public DisplayOptionsSettingsPresenter createPresenter() {
      return new DisplayOptionsSettingsPresenter(getContext(), getInjector());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      supportConnectionStatusLabel(false);
      setupViewPager();
   }

   private void setupViewPager() {
      Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
      final Point screenSize = new Point();
      display.getSize(screenSize);

      int screenWidth = screenSize.x;
      int pageActualWidth = getResources().getDimensionPixelSize(R.dimen.wallet_settings_display_options_page_size);
      int marginBetweenPages = getResources().getDimensionPixelSize(R.dimen.wallet_settings_display_options_page_margin);

      // Use negative margin to show multiple pages on the screen
      int actualPageMargin = screenWidth - pageActualWidth - marginBetweenPages;
      viewPager.setPageMargin(-actualPageMargin);

      // So page begin to appear at 1/3 of page width
      float alphaThreshold = 1 / 3f;
      // Calculate position for alpha start changing
      float alphaOffset = (1 - actualPageMargin / (float) screenWidth) * alphaThreshold;
      viewPager.setPageTransformer(false, (page, position) -> ((DisplayOptionsViewHolder) page.getTag()).onPagePositionUpdated(position, alphaOffset));

      viewPager.setOffscreenPageLimit(DisplayOptionsEnum.values().length);
      viewPager.setOverScrollMode(OVER_SCROLL_NEVER);
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public void setUser(SmartCardUser user) {
      viewPager.setAdapter(new DisplayOptionsPagerAdapter(getContext(), user, () -> getPresenter().openEditProfileScreen()));
      viewPager.setCurrentItem(0);

      indicator.setViewPager(viewPager);
   }
}