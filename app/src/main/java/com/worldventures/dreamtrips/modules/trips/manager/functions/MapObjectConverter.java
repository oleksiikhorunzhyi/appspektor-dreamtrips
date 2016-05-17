package com.worldventures.dreamtrips.modules.trips.manager.functions;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

import com.worldventures.dreamtrips.modules.trips.api.GetMapObjectsAction;
import com.worldventures.dreamtrips.modules.trips.model.MapObject;
import com.worldventures.dreamtrips.modules.trips.view.util.TripPinFactory;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class MapObjectConverter implements Func1<GetMapObjectsAction, Observable<List<Pair<Bitmap, MapObject>>>> {

    private Context context;

    public MapObjectConverter(Context context) {
        this.context = context;
    }

    @Override
    public Observable<List<Pair<Bitmap, MapObject>>> call(GetMapObjectsAction getMapObjectsAction) {
        return Observable.from(getMapObjectsAction.getMapObjects())
                .map(mapObjectHolder -> new Pair<>(TripPinFactory
                        .createPinBitmapFromMapObject(context, mapObjectHolder), mapObjectHolder.getItem()))
                .toList();
    }
}
