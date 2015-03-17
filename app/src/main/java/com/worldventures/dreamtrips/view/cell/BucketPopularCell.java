package com.worldventures.dreamtrips.view.cell;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFlat;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.spice.DreamSpiceManager;
import com.worldventures.dreamtrips.core.model.bucket.PopularBucketItem;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.utils.busevents.AddPressedEvent;
import com.worldventures.dreamtrips.utils.busevents.DonePressedEvent;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 1 on 03.03.15.
 */

@Layout(R.layout.adapter_item_popular_cell)
public class BucketPopularCell extends AbstractCell<PopularBucketItem> {

    @InjectView(R.id.imageViewImage)
    ImageView imageViewImage;
    @InjectView(R.id.textViewName)
    TextView textViewName;
    @InjectView(R.id.textViewDescription)
    TextView textViewDescription;
    @InjectView(R.id.buttonAdd)
    TextView buttonFlatAdd;
    @InjectView(R.id.buttonDone)
    TextView buttonFlatDone;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    @Inject
    UniversalImageLoader universalImageLoader;

    @Inject
    Context context;

    @Inject
    DreamSpiceManager dreamSpiceManager;

    public BucketPopularCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        textViewDescription.setText(getModelObject().getDescription());
        textViewName.setText(getModelObject().getName());
        universalImageLoader.loadImage(getModelObject().getImageLink(), imageViewImage, UniversalImageLoader.OP_LIST_SCREEN);

        if (getModelObject().isLoading()) {
            hideButtons();
        } else {
            showButtons();
        }

    }

    @OnClick(R.id.buttonDone)
    void onDone() {
        //getModelObject().setDone(buttonFlatDone.isSelected());
        getEventBus().post(new DonePressedEvent(getModelObject(), getPosition()));
        hideButtons();
    }

    @OnClick(R.id.buttonAdd)
    void onAdd() {
        //getModelObject().setAdd(buttonFlatAdd.isSelected());
        getEventBus().post(new AddPressedEvent(getModelObject(), getPosition()));
        hideButtons();
    }

    private void hideButtons() {
        buttonFlatAdd.setVisibility(View.GONE);
        buttonFlatDone.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showButtons() {
        buttonFlatAdd.setVisibility(View.VISIBLE);
        buttonFlatDone.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void prepareForReuse() {
        imageViewImage.setImageBitmap(null);
    }
}