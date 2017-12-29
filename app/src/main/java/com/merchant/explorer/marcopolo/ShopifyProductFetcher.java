package com.merchant.explorer.marcopolo;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ShopifyProductFetcher {

    private static final String TAG = "ShopifyProductFetcher";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchItems() {

        List<GalleryItem> items = new ArrayList<>();

        try {
            String url = Uri.parse("https://shopicruit.myshopify.com/admin/products.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6")
                    .buildUpon()
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody)
            throws IOException, JSONException {
        JSONArray productsJsonArray = jsonBody.getJSONArray("products");
        for (int i = 0; i < productsJsonArray.length(); i++) {
            JSONObject productJsonObject = productsJsonArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            item.setId(productJsonObject.getString("id"));
            item.setCaption(productJsonObject.getString("title"));
            item.setDescriptionFull(productJsonObject.getString("vendor"));

            // Feels very repetitive, expensive and inefficient
            // Add a todo for optimization
            // Or find a library that does this if pushing to production
            // P.S. There are tonnes of libraries
            JSONObject itemJsonObject = (JSONObject) productJsonObject.get("image");
            item.setUrl(itemJsonObject.getString("src"));


            items.add(item);
        }
    }
}
