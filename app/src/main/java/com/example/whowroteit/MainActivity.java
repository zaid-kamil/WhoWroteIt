package com.example.whowroteit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.whowroteit.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private com.example.whowroteit.databinding.ActivityMainBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
    }

    public void searchBooks(View view) {
        String queryString = bind.bookInput.getText().toString();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null ) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()
                && queryString.length() != 0) {
            new FetchBook(bind.titleText, bind.authorText).execute(queryString);
            bind.authorText.setText("");
            bind.titleText.setText(R.string.loading);
        } else {
            if (queryString.length() == 0) {
                bind.authorText.setText("");
                bind.titleText.setText(R.string.no_search_term);
            } else {
                bind.authorText.setText("");
                bind.titleText.setText(R.string.no_network);
            }
        }

    }
}