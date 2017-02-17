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
    public List<GalleryItem> fetchItems(int page) {
        List<GalleryItem> items = new ArrayList<>();

        try {
            String url = Uri.parse("http://image.baidu.com/search/index?")
                    .buildUpon()
                    .appendQueryParameter("tn", "resultjson")
                    .appendQueryParameter("word", "海贼王")
                    .appendQueryParameter("pn", "" + page)
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