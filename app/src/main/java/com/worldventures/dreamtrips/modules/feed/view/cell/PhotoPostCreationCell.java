package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.PhotoPostCreationDelegate;

import butterknife.InjectView;
import butterknife.OnClick;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

@Layout(R.layout.adapter_item_photo_post)
public class PhotoPostCreationCell extends AbstractDelegateCell<PhotoCreationItem, PhotoPostCreationDelegate> {

    @InjectView(R.id.shadow)
    View shadow;
    @InjectView(R.id.fab_progress)
    FabButton fabProgress;
    @InjectView(R.id.attached_photo)
    SimpleDraweeView attachedPhoto;
    @InjectView(R.id.fabbutton_circle)
    CircleImageView circleView;
    @InjectView(R.id.tag_btn)
    TextView tagButton;
    @InjectView(R.id.photo_title)
    EditText photoTitle;

    public PhotoPostCreationCell(View view) {
        super(view);
    }

    private TextWatcherAdapter textWatcher = new TextWatcherAdapter() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            super.onTextChanged(s, start, before, count);
            getModelObject().setTitle(s.toString().trim());
        }
    };

    @Override
    protected void syncUIStateWithModel() {
        switch (getModelObject().getStatus()) {
            case STARTED:
                showProgress();
                break;
            case FAILED:
                showError();
                break;
            case COMPLETED:
                hideProgress();
                break;
        }
        //
        invalidateAddTagBtn();
        //
        attachedPhoto.setController(GraphicUtils.provideFrescoResizingController(
                Uri.parse(getModelObject().getFilePath()), attachedPhoto.getController()));
        //
        photoTitle.setText(getModelObject().getTitle());
        photoTitle.addTextChangedListener(textWatcher);
    }

    private void showProgress() {
        shadow.setVisibility(View.VISIBLE);
        fabProgress.setVisibility(View.VISIBLE);
        fabProgress.setIcon(R.drawable.ic_upload_cloud, R.drawable.ic_upload_cloud);
        fabProgress.setIndeterminate(true);
        fabProgress.showProgress(true);
        int color = itemView.getResources().getColor(R.color.bucket_blue);
        circleView.setColor(color);
    }

    private void hideProgress() {
        fabProgress.setVisibility(View.GONE);
        shadow.setVisibility(View.GONE);
    }

    private void showError() {
        fabProgress.showProgress(false);
        fabProgress.setIcon(R.drawable.ic_upload_retry, R.drawable.ic_upload_retry);
        int color = itemView.getResources().getColor(R.color.bucket_red);
        circleView.setColor(color);
    }

    @OnClick(R.id.fab_progress)
    void onProgress() {
        if (getModelObject().getStatus().equals(UploadTask.Status.FAILED)) {
            cellDelegate.onProgressClicked(getModelObject());
        }
    }

    @OnClick(R.id.tag_btn)
    void onTag() {
        cellDelegate.onTagClicked(getModelObject());
    }

    @OnClick(R.id.remove)
    void onDelete() {
        cellDelegate.onRemoveClicked(getModelObject());
    }

    @Override
    public void prepareForReuse() {

    }

    private void invalidateAddTagBtn() {
        tagButton.setVisibility(getModelObject().getStatus() == UploadTask.Status.COMPLETED ? View.VISIBLE : View.GONE);

        if (getModelObject().getCombinedTags().isEmpty()) {
            tagButton.setText(R.string.tag_people);
            tagButton.setSelected(false);
        } else {
            tagButton.setText(R.string.empty);
            tagButton.setSelected(true);
        }
    }
}
