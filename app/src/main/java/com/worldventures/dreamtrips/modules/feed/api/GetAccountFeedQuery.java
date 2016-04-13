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
import com.worldventures.dreamtrips.modules.feed.view.custom.collage.CollageItem;
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

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_feed;
    }

    private ArrayList<ParentFeedItem> stubCollages(ArrayList<ParentFeedItem> target) {

        ArrayList<ParentFeedItem> result = new ArrayList<>();

        for (int i = 0; i < 17; i++) {
            ArrayList<ParentFeedItem> parent = getStubParentFeedItem();
            List<FeedEntityHolder> attach = new ArrayList<>();
            for (CollageItem item : getAttachData(i)) {
                attach.add(getAttachItem(i, item));
            }
            ((TextualPost) parent.get(0).getItems().get(0).getItem()).setAttachments(attach);
            result.addAll(parent);
        }

        result.addAll(target);
        return result;
    }

    private ArrayList<ParentFeedItem> getStubParentFeedItem() {
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

        return result;
    }

    private ArrayList<CollageItem> getAttachData(int index) {
        ArrayList<CollageItem> items = new ArrayList<>();
        switch (index) {
            case 0:
                items.add(new CollageItem(landscape[0], 640, 240));
                break;
            case 1:
                items.add(new CollageItem(landscape[0], 640, 600));
                break;
            case 2:
                items.add(new CollageItem(square[0], 640, 640));
                break;
            case 3:
                items.add(new CollageItem(portrait[0], 480, 640));
                break;
            case 4:
                items.add(new CollageItem(landscape[0], 640, 480));
                items.add(new CollageItem(landscape[1], 640, 480));
                break;
            case 5:
                items.add(new CollageItem(portrait[0], 480, 640));
                items.add(new CollageItem(portrait[1], 480, 640));
                break;
            case 6:
                items.add(new CollageItem(portrait[0], 480, 640));
                items.add(new CollageItem(square[0], 640, 480));
                break;
            case 7:
                items.add(new CollageItem(landscape[0], 640, 480));
                items.add(new CollageItem(landscape[1], 640, 480));
                items.add(new CollageItem(landscape[2], 640, 480));
                break;
            case 8:
                items.add(new CollageItem(portrait[0], 480, 640));
                items.add(new CollageItem(portrait[1], 480, 640));
                items.add(new CollageItem(portrait[2], 480, 640));
                break;
            case 9:
                items.add(new CollageItem(portrait[0], 480, 640));
                items.add(new CollageItem(portrait[0], 480, 640));
                items.add(new CollageItem(square[0], 480, 480));
                break;
            case 10:
                items.add(new CollageItem(landscape[0], 640, 480));
                items.add(new CollageItem(landscape[1], 640, 480));
                items.add(new CollageItem(landscape[2], 640, 480));
                items.add(new CollageItem(landscape[3], 640, 480));
                break;
            case 11:
                items.add(new CollageItem(portrait[0], 480, 640));
                items.add(new CollageItem(portrait[1], 480, 640));
                items.add(new CollageItem(portrait[2], 480, 640));
                items.add(new CollageItem(square[2], 480, 480));
                break;
            case 12:
                items.add(new CollageItem(square[2], 480, 480));
                items.add(new CollageItem(portrait[0], 480, 640));
                items.add(new CollageItem(portrait[1], 480, 640));
                items.add(new CollageItem(portrait[2], 480, 640));
                break;
            case 13:
                items.add(new CollageItem(landscape[0], 640, 480));
                items.add(new CollageItem(portrait[0], 480, 640));
                items.add(new CollageItem(square[0], 480, 480));
                items.add(new CollageItem(square[1], 480, 480));
                items.add(new CollageItem(square[2], 480, 480));
                break;
            case 14:
                items.add(new CollageItem(portrait[0], 480, 640));
                items.add(new CollageItem(square[1], 480, 480));
                items.add(new CollageItem(landscape[2], 640, 480));
                items.add(new CollageItem(landscape[3], 640, 480));
                items.add(new CollageItem(landscape[3], 640, 480));
                break;
            case 15:
                items.add(new CollageItem(square[2], 480, 480));
                items.add(new CollageItem(portrait[0], 480, 640));
                items.add(new CollageItem(portrait[1], 480, 640));
                items.add(new CollageItem(portrait[2], 480, 640));
                items.add(new CollageItem(portrait[3], 480, 640));
                break;
            case 16:
                items.add(new CollageItem(portrait[0], 480, 640));
                items.add(new CollageItem(portrait[1], 480, 640));
                items.add(new CollageItem(portrait[2], 480, 640));
                items.add(new CollageItem(square[1], 480, 480));
                items.add(new CollageItem(square[2], 480, 480));
                break;
            default:
                break;
        }
        return items;
    }

    private PhotoFeedItem getAttachItem(int magicNumber, CollageItem item) {
        Image image = new Image();
        image.setUrl(item.url());

        Photo photo = new Photo();
        photo.setImages(image);
        photo.setWidth(item.width());
        photo.setHeight(item.height());

        PhotoFeedItem photoFeedItem = new PhotoFeedItem();
        photoFeedItem.setItem(photo);
        photoFeedItem.setType(FeedEntityHolder.Type.PHOTO);
        photoFeedItem.setId(magicNumber);

        return photoFeedItem;
    }

    private String json = "[" +
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

    private String[] landscape = {
            "http://i.imgur.com/GhTzf0U.jpg",
            "http://i.imgur.com/2LG5wxB.jpg",
            "http://i.imgur.com/DzP0hbc.jpg",
            "http://i.imgur.com/SlQLU6N.jpg",
    };
    private String[] portrait = {
            "http://i.imgur.com/s6arY5h.jpg",
            "http://i.imgur.com/cqjjNHX.jpg",
            "http://i.imgur.com/RafEnde.jpg",
            "http://i.imgur.com/S6FD2Jr.jpg",
    };
    private String[] square = {
            "http://i.imgur.com/fXGAoRf.jpg",
            "http://i.imgur.com/GFfCQcf.jpg",
            "http://i.imgur.com/QiOJ9Y9.jpg",
            "http://i.imgur.com/lMwszDY.jpg",
    };
}
