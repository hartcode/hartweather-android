package com.hartcode.hartweather.list;

import android.app.*;
import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.Toast;

import com.hartcode.hartweather.R;
import com.hartcode.hartweather.data.*;
import com.hartcode.hartweather.libweatherapi.*;
import com.hartcode.hartweather.network.*;
import com.hartcode.libweatherapi.libopenweatherapi.*;

import org.slf4j.*;

public class WeatherListActivity extends AppCompatActivity implements View.OnClickListener, IConnectivity {
    private static final Logger logger = LoggerFactory.getLogger(WeatherListActivity.class);

    private String api_key = "34b3e14b5a4abd6edcc4c2e4051a6cab";
    private Unit units = Unit.Fahrenheit;
    private NetworkManager networkManager;
    private Model model;
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.model = new Model();
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        String temp_unit_string = prefs.getString(getString(R.string.temp_unit_key), Unit.Fahrenheit.toString());
        if (temp_unit_string != null)
        {
            this.units = Unit.valueOf(temp_unit_string);
        }

        this.connectivityManager =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);


        IWeatherAPI weatherapi = new OpenWeatherMapWeatherAPI(this.api_key, this.units);
        this.networkManager = new NetworkManager(weatherapi, model, this);


        for (int i =0; i < this.model.weatherSize(); i++)
        {
            this.networkManager.addRequest(this.model.getItem(i));
        }

        setContentView(R.layout.activity_weather_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        WeatherListActivityFragment fragment = (WeatherListActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        fragment.setData(model);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        boolean isSearchShown = savedInstanceState.getBoolean("isSearchShown",false);
        if (isSearchShown) {
            this.searchMenuItem.expandActionView();
        }
        CharSequence searchText = savedInstanceState.getCharSequence("searchText", "");
        if (searchText != "")
        {
            this.searchView.setQuery(searchText,false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather_list, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        this.searchMenuItem = menu.findItem(R.id.menu_add);
        this.searchView = (SearchView) searchMenuItem.getActionView();
        this.searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        this.searchView.setIconifiedByDefault(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.networkManager.stopThreads();
    }

    /**
     * Handles the floating action bar click
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        this.logger.debug("OnSearchRequested()");
        this.searchMenuItem.expandActionView();
    }

    @Override
    public boolean isConnectionActive() {
        boolean retval;
        NetworkInfo networkInfo = this.connectivityManager.getActiveNetworkInfo();
        retval = (networkInfo != null && networkInfo.isConnected()) ;
        return retval;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("isSearchShown",this.searchMenuItem.isActionViewExpanded());
        bundle.putCharSequence("searchText", this.searchView.getQuery());
    }
}
