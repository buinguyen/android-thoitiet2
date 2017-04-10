package nguyenbnt.app.ttvn.model.utils;

import android.content.Context;
import android.util.Log;

import nguyenbnt.app.ttvn.R;

/**
 * Created by 4000225 on 4/5/2017.
 */

public class Utils {
    private static final String Tag = "Utils";

    public static String genCitySearchUrl(Context context, String keysearch) {
        final String key = keysearch.replace(" ", "%20");
        final String url = "http://dataservice.accuweather.com/locations/v1/search?apikey="
                + context.getString(R.string.my_key) + "&q=" + keysearch;
        Log.d(Tag, url);
        return url;
    }

    public static String genWeatherUrl(Context context, String locationKey) {
        final String url = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/"
                + locationKey + "?apikey=" + context.getString(R.string.my_key) + "&language=vi-vn&details=false";
        Log.d(Tag, url);
        return url;
    }
}
