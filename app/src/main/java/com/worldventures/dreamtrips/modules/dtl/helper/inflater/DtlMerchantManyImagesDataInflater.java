package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.ui.fragment.BaseImageFragment;
import com.worldventures.dreamtrips.core.ui.fragment.ImageBundle;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantHelper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantMedia;

import java.util.List;

import butterknife.InjectView;
import me.relex.circleindicator.CircleIndicator;

public class DtlMerchantManyImagesDataInflater extends DtlMerchantCommonDataInflater {

    private final FragmentManager fragmentManager;
    //
    // TODO :: 4/24/16 add view pager usage when requested support for many images
//    @InjectView(R.id.merchant_details_cover_pager)
    ViewPager coverPager;
    @InjectView(R.id.merchant_details_cover_pager_indicator)
    CircleIndicator coverPagerIndicator;

    public DtlMerchantManyImagesDataInflater(DtlMerchantHelper helper, FragmentManager fragmentManager) {
        super(helper);
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onMerchantApply(DtlMerchant merchant) {
        super.onMerchantApply(merchant);
        setImages(merchant.getImages());
    }

    private void setImages(List<DtlMerchantMedia> mediaList) {
        if (mediaList.isEmpty()) {
            return;
        }
        //
        BaseStatePagerAdapter adapter = new BaseStatePagerAdapter(fragmentManager) {
            @Override
            public void setArgs(int position, Fragment fragment) {
                DtlMerchantMedia photo = mediaList.get(position);
                ((BaseImageFragment) fragment).setArgs(new ImageBundle<>(photo));
            }
        };
        Queryable.from(mediaList).forEachR(image -> {
            // TODO :: 4/24/16 temporary empty - untill requested support for many images
        });
        coverPager.setAdapter(adapter);
        if (mediaList.size() > 1) coverPagerIndicator.setViewPager(coverPager);
    }
}
