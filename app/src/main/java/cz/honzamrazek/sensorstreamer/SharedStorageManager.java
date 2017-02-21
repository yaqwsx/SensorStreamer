package cz.honzamrazek.sensorstreamer;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import cz.honzamrazek.sensorstreamer.models.SharedStorage;

public class SharedStorageManager<T> {
    private Class<T> mClass;
    private SharedPreferences mPreferences;
    private String mKey;
    private List<T> mItems;

    public SharedStorageManager(Context context, Class<T> c) {
        mClass = c;
        String storageName = c.getAnnotation(SharedStorage.class).storageName();
        int version = c.getAnnotation(SharedStorage.class).storageVersion();

        mKey = c.getAnnotation(SharedStorage.class).keyName() + "_" + version;
        mPreferences = context.getSharedPreferences(storageName, Context.MODE_PRIVATE);
        mItems = new ArrayList<>();

        reload();
    }

    public void reload() {
        String json = mPreferences.getString(mKey, "[]");
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(json).getAsJsonArray();

        Gson gson = new Gson();
        mItems.clear();
        for (final JsonElement elem : array) {
            mItems.add(gson.fromJson(elem, mClass));
        }
    }

    public void commit() {
        SharedPreferences.Editor editor = mPreferences.edit();
        String json = new Gson().toJson(mItems);
        editor.putString(mKey, json);
        editor.apply();
    }

    public List<T> getItems() {
        return mItems;
    }

    public T get(int position) {
        return mItems.get(position);
    }

    public void remove(int position) {
        mItems.remove(position);
    }

    public void add(int position, T item) {
        mItems.add(position, item);
    }

    public void add(T item) {
        mItems.add(item);
    }
}