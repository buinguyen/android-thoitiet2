package nguyenbnt.app.ttvn;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import nguyenbnt.app.ttvn.model.City;
import nguyenbnt.app.ttvn.model.accuweather.DailyForecasts;
import nguyenbnt.app.ttvn.model.accuweather.Headline;
import nguyenbnt.app.ttvn.model.utils.Parser;
import nguyenbnt.app.ttvn.model.utils.Utils;

/**
 * Created by 4000225 on 4/5/2017.
 */

public class QueryWeatherTask extends AsyncTask<City, Void, Void> {
    private final String Tag = this.getClass().getSimpleName();

    private Headline headline = null;
    List<DailyForecasts> dailyForecasts = null;
    private Context mContext;
    private IGetWeatherInfo mListener;

    public QueryWeatherTask(Context context) {
        mContext = context;
        mListener = (IGetWeatherInfo) context;
    }

    @Override
    protected Void doInBackground(City... params) {
        Log.d(Tag, "start");
        if (params == null && params.length == 0) {
            return null;
        }

        try {
            SyncHttpClient client = new SyncHttpClient();
            client.get(Utils.genWeatherUrl(mContext, params[0].key), null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    if (response == null) {
                        return;
                    }
                    try {
                        headline = Parser.parseHeadline(response.getString("Headline"));
                        dailyForecasts = Parser.parseDailyForecasts(response.getString("DailyForecasts"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(Tag, "onSuccess 1");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                }
            });
        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        Log.d(Tag, "onPostExecute");
        if (mListener != null) {
            mListener.onResult(headline, dailyForecasts);
        }
    }

    public interface IGetWeatherInfo {
        void onResult(Headline headline, List<DailyForecasts> dailyForecastses);
    }
}
