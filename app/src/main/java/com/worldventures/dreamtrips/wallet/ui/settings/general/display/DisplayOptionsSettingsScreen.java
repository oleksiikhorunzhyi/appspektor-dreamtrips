package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.SaveHomeDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.DialogErrorView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.RetryDialogErrorView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.RetryDialogErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SCConnectionErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.SmartCardErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.progress.SimpleDialogProgressView;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.success.SimpleToastSuccessView;

import org.jetbrains.annotations.Nullable;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.ErrorView;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.settings.GetHomeDisplayTypeAction;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;
import io.techery.janet.smartcard.exception.NotConnectedException;
import me.relex.circleindicator.CircleIndicator;

public class DisplayOptionsSettingsScreen extends WalletLinearLayout<DisplayOptionsSettingsPresenter.Screen, DisplayOptionsSettingsPresenter, DisplayOptionsSettingsPath> implements DisplayOptionsSettingsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.wrapper_pager) ViewGroup wrapperPager;
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
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      if (isInEditMode()) return;
      supportConnectionStatusLabel(true);
      setupToolbar();
      setupViewPager();
   }

   private void setupToolbar() {
      toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
      toolbar.inflateMenu(R.menu.menu_wallet_settings_display_options);
      toolbar.setOnMenuItemClickListener(item -> {
         if (item.getItemId() == R.id.done) saveCurrentChoice();
         return false;
      });
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

      viewPager.setOffscreenPageLimit(DisplayOptionsPagerAdapter.DISPLAY_OPTIONS.size());
      viewPager.setOverScrollMode(OVER_SCROLL_NEVER);
   }

   private void saveCurrentChoice() {
      getPresenter().saveDisplayType(DisplayOptionsPagerAdapter.DISPLAY_OPTIONS.get(viewPager.getCurrentItem()));
   }

   @Override
   public void setupViewPager(@NonNull SmartCardUser user, @SetHomeDisplayTypeAction.HomeDisplayType int type) {
      viewPager.setAdapter(new DisplayOptionsPagerAdapter(getContext(), user, getPresenter()::openEditProfileScreen));
      viewPager.setCurrentItem(DisplayOptionsPagerAdapter.DISPLAY_OPTIONS.indexOf(type));
      indicator.setViewPager(viewPager);

      wrapperPager.animate().alpha(1).setDuration(400);
   }

   @Override
   public OperationView<GetHomeDisplayTypeAction> provideGetDisplayTypeOperationView() {
      return new ComposableOperationView<>(new SimpleDialogProgressView<>(getContext(), R.string.wallet_settings_general_display_loading, false),
            ErrorViewFactory.<GetHomeDisplayTypeAction>builder()
                  .addProvider(new SmartCardErrorViewProvider<>(getContext(),
                        retryAction -> getPresenter().fetchDisplayType(), cancelAction -> getPresenter().goBack()))
                  .addProvider(new RetryDialogErrorViewProvider<>(getContext(), NotConnectedException.class, R.string.wallet_smart_card_is_disconnected,
                        retryAction -> getPresenter().fetchDisplayType(), cancelAction -> getPresenter().goBack()))
                  .defaultErrorView(new RetryDialogErrorView<>(getContext(), R.string.error_something_went_wrong,
                        retryAction -> getPresenter().fetchDisplayType(), cancelAction -> getPresenter().goBack()))
                  .build()
      );
   }

   @Override
   public OperationView<SaveHomeDisplayTypeCommand> provideSaveDisplayTypeOperationView() {
      return new ComposableOperationView<>(new SimpleDialogProgressView<>(getContext(), R.string.wallet_settings_general_display_updating, false),
            new SimpleToastSuccessView<>(getContext(), R.string.wallet_settings_general_display_changes_saved),
            ErrorViewFactory.<SaveHomeDisplayTypeCommand>builder()
                  .addProvider(getUserRequiredInfoMissingDialogProvider(
                        SaveHomeDisplayTypeCommand.MissingUserPhoneException.class,
                        R.string.wallet_settings_general_display_phone_required_title,
                        R.string.wallet_settings_general_display_phone_required_message))
                  .addProvider(getUserRequiredInfoMissingDialogProvider(
                        SaveHomeDisplayTypeCommand.MissingUserPhotoException.class,
                        R.string.wallet_settings_general_display_photo_required_title,
                        R.string.wallet_settings_general_display_photo_required_message))
                  .addProvider(new SCConnectionErrorViewProvider<>(getContext()))
                  .addProvider(new SmartCardErrorViewProvider<>(getContext(), retryAction -> saveCurrentChoice()))
                  .defaultErrorView(new RetryDialogErrorView<>(getContext(), R.string.error_something_went_wrong, retryAction -> saveCurrentChoice()))
                  .build()
      );
   }

   @NonNull
   private ErrorViewProvider<SaveHomeDisplayTypeCommand>
   getUserRequiredInfoMissingDialogProvider(Class<? extends Throwable> error, @StringRes int title, @StringRes int message) {
      return new ErrorViewProvider<SaveHomeDisplayTypeCommand>() {
         @Override
         public Class<? extends Throwable> forThrowable() {
            return error;
         }

         @Nullable
         @Override
         public ErrorView<SaveHomeDisplayTypeCommand> create(SaveHomeDisplayTypeCommand command, Throwable throwable) {
            return new DialogErrorView<SaveHomeDisplayTypeCommand>(getContext()) {
               @Override
               protected MaterialDialog createDialog(SaveHomeDisplayTypeCommand command, Throwable throwable, Context context) {
                  return new MaterialDialog.Builder(getContext())
                        .title(title)
                        .content(message)
                        .positiveText(android.R.string.ok)
                        .build();
               }
            };
         }
      };
   }
}
