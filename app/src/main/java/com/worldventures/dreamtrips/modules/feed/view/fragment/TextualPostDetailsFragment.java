package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.feed.bundle.PostBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityEditClickEvent;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.presenter.TextualPostDetailsPresenter;
import com.worldventures.dreamtrips.modules.feed.view.popup.FeedItemMenuBuilder;

import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;


@Layout(R.layout.fragment_textual_post_details)
public class TextualPostDetailsFragment extends BaseFragmentWithArgs<TextualPostDetailsPresenter, PostBundle> implements TextualPostDetailsPresenter.View {

    @InjectView(R.id.post)
    TextView post;

    @Override
    protected TextualPostDetailsPresenter createPresenter(Bundle savedInstanceState) {
        return new TextualPostDetailsPresenter(getArgs().getTextualPost());
    }

    public void showActionPopup(View anchor) {
        anchor.setEnabled(false);
        FeedItemMenuBuilder.create(getActivity(), anchor, R.menu.menu_feed_entity_edit)
                .onDelete(this::showDeleteDialog)
                .onEdit(() -> {
                    if (isVisibleOnScreen()) getPresenter().onEdit();
                })
                .dismissListener(menu -> anchor.setEnabled(true))
                .show();
    }

    @Override
    public void moveToEdit(TextualPost textualPost) {
        fragmentCompass.setContainerId(R.id.container_details_floating);
        fragmentCompass.disableBackStack();
        fragmentCompass.showContainer();
        //
        NavigationBuilder.create()
                .with(fragmentCompass)
                .data(new PostBundle(textualPost))
                .attach(Route.POST_CREATE);
    }

    @Override
    public void setupView(TextualPost textualPost) {
        post.setText(textualPost.getDescription());
    }

    public void onEvent(FeedEntityEditClickEvent event) {
        if (isVisibleOnScreen()) {
            showActionPopup(event.getAnchor());
        }
    }

    @Override
    public boolean isVisibleOnScreen() {
        return ViewUtils.isPartVisibleOnScreen(this);
    }

    private void showDeleteDialog() {
        Dialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getResources().getString(R.string.post_delete))
                .setContentText(getResources().getString(R.string.post_delete_caption))
                .setConfirmText(getResources().getString(R.string.post_delete_confirm))
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    getPresenter().onDelete();
                });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

}
