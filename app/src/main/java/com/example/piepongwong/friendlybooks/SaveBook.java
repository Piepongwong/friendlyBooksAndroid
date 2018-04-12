package com.example.piepongwong.friendlybooks;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Piepongwong on 10-4-2018.
 */

public class SaveBook {
    Context context;
    Uri uri;
    JSONObject firstResponse;
    String thumbnailUrl;
    String uniqueID = UUID.randomUUID().toString();

    SaveBook(Context theContext, JSONObject firstApiResponse){
        context = theContext;
        Log.i("DEBUG?", "Zeeea");
        firstResponse = firstApiResponse;
        try {
            thumbnailUrl = firstApiResponse.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("smallThumbnail");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void downloadImageAndSave() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        try {
            JSONArray items = firstResponse.getJSONArray("items");
            JSONObject item = items.getJSONObject(0);
            JSONObject info = item.getJSONObject("volumeInfo");
            String title = info.getString("title");
            JSONArray authors = info.getJSONArray("authors");
            String author = authors.getString(0);
            String description = info.getString("description");
            String isbn_10 = info.getJSONArray("industryIdentifiers").getJSONObject(0).getString("identifier");
            String isbn_13 = info.getJSONArray("industryIdentifiers").getJSONObject(1).getString("identifier");
            String smallThumbnail = "";
            String thumbnail = "";
            if(info.has("imageLinks")) {
                smallThumbnail = info.getJSONObject("imageLinks").getString("smallThumbnail");
                thumbnail = info.getJSONObject("imageLinks").getString("thumbnail");
            }
            // Save this downloaded bitmap to internal storage
            BooksDbHelper mDbHelper = new BooksDbHelper(context);
            mDbHelper.createBook(title, author, description, isbn_10, isbn_13, thumbnail, smallThumbnail, uniqueID);
            Toast.makeText(context, title, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ImageRequest imageRequest = new ImageRequest(
                thumbnailUrl, // Image URL
                new Response.Listener<Bitmap>() { // Bitmap listener
                    @Override
                    public void onResponse(Bitmap response) {
                        uri = saveImageToInternalStorage(response);
                }
            },
            0, // Image width
            0, // Image height
            ImageView.ScaleType.CENTER_CROP, // Image scale type
            Bitmap.Config.RGB_565, //Image decode configuration
            new Response.ErrorListener() { // Error listener
                @Override
                public void onErrorResponse(VolleyError error) {
                // Do something with error response
                error.printStackTrace();
                }
            }
        );
        requestQueue.add(imageRequest);
    }

    // Custom method to save a bitmap into internal storage
    protected Uri saveImageToInternalStorage(Bitmap bitmap){
        // Initialize ContextWrapper
        ContextWrapper wrapper = new ContextWrapper(context);
        // Initializing a new file
        File file = wrapper.getDir(context.getResources().getString(R.string.thumbnailFolder),MODE_PRIVATE);
        // Create a file to save the image
        file = new File(file, uniqueID +".jpg");
        try{
            // Initialize a new OutputStream
            OutputStream stream = null;
            // If the output file exists, it can be replaced or appended to it
            stream = new FileOutputStream(file);
            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            // Flushes the stream
            stream.flush();
            stream.close();
        }catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }
        // Parse the gallery image url to uri
        Uri savedImageURI = Uri.parse(file.getAbsolutePath());
        return savedImageURI;
    }

}
