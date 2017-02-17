package stark.a.is.zhang.photogallery.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import stark.a.is.zhang.photogallery.R;
import stark.a.is.zhang.photogallery.model.GalleryItem;
import stark.a.is.zhang.photogallery.tool.ImageFetcher;
import stark.a.is.zhang.photogallery.tool.QueryPreference;
import stark.a.is.zhang.photogallery.tool.ThumbnailDownloader;

public class PhotoGalleryFragment extends Fragment{
    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    LoaderManager.LoaderCallbacks<List<GalleryItem>> mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mCallback = new LoaderCallback();
        createThumbnailDownLoader();

        if (isConnectedToNetwork()) {
            startURLLoader(buildLoaderArgs());
        } else {
            Toast.makeText(getActivity(), "Please Connect to Internet", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= 21) {
            registerNetworkCallBack();
        }
    }

    private ConnectivityManager mConMgr;

    private boolean isConnectedToNetwork() {
        mConMgr = (ConnectivityManager) getActivity().getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo =  mConMgr.getActiveNetworkInfo();

        return (networkInfo != null) && networkInfo.isConnected();
    }

    private static final int mLoaderId = 0;
    private static String PAGE = "page";
    private int mPage = 0;

    private List<GalleryItem> mGalleryItems = new ArrayList<>();

    Handler mMainHandler = new Handler();

    private class LoaderCallback implements LoaderManager.LoaderCallbacks<List<GalleryItem>> {
        @Override
        public Loader<List<GalleryItem>> onCreateLoader(int id, Bundle args) {
            if (id == mLoaderId) {
                return new FetchItemsLoader(getActivity(), args);
            } else {
                return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<List<GalleryItem>> loader, List<GalleryItem> data) {
            if (loader.getId() == mLoaderId) {
                mGalleryItems.addAll(data);

                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setupAdapter();

                        removeDialog();
                    }
                });
            }
        }

        @Override
        public void onLoaderReset(Loader<List<GalleryItem>> loader) {
        }
    }

    private Bundle buildLoaderArgs() {
        Bundle args = new Bundle();
        args.putInt(PAGE, mPage);

        return args;
    }

    private void startURLLoader(Bundle args) {
        getLoaderManager().restartLoader(mLoaderId, args, mCallback);
    }

    private static class FetchItemsLoader extends AsyncTaskLoader<List<GalleryItem>> {
        private int mConvertPage;

        FetchItemsLoader(Context context, Bundle args) {
            super(context);
            //Just for baidu image search use, the gap between two pages is 20
            int page = args.getInt(PAGE, 0);
            mConvertPage = page * 20;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<GalleryItem> loadInBackground() {
            String word = QueryPreference.getStoredQuery(getContext());

            if (word == null) {
                return new ImageFetcher().fetchDefaultPhotos(mConvertPage);
            } else {
                return new ImageFetcher().searchPhotos(mConvertPage, word);
            }
        }
    }

    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    private void createThumbnailDownLoader() {
        mThumbnailDownloader = new ThumbnailDownloader<>(mMainHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
                    @Override
                    public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail, int tagId) {
                        Drawable drawable= new BitmapDrawable(getResources(), thumbnail);
                        target.bindDrawable(drawable, tagId);
                    }
                }
        );

        mThumbnailDownloader.start();
        //make sure that looper is prepared
        mThumbnailDownloader.getLooper();
    }

    private ConnectivityManager.NetworkCallback mNetworkCallback;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void registerNetworkCallBack() {
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        mNetworkCallback = new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(Network network) {
                startURLLoader(buildLoaderArgs());
            }
        };

        mConMgr.registerNetworkCallback(networkRequest, mNetworkCallback);
    }

    private RecyclerView mPhotoRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);

        dynamicAdjustLayoutManager();

        addOnScrollListener();

        setupAdapter();

        return v;
    }

    private void dynamicAdjustLayoutManager() {
        ViewTreeObserver viewTreeObserver = mPhotoRecyclerView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int mPrev = 0;
            private int mMinWidth = 400;

            @Override
            public void onGlobalLayout() {
                if (getView() != null) {
                    int count = getView().getWidth() / mMinWidth;

                    if (mPrev != count) {
                        mPhotoRecyclerView.setLayoutManager(
                                new GridLayoutManager(getActivity(), count));
                        mPrev = count;
                    }
                }
            }
        });
    }

    private void addOnScrollListener() {
        mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisibleItemPosition;
            private int firstVisibleItemPosition;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                if (layoutManager instanceof GridLayoutManager) {
                    lastVisibleItemPosition = ((GridLayoutManager) layoutManager)
                            .findLastVisibleItemPosition();

                    firstVisibleItemPosition = ((GridLayoutManager) layoutManager)
                            .findFirstVisibleItemPosition();

                    mLastPosition = lastVisibleItemPosition - layoutManager.getChildCount()/2;
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();

                if (newState == RecyclerView.SCROLL_STATE_IDLE && visibleItemCount > 0) {
                    int firstToPreloadPosition = firstVisibleItemPosition - 10;

                    int lastToPreloadPosition = lastVisibleItemPosition + 10;

                    if ((lastVisibleItemPosition == totalItemCount - 1)
                            || (lastToPreloadPosition >= totalItemCount)){
                        ++mPage;
                        startURLLoader(buildLoaderArgs());
                    } else if (firstVisibleItemPosition == 0) {
                        Toast.makeText(getActivity(),
                                "Already to the top",
                                Toast.LENGTH_SHORT)
                                .show();
                    }

                    if (firstToPreloadPosition < 0) {
                        firstToPreloadPosition = 0;
                    }

                    for (int i = firstVisibleItemPosition - 1; i >= firstToPreloadPosition; --i) {
                        mThumbnailDownloader.queueThumbnail(mGalleryItems.get(i).getThumbURL());
                    }

                    if (lastToPreloadPosition < totalItemCount) {
                        for (int i = lastVisibleItemPosition + 1; i <= lastToPreloadPosition; ++i) {
                            mThumbnailDownloader.queueThumbnail(mGalleryItems.get(i).getThumbURL());
                        }
                    }
                }
            }
        });
    }

    private int mLastPosition;

    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mGalleryItems));
            mPhotoRecyclerView.scrollToPosition(mLastPosition);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;

        PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);

            Drawable placeHolder;
            if (Build.VERSION.SDK_INT >= 21) {
                placeHolder = getResources().getDrawable(R.drawable.place_holder,
                        getActivity().getTheme());
            } else {
                placeHolder = getResources().getDrawable(R.drawable.place_holder);
            }
            holder.bindDrawable(placeHolder, position);

            mThumbnailDownloader.queueThumbnail(
                    holder, galleryItem.getThumbURL(), position);
            //holder.bindGalleryItem(galleryItem, position);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;
        private TextView mTextView;

        PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView = (ImageView) itemView.findViewById
                    (R.id.fragment_photo_gallery_image_view);
            mTextView = (TextView) itemView.findViewById(
                    R.id.fragment_photo_gallery_text_view);
        }

        void bindDrawable(Drawable drawable, int position) {
            mItemImageView.setImageDrawable(drawable);
            mTextView.setText(getString(R.string.text_title, "" + position));
        }

// A Test used Picasso Library to download pictures
//        void bindGalleryItem(GalleryItem galleryItem, int position) {
//            mTextView.setText(getString(R.string.text_title, "" + position));
//
//            Picasso.with(getActivity())
//                    .load(galleryItem.getThumbURL())
//                    .placeholder(R.drawable.place_holder)
//                    .into(mItemImageView);
//        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                //hide the input keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                //change the searchView back to an icon
                searchView.onActionViewCollapsed();

                String oldQuery = QueryPreference.getStoredQuery(getActivity());
                if (oldQuery != null && oldQuery.equals(query)) {
                    return true;
                }

                QueryPreference.setStoredQuery(getActivity(), query);
                reloadPictureAfterQueryChange();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreference.getStoredQuery(getContext());
                searchView.setQuery(query, false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreference.setStoredQuery(getActivity(), null);
                reloadPictureAfterQueryChange();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static String FRAGMENT_TAG = "progressDialog";

    private void reloadPictureAfterQueryChange() {
        mThumbnailDownloader.clearQueue();
        mLastPosition = 0;
        mGalleryItems.clear();

        showDialog();

        startURLLoader(buildLoaderArgs());
    }

    private void showDialog() {
        DialogFragment fragment = new ProgressDialogFragment();
        getFragmentManager().beginTransaction()
                .add(fragment, FRAGMENT_TAG)
                .commit();
    }

    private void removeDialog() {
        DialogFragment fragment = (DialogFragment) getFragmentManager()
                .findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) {
            fragment.dismiss();
        }
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
        if (mNetworkCallback != null && Build.VERSION.SDK_INT >= 21) {
            mConMgr.unregisterNetworkCallback(mNetworkCallback);
        }
    }
}