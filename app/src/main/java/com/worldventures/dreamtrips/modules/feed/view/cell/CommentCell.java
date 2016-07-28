package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.FlagPopupMenu;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.view.custom.TranslateView;
import com.worldventures.dreamtrips.modules.feed.view.util.CommentCellHelper;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import cn.pedant.SweetAlert.SweetAlertDialog;

@Layout(R.layout.adapter_item_comment)
public class CommentCell extends AbstractDelegateCell<Comment, CommentCell.CommentCellDelegate>
        implements Flaggable {

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

    @Inject SessionHolder<UserSession> appSessionHolder;
    @Inject LocaleHelper localeHelper;
    @Inject @Named(RouteCreatorModule.PROFILE) RouteCreator<Integer> routeCreator;
    @Inject @ForActivity Provider<Injector> injectorProvider;

    private CommentCellHelper commentCellHelper;

    public CommentCell(View view) {
        super(view);
        commentCellHelper = new CommentCellHelper(view.getContext());
        commentCellHelper.attachView(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        commentCellHelper.set(getModelObject(), injectorProvider.get());
        User owner = getModelObject().getOwner();
        if (owner.getId() == appSessionHolder.get().get().getUser().getId()) {
            selfActionsWrapper.setVisibility(View.VISIBLE);
            actionsWrapper.setVisibility(View.GONE);
        } else {
            selfActionsWrapper.setVisibility(View.GONE);
            actionsWrapper.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(getModelObject().getLanguageFrom())
                    && !localeHelper.isOwnLanguage(getModelObject().getLanguageFrom())
                    && !getModelObject().isTranslated()) {
                showTranslationButton();
            } else {
                hideTranslationButton();
            }
            if (getModelObject().isTranslated()) {
                viewWithTranslation.showTranslation(getModelObject().getTranslation(),
                        getModelObject().getLanguageFrom());
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
        Dialog dialog = new SweetAlertDialog(itemView.getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(itemView.getResources().getString(R.string.comment_delete))
                .setContentText(itemView.getResources().getString(R.string.comment_delete_caption))
                .setConfirmText(itemView.getResources().getString(R.string.comment_delete_confirm))
                .setConfirmClickListener(sDialog -> {
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
        router.moveTo(routeCreator.createRoute(user.getId()), NavigationConfigBuilder.forActivity()
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