package nguyenbnt.app.ttvn.model.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import nguyenbnt.app.ttvn.model.accuweather.DailyForecasts;
import nguyenbnt.app.ttvn.model.accuweather.DayStatus;
import nguyenbnt.app.ttvn.model.accuweather.Headline;
import nguyenbnt.app.ttvn.model.accuweather.Temperature;

/**
 * Created by 4000225 on 4/5/2017.
 */

public class Parser {
    public static Headline parseHeadline(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);

        Headline headline = null;
        if (jsonObject != null) {
            headline = new Headline();
            headline.EffectiveDate = jsonObject.getString("EffectiveDate");
            headline.Severity = jsonObject.getString("Severity");
            headline.Text = jsonObject.getString("Text");
            headline.Category = jsonObject.getString("Category");
            headline.EndDate = jsonObject.getString("EndDate");
        }
        return headline;
    }

    public static List<DailyForecasts> parseDailyForecasts(String data) throws JSONException {
        JSONArray jsonArray = new JSONArray(data);

        List<DailyForecasts> dailyForecasts = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                DailyForecasts dailyForecast = new DailyForecasts();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                dailyForecast.Date = jsonObject.getString("Date");
                dailyForecast.Temperature = parseTemperature(jsonObject.getString("Temperature"));
                dailyForecast.Day = parseDayStatus(jsonObject.getString("Day"));
                dailyForecast.Night = parseDayStatus(jsonObject.getString("Night"));
                dailyForecasts.add(dailyForecast);
            }
        }
        return dailyForecasts;
    }

    private static String toCelsius(String fahrenheit) {
        String celsiusStr = "";
        if (fahrenheit.isEmpty()) {
            return celsiusStr;
        }
        try {
            float fah = Float.parseFloat(fahrenheit);
            celsiusStr = String.valueOf(Math.round((fah - 32) * 10 / 1.8) / 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return celsiusStr;
    }

    public static Temperature parseTemperature(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);

        Temperature temperature = null;
        if (jsonObject != null) {
            String min = new JSONObject(jsonObject.getString("Minimum")).getString("Value");
            String minUnitType = new JSONObject(jsonObject.getString("Minimum")).getString("UnitType");
            String max = new JSONObject(jsonObject.getString("Maximum")).getString("Value");
            String maxUnitType = new JSONObject(jsonObject.getString("Maximum")).getString("UnitType");

            temperature = new Temperature();
            if (minUnitType.equals("18")) { // Fahrenheit Unit is 18, http://developer.accuweather.com/unit-types
                temperature.Minimum = toCelsius(min);
            } else {
                temperature.Minimum = min;
            }
            if (maxUnitType.equals("18")) {
                temperature.Maximum = toCelsius(max);
            } else {
                temperature.Maximum = max;
            }
            temperature.Unit = "17"; // Celsius Unit is 17, http://developer.accuweather.com/unit-types

        }
        return temperature;
    }

    public static DayStatus parseDayStatus(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);

        DayStatus dayStatus = null;
        if (jsonObject != null) {
            dayStatus = new DayStatus();
            dayStatus.Icon = jsonObject.getString("Icon");
            dayStatus.IconPhrase = jsonObject.getString("IconPhrase");
        }
        return dayStatus;
    }
}
