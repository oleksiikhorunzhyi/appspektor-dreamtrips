package com.worldventures.dreamtrips.modules.trips.manager.functions;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

import com.worldventures.dreamtrips.modules.trips.api.GetMapObjectsHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.MapObject;
import com.worldventures.dreamtrips.modules.trips.view.util.TripPinFactory;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class MapObjectConverter implements Func1<GetMapObjectsHttpAction, Observable<List<Pair<Bitmap, MapObject>>>> {

    private Context context;

    public MapObjectConverter(Context context) {
        this.context = context;
    }

    @Override
    public Observable<List<Pair<Bitmap, MapObject>>> call(GetMapObjectsHttpAction getMapObjectsHttpAction) {
        return Observable.from(getMapObjectsHttpAction.getMapObjects())
                .map(mapObjectHolder -> new Pair<>(TripPinFactory
                        .createPinBitmapFromMapObject(context, mapObjectHolder), mapObjectHolder.getItem()))
                .toList();
    }
}
