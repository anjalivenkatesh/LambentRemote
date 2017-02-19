package com.lambent.lambentremote.common;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anjali on 10/20/16.
 */
public class ParcelableUtils {

    public static void writeParcelablesListHashmapToParcel(Parcel dest, HashMap<String, List<Parcelable>> hashMap, int flags) {
        final int hashMapSize = hashMap.size();
        dest.writeInt(hashMapSize);

        for (Map.Entry<String, List<Parcelable>> entry : hashMap.entrySet()) {
            dest.writeString(entry.getKey());
            List<Parcelable> list = entry.getValue();
            dest.writeTypedList(list);
        }
    }

    public static HashMap<String, List<Parcelable>> readParcelablesListHashmapToParcel(Parcel source, Parcelable.Creator<Object> creator) {
        HashMap<String, List<Parcelable>> hashMap = new HashMap<>();
        final int hashMapSize = source.readInt();

        for (int i = 0; i < hashMapSize; i++) {
            String key = source.readString();
            List<Object> list = new ArrayList<>();
            source.readTypedList(list, creator);
            //TODO
        }

        return hashMap;
    }
}
