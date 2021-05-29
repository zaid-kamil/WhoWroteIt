package com.example.whowroteit;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class BookLoader extends AsyncTaskLoader<String> {

    private final String query;

    public BookLoader(@NonNull Context context, String query) {
        super(context);
        this.query = query;
    }

    @Nullable
    @Override
    public String loadInBackground() {
        return NetworkUtils.getBookInfo(query);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad(); // call loadInBackground
    }
}
