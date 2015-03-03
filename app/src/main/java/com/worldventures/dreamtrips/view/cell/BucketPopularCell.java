package com.worldventures.dreamtrips.view.cell;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFlat;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.techery.spares.ui.view.cell.BaseCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.BucketPopularItem;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 1 on 03.03.15.
 */

@Layout(R.layout.adapter_item_popular_cell)
public class BucketPopularCell extends AbstractCell<BucketPopularItem> {

    @InjectView(R.id.imageViewImage)
    ImageView imageViewImage;
    @InjectView(R.id.textViewName)
    TextView textViewName;
    @InjectView(R.id.textViewDescription)
    TextView textViewDescription;
    @InjectView(R.id.buttonAdd)
    ButtonFlat buttonFlatAdd;
    @InjectView(R.id.buttonDone)
    ButtonFlat buttonFlatDone;

    @Inject
    UniversalImageLoader universalImageLoader;

    @Inject
    Context context;

    public BucketPopularCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        textViewDescription.setText(getModelObject().getDescription());
        textViewName.setText(getModelObject().getName());
        universalImageLoader.loadImage(getModelObject().getImageLink(), imageViewImage, UniversalImageLoader.OP_LIST_SCREEN);
    }

    @OnClick(R.id.buttonDone)
    void onDone() {
        buttonFlatDone.setSelected(!buttonFlatDone.isSelected());
        buttonFlatDone.setBackgroundColor(buttonFlatDone.isSelected()
                ? context.getResources().getColor(R.color.bucket_button_selected)
                : context.getResources().getColor(R.color.bucket_button_default));
        getModelObject().setDone(buttonFlatDone.isSelected());
    }

    @OnClick(R.id.buttonAdd)
    void onAdd() {
        buttonFlatAdd.setSelected(!buttonFlatAdd.isSelected());
        buttonFlatAdd.setBackgroundColor(buttonFlatAdd.isSelected()
                ? context.getResources().getColor(R.color.bucket_button_selected)
                : context.getResources().getColor(R.color.bucket_button_default));
        getModelObject().setAdd(buttonFlatAdd.isSelected());
    }

    @Override
    public void prepareForReuse() {
        imageViewImage.setImageBitmap(null);
    }
}
