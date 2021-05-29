package com.example.whowroteit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.whowroteit.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private com.example.whowroteit.databinding.ActivityMainBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        if(getSupportLoaderManager().getLoader(0)!=null){
            getSupportLoaderManager().initLoader(0,null,this);
        }
    }

    public void searchBooks(View view) {
        String queryString = bind.bookInput.getText().toString();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null ) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected() && queryString.length() != 0) {
          /*
          AsyncTask   
          new FetchBook(bind.titleText, bind.authorText).execute(queryString);
          */
            Bundle b = new Bundle();
            b.putString("query",queryString);
            getSupportLoaderManager().restartLoader(0,b,this);
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

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String query = null;
        if (args!=null){
            query = args.getString("query");
        }
        return  new BookLoader(this,query);
    }

    @Override
    public void onLoadFinished(@NonNull  Loader<String> loader, String data) {
        try {
            // Convert the response into a JSON object.
            JSONObject jsonObject = new JSONObject(data);
            // Get the JSONArray of book items.
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            // Initialize iterator and results fields.
            int i = 0;
            String title = null;
            String authors = null;

            // Look for results in the items array, exiting
            // when both the title and author
            // are found or when all items have been checked.
            while (i < itemsArray.length() && (authors == null && title == null)) {
                // Get the current item information.
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Move to the next item.
                i++;
            }

            // If both are found, display the result.
            if (title != null && authors != null) {
                bind.titleText.setText(title);
                bind.authorText.setText(authors);
            } else {
                // If none are found, update the UI to
                // show failed results.
                bind.titleText.setText(R.string.no_results);
                bind.authorText.setText("");
            }

        } catch (Exception e) {
            // If onPostExecute does not receive a proper JSON string,
            // update the UI to show failed results.
            bind.titleText.setText(R.string.no_results);
            bind.authorText.setText("");
        }
    }

    @Override
    public void onLoaderReset(@NonNull  Loader<String> loader) {

    }
}