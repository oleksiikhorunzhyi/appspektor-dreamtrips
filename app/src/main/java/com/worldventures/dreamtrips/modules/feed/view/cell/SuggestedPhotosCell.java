package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.SmartAvatarView;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.SuggestedPhotosDelegate;
import com.worldventures.dreamtrips.modules.feed.view.util.SuggestedPhotosListDecorator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_suggested_photos)
public class SuggestedPhotosCell extends AbstractDelegateCell<MediaAttachment, SuggestedPhotosDelegate>
        implements CellDelegate<PhotoGalleryModel> {

    @Inject
    protected SessionHolder<UserSession> appSessionHolder;
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    @Inject
    SnappyRepository db;

    @InjectView(R.id.suggestion_avatar)
    SmartAvatarView avatar;
    @InjectView(R.id.suggested_photos_user)
    TextView userName;
    @InjectView(R.id.suggested_photos_description)
    TextView description;
    @InjectView(R.id.suggested_photos)
    RecyclerView suggestedList;
    @InjectView(R.id.btn_attach)
    Button btnAttach;
    @InjectView(R.id.card_view_wrapper)
    CardView cardViewWrapper;

    private BaseDelegateAdapter suggestionAdapter;
    private List<PhotoGalleryModel> pickedItems = new ArrayList<>();

    public SuggestedPhotosCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        User user = appSessionHolder.get().get().getUser();
        avatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));
        avatar.setup(user, injectorProvider.get());
        avatar.invalidate();
        //
        userName.setText(user.getFullName());
        //
        changeText();
        //
        if (suggestionAdapter == null) {
            suggestionAdapter = new BaseDelegateAdapter(itemView.getContext(), injectorProvider.get());
            suggestionAdapter.registerCell(PhotoGalleryModel.class, SuggestionPhotoCell.class);
            suggestionAdapter.registerDelegate(PhotoGalleryModel.class, this);
            suggestedList.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            suggestedList.setAdapter(suggestionAdapter);
            suggestedList.addItemDecoration(new SuggestedPhotosListDecorator());
        }
        //
        suggestionAdapter.setItems(getModelObject().chosenImages);
        //
        if (ViewUtils.isTablet(itemView.getContext())) {
            cardViewWrapper.setCardElevation(4);
            int m = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.spacing_small);
            ((ViewGroup.MarginLayoutParams) cardViewWrapper.getLayoutParams()).setMargins(m, m, m, m);
        } else {
            cardViewWrapper.setCardElevation(0);
        }
    }

    @Override
    public void prepareForReuse() {

    }

    @OnClick(R.id.suggestion_cancel)
    void onCancel() {
        cellDelegate.onCancelClicked();
    }

    @OnClick(R.id.btn_attach)
    void onAttach() {
        cellDelegate.onAttachClicked(pickedItems);
    }

    @Override
    public void onCellClicked(PhotoGalleryModel model) {
        model.setChecked(!model.isChecked());
        //
        if (model.isChecked())
            pickedItems.add(model);
        else
            pickedItems.remove(model);
        //
        suggestionAdapter.notifyDataSetChanged();
        //
        btnAttach.setVisibility(hasPickedItems() ? View.VISIBLE : View.GONE);
        changeText();
    }

    private boolean hasPickedItems() {
        return pickedItems.size() > 0;
    }

    private void changeText() {
        if (hasPickedItems()) {
            int resource = QuantityHelper.chooseResource(pickedItems.size(),
                    R.string.suggested_photo_selected_one, R.string.suggested_photo_selected_multiple);
            description.setText(String.format(itemView.getContext().getResources().getString(resource), pickedItems.size()));
        } else {
            description.setText(R.string.suggested_photo);
        }
    }
}
