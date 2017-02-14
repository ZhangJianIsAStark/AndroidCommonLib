package stark.a.is.zhang.photogallery;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import stark.a.is.zhang.photogallery.model.GalleryItem;

public class PhotoGalleryFragment extends Fragment{
    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mGalleryItems = new ArrayList<>();

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    public static final int mLoaderId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        LoaderManager.LoaderCallbacks<List<GalleryItem>> callback =
                new LoaderManager.LoaderCallbacks<List<GalleryItem>>() {
            @Override
            public Loader<List<GalleryItem>> onCreateLoader(int id, Bundle args) {
                if (id == mLoaderId) {
                    return new FetchItemsLoader(getActivity());
                } else {
                    return null;
                }
            }

            @Override
            public void onLoadFinished(Loader<List<GalleryItem>> loader, List<GalleryItem> data) {
                if (loader.getId() == mLoaderId) {
                    mGalleryItems = data;
                    setupAdapter();
                }
            }

            @Override
            public void onLoaderReset(Loader<List<GalleryItem>> loader) {
            }
        };

        getLoaderManager().restartLoader(mLoaderId, null, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        return v;
    }

    //网络连接性判断

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private GalleryItem mGalleryItem;
        private TextView mTextView;

        PhotoHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }

        void bindGalleryItem(GalleryItem item) {
            mGalleryItem = item;
            mTextView.setText(mGalleryItem.getUrl());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;

        PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PhotoHolder(new TextView(getActivity()));
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            holder.bindGalleryItem(mGalleryItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mGalleryItems));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private static class FetchItemsLoader extends AsyncTaskLoader<List<GalleryItem>> {
        FetchItemsLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<GalleryItem> loadInBackground() {
            return new ImageFetcher().fetchItems();
        }
    }
}