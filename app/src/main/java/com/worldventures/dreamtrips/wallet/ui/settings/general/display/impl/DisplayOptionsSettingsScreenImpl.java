package com.worldventures.dreamtrips.wallet.ui.settings.general.display.impl;


import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.command.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.GetDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.SaveDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.exception.MissingUserPhoneException;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.exception.MissingUserPhotoException;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
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
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsPagerAdapter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSettingsScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSource;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsViewHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.UpdateSmartCardUserOperationView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileDelegate;

import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.ErrorView;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;
import io.techery.janet.smartcard.exception.NotConnectedException;
import me.relex.circleindicator.CircleIndicator;

import static android.view.View.OVER_SCROLL_NEVER;

public class DisplayOptionsSettingsScreenImpl extends WalletBaseController<DisplayOptionsSettingsScreen, DisplayOptionsSettingsPresenter> implements DisplayOptionsSettingsScreen {
   public static String KEY_PROFILE_VIEWMODEL = "key_profile_viewmodel";
   public static String KEY_DISPLAY_OPTIONS_SOURCE = "key_smart_card_user";

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.wrapper_pager) ViewGroup wrapperPager;
   @InjectView(R.id.pager) ViewPager viewPager;
   @InjectView(R.id.indicator) CircleIndicator indicator;

   @Inject DisplayOptionsSettingsPresenter presenter;

   public static DisplayOptionsSettingsScreenImpl create(DisplayOptionsSource source) {
      return create(null, source);
   }

   public static DisplayOptionsSettingsScreenImpl create(ProfileViewModel profileViewModel, DisplayOptionsSource source) {
      final Bundle args = new Bundle();
      if (profileViewModel != null) {
         args.putParcelable(KEY_PROFILE_VIEWMODEL, profileViewModel);
      }
      args.putSerializable(KEY_DISPLAY_OPTIONS_SOURCE, source);
      return new DisplayOptionsSettingsScreenImpl(args);
   }

   public DisplayOptionsSettingsScreenImpl(Bundle args) {
      super(args);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_settings_display_options, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return true;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
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
   public OperationView<GetDisplayTypeCommand> provideGetDisplayTypeOperationView() {
      return new ComposableOperationView<>(new SimpleDialogProgressView<>(getContext(), R.string.wallet_settings_general_display_loading, false),
            ErrorViewFactory.<GetDisplayTypeCommand>builder()
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
   public OperationView<SaveDisplayTypeCommand> provideSaveDisplayTypeOperationView() {
      return new ComposableOperationView<>(new SimpleDialogProgressView<>(getContext(), R.string.wallet_settings_general_display_updating, false),
            new SimpleToastSuccessView<>(getContext(), R.string.wallet_settings_general_display_changes_saved),
            ErrorViewFactory.<SaveDisplayTypeCommand>builder()
                  .addProvider(getUserRequiredInfoMissingDialogProvider(MissingUserPhoneException.class,
                        R.string.wallet_settings_general_display_phone_required_title,
                        R.string.wallet_settings_general_display_phone_required_message))
                  .addProvider(getUserRequiredInfoMissingDialogProvider(MissingUserPhotoException.class,
                        R.string.wallet_settings_general_display_photo_required_title,
                        R.string.wallet_settings_general_display_photo_required_message))
                  .addProvider(new SCConnectionErrorViewProvider<>(getContext()))
                  .addProvider(new SmartCardErrorViewProvider<>(getContext(), retryAction -> saveCurrentChoice()))
                  .defaultErrorView(new RetryDialogErrorView<>(getContext(), R.string.error_something_went_wrong, retryAction -> saveCurrentChoice()))
                  .build()
      );
   }

   @Override
   public ProfileViewModel getProfileViewModel() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_PROFILE_VIEWMODEL))
                  ? getArgs().getParcelable(KEY_PROFILE_VIEWMODEL)
                  : null;
   }

   @Override
   public DisplayOptionsSource getDisplayOptionsSource() {
      return (getArgs() != null && !getArgs().isEmpty() && getArgs().containsKey(KEY_DISPLAY_OPTIONS_SOURCE))
            ? (DisplayOptionsSource) getArgs().getSerializable(KEY_DISPLAY_OPTIONS_SOURCE)
            : null;
   }

   @Override
   public OperationView<UpdateSmartCardUserCommand> provideUpdateSmartCardOperation(WalletProfileDelegate delegate) {
      return new UpdateSmartCardUserOperationView.UpdateUser(getContext(), delegate, null);
   }

   @Override
   public OperationView<RetryHttpUploadUpdatingCommand> provideHttpUploadOperation(WalletProfileDelegate delegate) {
      return new UpdateSmartCardUserOperationView.RetryHttpUpload(getContext(), delegate);
   }

   @NonNull
   private ErrorViewProvider<SaveDisplayTypeCommand>
   getUserRequiredInfoMissingDialogProvider(Class<? extends Throwable> error, @StringRes int title, @StringRes int message) {
      return new ErrorViewProvider<SaveDisplayTypeCommand>() {
         @Override
         public Class<? extends Throwable> forThrowable() {
            return error;
         }

         @Nullable
         @Override
         public ErrorView<SaveDisplayTypeCommand> create(SaveDisplayTypeCommand command, Throwable parentThrowable, Throwable throwable) {
            return new DialogErrorView<SaveDisplayTypeCommand>(getContext()) {
               @Override
               protected MaterialDialog createDialog(SaveDisplayTypeCommand command, Throwable throwable, Context context) {
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

   @Override
   public DisplayOptionsSettingsPresenter getPresenter() {
      return presenter;
   }
}
