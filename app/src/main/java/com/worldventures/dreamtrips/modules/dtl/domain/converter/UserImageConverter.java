package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ImmutableUserImage;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.UserImage;
import io.techery.mappery.MapperyContext;

public class UserImageConverter implements Converter<com.worldventures.dreamtrips.api.dtl.merchants.model.UserImage, UserImage> {

    @Override
    public Class<com.worldventures.dreamtrips.api.dtl.merchants.model.UserImage> sourceClass() {
        return com.worldventures.dreamtrips.api.dtl.merchants.model.UserImage.class;
    }

    @Override
    public Class<UserImage> targetClass() {
        return UserImage.class;
    }

    @Override
    public UserImage convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.dtl.merchants.model.UserImage userImage) {
        return ImmutableUserImage.builder()
                .original(userImage.original())
                .medium(userImage.medium())
                .thumb(userImage.thumb())
                .build();
    }
}
