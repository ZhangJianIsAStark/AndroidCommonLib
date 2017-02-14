package stark.a.is.zhang.photogallery;

import android.net.Uri;
import android.util.Log;

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
    public List<GalleryItem> fetchItems() {
        List<GalleryItem> items = new ArrayList<>();

        try {
            String url = Uri.parse("http://image.baidu.com/search/index?")
                    .buildUpon()
                    .appendQueryParameter("tn", "resultjson")
                    .appendQueryParameter("word", "微距摄影")
                    .build().toString();

            String jsonString = HttpUtil.getUrlString(url);

            Gson gson = new Gson();

            Response response = gson.fromJson(jsonString, Response.class);
            parseItems(items, response);
        } catch (JSONException | IOException e) {
                e.printStackTrace();
        }

        return items;
    }

    private void parseItems(List<GalleryItem> items, Response response)
            throws IOException, JSONException {
        List<Data> dataList = response.getData();
        for (Data data : dataList) {
            GalleryItem item = new GalleryItem();
            item.setUrl(data.getObjURL());

            items.add(item);
        }
    }
}