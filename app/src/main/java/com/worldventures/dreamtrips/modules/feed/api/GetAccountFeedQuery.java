package com.worldventures.dreamtrips.modules.feed.api;

import android.support.annotation.Nullable;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.techery.spares.utils.gson.LowercaseEnumTypeAdapterFactory;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DateTimeDeserializer;
import com.worldventures.dreamtrips.core.api.DateTimeSerializer;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.serializer.FeedEntityDeserializer;
import com.worldventures.dreamtrips.modules.feed.model.serializer.FeedItemDeserializer;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetAccountFeedQuery extends Query<ArrayList<ParentFeedItem>> {

    public static final int LIMIT = 10;
    private Date before;
    private String circleId;

    public GetAccountFeedQuery(String id) {
        this(null, id);
    }

    public GetAccountFeedQuery(Date before, @Nullable String circleId) {
        super((Class<ArrayList<ParentFeedItem>>) new ArrayList<ParentFeedItem>().getClass());
        this.before = before;
        this.circleId = circleId;
    }

    @Override
    public ArrayList<ParentFeedItem> loadDataFromNetwork() throws Exception {
        String before = this.before == null ? null : DateTimeUtils.convertDateToUTCString(this.before);
        ArrayList<ParentFeedItem> result = getService().getAccountFeed(LIMIT, before, circleId);
        return stubCollages(result);
    }

    private ArrayList<ParentFeedItem> stubCollages(ArrayList<ParentFeedItem> target) {
        String json = "[" +
                "  {" +
                "    \"type\":\"Single\"," +
                "    \"items\":[" +
                "      {" +
                "        \"type\":\"Post\"," +
                "        \"action\":\"add\"," +
                "        \"posted_at\":\"2016-04-12T11:05:49Z\"," +
                "        \"links\":{" +
                "          \"users\":[" +
                "            {" +
                "              \"id\":143255," +
                "              \"first_name\":\"Techery\"," +
                "              \"last_name\":\"Test\"," +
                "              \"username\":\"65664267\"," +
                "              \"badges\":[" +
                "              ]," +
                "              \"avatar\":{" +
                "                \"original\":\"http://s3-us-west-2.amazonaws.com/dtappstg/avatars/143255/original/1455543632_20160203_150955.jpg\"," +
                "                \"medium\":\"http://s3-us-west-2.amazonaws.com/dtappstg/avatars/143255/medium/1455543632_20160203_150955.jpg\"," +
                "                \"thumb\":\"http://s3-us-west-2.amazonaws.com/dtappstg/avatars/143255/thumb/1455543632_20160203_150955.jpg\"" +
                "              }," +
                "              \"location\":null," +
                "              \"company\":\"\"" +
                "            }" +
                "          ]" +
                "        }," +
                "        \"item\":{" +
                "          \"uid\":\"p1JoS2VWdc\"," +
                "          \"description\":\"Test post\"," +
                "          \"liked\":false," +
                "          \"likes_count\":0," +
                "          \"created_at\":\"2016-04-12T11:05:49Z\"," +
                "          \"updated_at\":\"2016-04-12T11:05:49Z\"," +
                "          \"comments_count\":0," +
                "          \"comments\":[" +
                "          ]," +
                "          \"user\":{" +
                "            \"id\":143255," +
                "            \"first_name\":\"Techery\"," +
                "            \"last_name\":\"Test\"," +
                "            \"username\":\"65664267\"," +
                "            \"badges\":[" +
                "            ]," +
                "            \"avatar\":{" +
                "              \"original\":\"http://s3-us-west-2.amazonaws.com/dtappstg/avatars/143255/original/1455543632_20160203_150955.jpg\"," +
                "              \"medium\":\"http://s3-us-west-2.amazonaws.com/dtappstg/avatars/143255/medium/1455543632_20160203_150955.jpg\"," +
                "              \"thumb\":\"http://s3-us-west-2.amazonaws.com/dtappstg/avatars/143255/thumb/1455543632_20160203_150955.jpg\"" +
                "            }," +
                "            \"location\":null," +
                "            \"company\":\"\"" +
                "          }" +
                "        }" +
                "      }" +
                "    ]" +
                "  }" +
                "]";

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory("unknown"))
                .registerTypeAdapter(Date.class, new DateTimeDeserializer())
                .registerTypeAdapter(Date.class, new DateTimeSerializer())
                .registerTypeAdapter(FeedItem.class, new FeedItemDeserializer())
                .registerTypeAdapter(FeedEntityHolder.class, new FeedEntityDeserializer())
                .create();

        ArrayList<ParentFeedItem> result = gson.fromJson(json, new TypeToken<ArrayList<ParentFeedItem>>() {
        }.getType());

        if (result.get(0).getItems().get(0).getType() == FeedEntityHolder.Type.POST) {
            List<FeedEntityHolder> attach = new ArrayList<>();

            Image image = new Image();
            image.setUrl("http://i.imgur.com/GhTzf0U.jpg");

            Photo photo = new Photo();
            photo.setImages(image);
            photo.setWidth(640);
            photo.setHeight(480);

            PhotoFeedItem photoFeedItem = new PhotoFeedItem();
            photoFeedItem.setItem(photo);
            photoFeedItem.setType(FeedEntityHolder.Type.PHOTO);
            photoFeedItem.setId(1);

            attach.add(photoFeedItem);
            ((TextualPost) result.get(0).getItems().get(0).getItem()).setAttachments(attach);
        }
        result.addAll(target);
        return result;
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_feed;
    }
}
