package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.User;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.FragmentClassProviderModule;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.FragmentClassProvider;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.feed.model.comment.Comment;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.TranslateView;
import com.worldventures.dreamtrips.social.ui.feed.view.util.CommentCellHelper;
import com.worldventures.dreamtrips.social.ui.flags.view.FlagPopupMenu;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Flag;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import cn.pedant.SweetAlert.SweetAlertDialog;

@Layout(R.layout.adapter_item_comment)
public class CommentCell extends BaseAbstractDelegateCell<Comment, CommentCell.CommentCellDelegate> implements Flaggable {

   @InjectView(R.id.user_photo) SimpleDraweeView userPhoto;
   @InjectView(R.id.user_name) TextView userName;
   @InjectView(R.id.date) TextView date;
   @InjectView(R.id.text) TextView text;
   @InjectView(R.id.edited) ImageView edited;
   @InjectView(R.id.actions_wrapper) View actionsWrapper;
   @InjectView(R.id.self_actions_wrapper) View selfActionsWrapper;
   @InjectView(R.id.comment_flag) View flagButton;
   @InjectView(R.id.comment_translate) View translateButton;
   @InjectView(R.id.comment_translate_view) TranslateView viewWithTranslation;
   @InjectView(R.id.translation_dot_separator) View translationDotSeparator;

   @Inject SessionHolder appSessionHolder;
   @Inject @Named(FragmentClassProviderModule.PROFILE) FragmentClassProvider<Integer> fragmentClassProvider;
   @Inject @ForActivity Provider<Injector> injectorProvider;
   @Inject Router router;

   private CommentCellHelper commentCellHelper;

   public CommentCell(View view) {
      super(view);
      commentCellHelper = new CommentCellHelper(view.getContext());
      commentCellHelper.attachView(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      if (!appSessionHolder.get().isPresent()) {
         return;
      }

      commentCellHelper.set(getModelObject(), injectorProvider.get());
      User owner = getModelObject().getOwner();

      boolean ownComment = owner.getId() == appSessionHolder.get().get().user().getId();
      boolean emptyCommentLanguage = TextUtils.isEmpty(getModelObject().getLanguage());
      boolean ownLanguage = LocaleHelper.isOwnLanguage(appSessionHolder, getModelObject().getLanguage());
      boolean alreadyTranslated = getModelObject().isTranslated();

      if (ownComment) {
         selfActionsWrapper.setVisibility(View.VISIBLE);
         actionsWrapper.setVisibility(View.GONE);
         viewWithTranslation.hide();
         hideTranslationButton();
      } else {
         selfActionsWrapper.setVisibility(View.GONE);
         actionsWrapper.setVisibility(View.VISIBLE);
         if (!emptyCommentLanguage && !ownLanguage && !getModelObject().isTranslated()) {
            showTranslationButton();
         } else {
            hideTranslationButton();
         }
         if (alreadyTranslated) {
            viewWithTranslation.showTranslation(getModelObject().getTranslation(), getModelObject().getLanguage());
         } else {
            viewWithTranslation.hide();
         }
      }

      if (getModelObject().isUpdate()) {
         edited.setVisibility(View.VISIBLE);
      } else {
         edited.setVisibility(View.INVISIBLE);
      }
   }

   @OnClick(R.id.comment_translate)
   void onTranslateClicked() {
      viewWithTranslation.showProgress();
      hideTranslationButton();
      cellDelegate.onTranslateComment(getModelObject());
   }

   @Optional
   @OnClick(R.id.comment_flag)
   void onFlagClicked() {
      cellDelegate.onFlagClicked(this);
   }

   @Optional
   @OnClick(R.id.comment_edit)
   void onEditClicked() {
      cellDelegate.onEditComment(getModelObject());
   }

   @Optional
   @OnClick(R.id.comment_delete)
   void onDeleteClicked() {
      Dialog dialog = new SweetAlertDialog(itemView.getContext(), SweetAlertDialog.WARNING_TYPE).setTitleText(itemView.getResources()
            .getString(R.string.comment_delete)).setContentText(itemView.getResources()
            .getString(R.string.comment_delete_caption)).setConfirmText(itemView.getResources()
            .getString(R.string.comment_delete_confirm)).setConfirmClickListener(sDialog -> {
         sDialog.dismissWithAnimation();
         cellDelegate.onDeleteComment(getModelObject());
      });
      dialog.setCanceledOnTouchOutside(true);
      dialog.show();
   }

   @Override
   public void showFlagDialog(List<Flag> flags) {
      FlagPopupMenu popupMenu = new FlagPopupMenu(itemView.getContext(), flagButton);
      popupMenu.show(flags, (flagReasonId, reason) -> cellDelegate.onFlagChosen(getModelObject(), flagReasonId, reason));
   }

   @Optional
   @OnClick(R.id.user_photo)
   void commentOwnerClicked() {
      User user = commentCellHelper.getComment().getOwner();
      openUser(user);
   }

   private void openUser(User user) {
      router.moveTo(fragmentClassProvider.provideFragmentClass(user.getId()), NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .data(new UserBundle(user))
            .build());
   }

   private void showTranslationButton() {
      translateButton.setVisibility(View.VISIBLE);
      translationDotSeparator.setVisibility(View.VISIBLE);
   }

   private void hideTranslationButton() {
      translateButton.setVisibility(View.GONE);
      translationDotSeparator.setVisibility(View.GONE);
   }

   public interface CommentCellDelegate extends CellDelegate<Comment> {

      void onEditComment(Comment comment);

      void onDeleteComment(Comment comment);

      void onTranslateComment(Comment comment);

      void onFlagClicked(Flaggable flaggableView);

      void onFlagChosen(Comment comment, int flagReasonId, String reason);
   }
}
