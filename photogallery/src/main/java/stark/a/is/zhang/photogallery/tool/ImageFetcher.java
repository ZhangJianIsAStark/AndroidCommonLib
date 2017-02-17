package stark.a.is.zhang.photogallery.tool;

import android.net.Uri;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import stark.a.is.zhang.photogallery.model.Data;
import stark.a.is.zhang.photogallery.model.GalleryItem;
import stark.a.is.zhang.photogallery.model.Response;
import stark.a.is.zhang.utils.HttpUtil;

public class ImageFetcher {
    public List<GalleryItem> fetchDefaultPhotos(int page) {
        String url = buildUrl(page, null);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(int page, String query) {
        String url = buildUrl(page, query);
        return downloadGalleryItems(url);
    }

    private List<GalleryItem> downloadGalleryItems(String url) {
        List<GalleryItem> items = new ArrayList<>();

        try {
            String jsonString = HttpUtil.getUrlString(url);

            Gson gson = new Gson();
            Response response = gson.fromJson(jsonString, Response.class);

            parseItems(items, response);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return items;
    }

    private String buildUrl(int page, String word) {
        Uri.Builder uriBuilder = Uri.parse("http://image.baidu.com/search/index?").buildUpon();
        uriBuilder.appendQueryParameter("tn", "resultjson");
        uriBuilder.appendQueryParameter("pn", "" + page);

        if (word != null) {
            uriBuilder.appendQueryParameter("word", word);
        } else {
            uriBuilder.appendQueryParameter("word", "极简");
        }

        return uriBuilder.build().toString();
    }

    private void parseItems(List<GalleryItem> items, Response response)
            throws IOException, JSONException {
        if (response == null) {
            return;
        }

        List<Data> dataList = response.getData();

        for (Data data : dataList) {
            if (data.getThumbURL() == null) {
                continue;
            }

            GalleryItem item = new GalleryItem();

            item.setFromURLHost(data.getFromURLHost());
            item.setObjURL(data.getObjURL());
            item.setThumbURL(data.getThumbURL());

            items.add(item);
        }
    }
}