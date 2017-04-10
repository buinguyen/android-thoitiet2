package nguyenbnt.app.ttvn;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import nguyenbnt.app.ttvn.model.City;
import nguyenbnt.app.ttvn.model.DayInfoAdapter;
import nguyenbnt.app.ttvn.model.DividerItemDecoration;
import nguyenbnt.app.ttvn.model.accuweather.DailyForecasts;
import nguyenbnt.app.ttvn.model.accuweather.Headline;

public class MainActivity extends AppCompatActivity implements QueryWeatherTask.IGetWeatherInfo {

    private final String Tag = this.getClass().getSimpleName();

    private List<City> mCities = new ArrayList<>();
    private List<DailyForecasts> mDayInfoList = new ArrayList<>();
    private DayInfoAdapter mDayInfoAdapter;
    private boolean isRunning = false;
    private ProgressDialog mLoadingDialog;

    @Bind(R.id.spn_area)
    MaterialSpinner mSpnArea;
    @Bind(R.id.spn_province)
    MaterialSpinner mSpnProvince;
    @Bind(R.id.rv_weather)
    RecyclerView mRcvWeather;
    @Bind(R.id.id_label)
    TextView mTvLabel;
    @Bind(R.id.id_content)
    TextView mTvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initProvinces("province");
        initSpinners();

        // Init recycle view
        mDayInfoAdapter = new DayInfoAdapter(this, mDayInfoList);
//        mRcvWeather = (RecyclerView) findViewById(R.id.rv_weather);
        mRcvWeather.setHasFixedSize(true);
        mRcvWeather.addItemDecoration(new DividerItemDecoration(this));
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvWeather.setLayoutManager(llm);
        mRcvWeather.setAdapter(mDayInfoAdapter);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        mRcvWeather.setItemAnimator(itemAnimator);
    }

    /**
     * To load JSON string from text file in asset folder
     *
     * @param pFilePath asset file path
     * @return String - json string
     */
    public void initProvinces(String pFilePath) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("province")));
            String line = null;
            while ((line = reader.readLine()) != null) {
                Log.d(Tag, line);
                String[] cityInfo = line.split("---");
                if (cityInfo.length >= 2) {
                    mCities.add(new City(cityInfo[0], cityInfo[1]));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                reader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void initSpinners() {
        List<String> provinceList = new ArrayList<String>();
        provinceList.add("- Tỉnh/thành phố -");
        for (City city : mCities) {
            provinceList.add(city.name);
        }
        mSpnProvince.setItems(provinceList);
        mSpnProvince.setSelectedIndex(0);
        mSpnProvince.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                onItemSelectedSpnProvince(view, position);
            }
        });
    }

    protected void onItemSelectedSpnProvince(MaterialSpinner spinner, int position) {
        reset();

        if (position <= 0) {
            Log.e(Tag, "No province is selected");
            return;
        }
        if (isNetworkConnected() == false) {
            Toast.makeText(getApplicationContext(), "No connect Internet", Toast.LENGTH_SHORT).show();
            Log.e(Tag, "No connect Internet");
            return;
        }
        if (isRunning) {
            Toast.makeText(getApplicationContext(), "Running...", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingDialog();

        for (City province : mCities) {
            if (province.name.equals(mSpnProvince.getItems().get(position))) {
                isRunning = true;
                new QueryWeatherTask(this).execute(province);
                break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {

        }
        return super.onOptionsItemSelected(item);
    }

    private void reset() {
        mDayInfoList.clear();
        mDayInfoAdapter.notifyDataSetChanged();
        mTvLabel.setVisibility(View.INVISIBLE);
        mTvContent.setText("");
    }

    public void updateData(Headline headline, List<DailyForecasts> dayInfos) {
        mDayInfoList.clear();
        isRunning = false;

        // update headline
        mTvLabel.setVisibility(View.VISIBLE);
        mTvContent.setText(headline.Text);

        // update list
        mDayInfoList.addAll(dayInfos);
        mDayInfoAdapter.notifyDataSetChanged();
        mRcvWeather.setAdapter(mDayInfoAdapter);
    }

    @Override
    public void onResult(Headline headline, List<DailyForecasts> dailyForecastses) {
        Log.d(Tag, "onResult");
        if (headline != null) {
            Log.d(Tag, headline.EffectiveDate + "\n" + headline.Text + "\n"
                    + headline.Severity + "\n" + headline.Category + "\n"
                    + headline.EndDate);
        }

        updateData(headline, dailyForecastses);

        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * Show loading dialog with custom message
     *
     * @param pMsg obmited if using default loading message
     */
    protected void showLoadingDialog(String... pMsg) {
        if (pMsg != null && pMsg.length > 0) {
            mLoadingDialog = ProgressDialog.show(this, "", pMsg[0], true);
        } else {
            mLoadingDialog = ProgressDialog.show(this, "", "Đang tải dữ liệu...", true);
        }
        mLoadingDialog.setCancelable(false);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
