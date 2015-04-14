package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.core.utils.events.AddPressedEvent;
import com.worldventures.dreamtrips.core.utils.events.DonePressedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_popular_cell)
public class BucketPopularCell extends AbstractCell<PopularBucketItem> {

    @InjectView(R.id.imageViewImage)
    protected ImageView imageViewImage;
    @InjectView(R.id.textViewName)
    protected TextView textViewName;
    @InjectView(R.id.textViewDescription)
    protected TextView textViewDescription;
    @InjectView(R.id.buttonAdd)
    protected TextView buttonFlatAdd;
    @InjectView(R.id.buttonDone)
    protected TextView buttonFlatDone;
    @InjectView(R.id.progressBar)
    protected ProgressBar progressBar;

    @Inject
    protected UniversalImageLoader universalImageLoader;

    @Inject
    protected Context context;

    public BucketPopularCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        textViewDescription.setText(getModelObject().getDescription());
        textViewName.setText(getModelObject().getName());

        int width = context.getResources().getDimensionPixelSize(R.dimen.bucket_popular_photo_width);
        int height = context.getResources().getDimensionPixelOffset(R.dimen.tripImageHeight);

        universalImageLoader.loadImage(getModelObject().getCoverPhotoUrlThumb(width, height),
                imageViewImage, UniversalImageLoader.OP_TRIP_PHOTO);

        if (getModelObject().isLoading()) {
            hideButtons();
        } else {
            showButtons();
        }

    }

    @OnClick(R.id.buttonDone)
    void onDone() {
        getEventBus().post(new DonePressedEvent(getModelObject(), getPosition()));
        hideButtons();
    }

    @OnClick(R.id.buttonAdd)
    void onAdd() {
        getEventBus().post(new AddPressedEvent(getModelObject(), getPosition()));
        hideButtons();
    }

    private void hideButtons() {
        buttonFlatAdd.setVisibility(View.INVISIBLE);
        buttonFlatDone.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showButtons() {
        buttonFlatAdd.setVisibility(View.VISIBLE);
        buttonFlatDone.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void prepareForReuse() {
        imageViewImage.setImageBitmap(null);
    }
}