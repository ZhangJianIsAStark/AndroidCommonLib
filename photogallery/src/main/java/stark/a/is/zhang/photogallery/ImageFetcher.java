package stark.a.is.zhang.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import stark.a.is.zhang.photogallery.model.GalleryItem;
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

            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (JSONException | IOException e) {
                e.printStackTrace();
        }

        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody)
            throws IOException, JSONException {
        JSONArray dataArray = jsonBody.getJSONArray("data");
        for (int i = 0; i < dataArray.length(); ++i) {
            JSONObject dataObject = dataArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setUrl(dataObject.getString("objURL"));

            items.add(item);
        }
    }
}