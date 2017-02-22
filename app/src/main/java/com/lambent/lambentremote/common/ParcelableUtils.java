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

    private static final int NULL_FLAG = -1;

    public static <T extends Parcelable> void writeParcelablesListHashmapToParcel(Parcel dest, HashMap<String, List<T>> hashMap) {
        if (hashMap == null) {
            dest.writeInt(NULL_FLAG);
            return;
        }

        final int hashMapSize = hashMap.size();
        dest.writeInt(hashMapSize);

        for (Map.Entry<String, List<T>> entry : hashMap.entrySet()) {
            dest.writeString(entry.getKey());
            List<T> list = entry.getValue();
            dest.writeTypedList(list);
        }
    }

    public static <T extends Parcelable> HashMap<String, List<T>> readParcelablesListHashmapToParcel(Parcel source, Parcelable.Creator<T> creator) {
        int num = source.readInt();

        if (num == NULL_FLAG) {
            return null;
        }

        HashMap<String, List<T>> hashMap = new HashMap<>(num);

        for (int i = 0; i < num; i++) {
            String key = source.readString();
            List<T> list = new ArrayList<>();
            source.readTypedList(list, creator);
            hashMap.put(key, list);
        }

        return hashMap;
    }
}
