package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.app.Dialog;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.FlagPopupMenu;
import com.worldventures.dreamtrips.modules.common.view.custom.FlagView;
import com.worldventures.dreamtrips.modules.feed.event.DeleteCommentRequestEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditCommentRequestEvent;
import com.worldventures.dreamtrips.modules.feed.event.ItemFlaggedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadFlagEvent;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
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
public class CommentCell extends AbstractCell<Comment> implements Flaggable {

    @InjectView(R.id.user_photo)
    SimpleDraweeView userPhoto;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.date)
    TextView date;
    @InjectView(R.id.text)
    TextView text;
    @Optional
    @InjectView(R.id.edit)
    ImageView edit;
    @Optional
    @InjectView(R.id.reply)
    ImageView reply;
    @InjectView(R.id.edited)
    ImageView edited;
    @Optional
    @InjectView(R.id.flag)
    FlagView flag;

    @Inject
    SessionHolder<UserSession> appSessionHolder;
    @Inject @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

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

        if (edit != null)
            if (appSessionHolder.get().get().getUser().getId() == owner.getId()) {
                edit.setVisibility(View.VISIBLE);
            } else {
                edit.setVisibility(View.GONE);
            }

        if (flag != null) {
            if (appSessionHolder.get().get().getUser().getId() == owner.getId()) {
                flag.setVisibility(View.GONE);
            } else {
                flag.setVisibility(View.VISIBLE);
            }
        }

        if (getModelObject().isUpdate()) {
            edited.setVisibility(View.VISIBLE);
        } else {
            edited.setVisibility(View.INVISIBLE);
        }
    }

    @Optional
    @OnClick(R.id.flag)
    void onFlagClicked() {
        flag.showProgress();
        getEventBus().post(new LoadFlagEvent(this));
    }

    @Optional
    @OnClick(R.id.edit)
    void onEditClicked() {
        PopupMenu popup = new PopupMenu(itemView.getContext(), edit);
        popup.inflate(R.menu.menu_comment_edit);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    Dialog dialog = new SweetAlertDialog(itemView.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(itemView.getResources().getString(R.string.comment_delete))
                            .setContentText(itemView.getResources().getString(R.string.comment_delete_caption))
                            .setConfirmText(itemView.getResources().getString(R.string.comment_delete_confirm))
                            .setConfirmClickListener(sDialog -> {
                                sDialog.dismissWithAnimation();
                                getEventBus().post(new DeleteCommentRequestEvent(getModelObject()));
                            });
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    break;
                case R.id.action_edit:
                    getEventBus().post(new EditCommentRequestEvent(getModelObject()));
                    break;
            }

            return true;
        });
        popup.show();
    }

    @Override
    public void prepareForReuse() {

    }

    @Override
    public void showFlagDialog(List<Flag> flags) {
        flag.hideProgress();
        FlagPopupMenu popupMenu = new FlagPopupMenu(itemView.getContext(), flag);
        popupMenu.show(flags, (flagReasonId, reason) -> getEventBus().post(new ItemFlaggedEvent(getModelObject(), flagReasonId, reason)));
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

}
