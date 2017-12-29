package com.merchant.explorer.marcopolo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarcoPoloStoreFrontFragment extends Fragment {

    private static final String TAG = "MarcoPolo";

    private RecyclerView mMarcoPoloStoreFrontRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<ProductHolder> mThumbnailDownloader;


    public static MarcoPoloStoreFrontFragment newInstance() {
        return new MarcoPoloStoreFrontFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<ProductHolder>() {
                    @Override
                    public void onThumbnailDownloaded(ProductHolder productHolder, Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        productHolder.bindDrawable(drawable);
                    }
                }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_marco_polo_store_front_activity, container, false);
        mMarcoPoloStoreFrontRecyclerView = (RecyclerView) v
                .findViewById(R.id.fragment_marco_polo_store_front_recycler_view);
        mMarcoPoloStoreFrontRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        setupAdapter();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private void setupAdapter() {
        if (isAdded()) {
            mMarcoPoloStoreFrontRecyclerView.setAdapter(new ProductAdapter(mItems));
        }
    }

    private class ProductHolder extends RecyclerView.ViewHolder {
        private TextView mProductTitleTextView;
        private TextView mProductDescriptionTextView;
        private ImageView mItemImageView;
        public ProductHolder(View itemView) {
            super(itemView);

            mProductTitleTextView = (TextView) itemView.findViewById(R.id.product_title_text_view);
            mProductDescriptionTextView = (TextView) itemView.findViewById(R.id.product_description_text_view);
            mItemImageView = (ImageView) itemView.findViewById(R.id.fragment_product_gallery_image_view);
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }

        public void bindGalleryItem(GalleryItem item) {
            mProductTitleTextView.setText(item.toString());
            mProductDescriptionTextView.setText(item.getDescriptionFull());
        }
    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductHolder> {
        private List<GalleryItem> mGalleryItems;
        public ProductAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }
        @Override
        public ProductHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, viewGroup, false);
            return new ProductHolder(view);
        }
        @Override
        public void onBindViewHolder(ProductHolder productHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            productHolder.bindGalleryItem(galleryItem);
            Drawable placeholderImage = getResources().getDrawable(R.drawable.aint_no_telling);
            // Just an image to keep the user occupied
            // As product images are fetched from Shopify's CDN
            productHolder.bindDrawable(placeholderImage);
            mThumbnailDownloader.queueThumbnail(productHolder, galleryItem.getUrl());
        }
        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new ShopifyProductFetcher().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }
    }
}
