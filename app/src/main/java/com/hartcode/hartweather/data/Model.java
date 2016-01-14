package com.hartcode.hartweather.data;

import com.hartcode.hartweather.data.record.*;
import com.hartcode.hartweather.list.*;
import com.hartcode.hartweather.libweatherapi.*;

import org.slf4j.*;

import java.util.*;

/**
 *
 */
public class Model {
    private static final Logger logger = LoggerFactory.getLogger(Model.class);
    private final List<WeatherRecord> favoriteWeatherList;
    private final List<WeatherRecord> searchWeatherList;
    private static final int INDEX_NOT_FOUND = -1;
    private static final int MAX_LIST_SIZE = 10;
    private WeatherDataChangeHandler weatherDataChangeHandler;

    /**
     * The data model for HartWeather.
     */
    public Model()
    {
        this.favoriteWeatherList = new ArrayList<>();
        this.searchWeatherList = new ArrayList<>();

        this.loadFromDB();
    }
    /**
     *
     * @param view The user interface to update when the model changes.
     */
    public void setView(IView view)
    {
        if (view == null)
        {
            throw new IllegalArgumentException("View cannot be null.");
        }
        this.weatherDataChangeHandler = new WeatherDataChangeHandler(view);
    }



    /**
     * Adds or Updates the weather in the data model.
     * @param weather weather data
     */
    public boolean addUpdate(WeatherRecord weather)
    {
        return this.addUpdate(weather,true);
    }

    private boolean addUpdate(WeatherRecord weather, boolean okToSave)
    {
        logger.debug("addUpdate");
        boolean retval = false;
        if (weather == null)
        {
            throw new IllegalArgumentException("Weather cannot be null.");
        }

        if (this.containsWeather(weather))
        {
            int index = this.getWeatherIndex(weather);
            // Replace current weather
            WeatherRecord record = this.favoriteWeatherList.get(index);
            record.updateFromWeatherRecord(weather);
            if (okToSave)
            {
                record.save();
            }
            this.sendWeatherDataChange();
            retval = true;
        }else
        {
            if (this.favoriteWeatherList.size() >= MAX_LIST_SIZE)
            {
                // Favorites is Full
                this.sendShowErrorMessage("Too Many Favorites Already.");

            }
            else
            {
                // Add new Weather to list
                this.favoriteWeatherList.add(weather);
                this.sendWeatherDataChange();
                if (okToSave)
                {
                    weather.save();
                }
                retval = true;
            }
        }
        return retval;
    }

    public void addSearchData(List<WeatherRecord> weatherRecords)
    {
        this.searchWeatherList.clear();
        this.searchWeatherList.addAll(weatherRecords);
        this.sendWeatherDataChange();
    }
    /**
     *  Checks the data model for the given weather.
     *  Returns the index in the array if it's found.
     *  Returns -1 if not found.
     * @param weather weather data
     */
    public int getWeatherIndex(WeatherRecord weather)
    {
        int retval = INDEX_NOT_FOUND;
        if (weather == null)
        {
            throw new IllegalArgumentException("Weather cannot be null.");
        }
        int i = 0;
        while(retval == INDEX_NOT_FOUND && i < this.favoriteWeatherList.size())
        {
            WeatherRecord weatherRecord = this.favoriteWeatherList.get(i);
            if (weatherRecord.lat == weather.lat && weatherRecord.lon == weather.lon)
            {
                retval = i;
            }
            i++;
        }
        return retval;
    }

    /**
     * Checks the data model for the given weather.
     * Returns true if it's found.
     * @param weather weather data
     */
    public boolean containsWeather(WeatherRecord weather)
    {
        if (weather == null)
        {
            throw new IllegalArgumentException("Weather cannot be null.");
        }
        return this.getWeatherIndex(weather) != INDEX_NOT_FOUND;
    }

    public Weather getItem(int index)
    {
        return this.favoriteWeatherList.get(index).toWeather();
    }

    public Weather getSearchItem(int index)
    {
        return this.searchWeatherList.get(index).toWeather();
    }

    public int weatherSize()
    {
        return this.favoriteWeatherList.size();
    }
    public int searchSize()
    {
        return this.searchWeatherList.size();
    }

    private void loadFromDB()
    {
        logger.debug("loading from db.");
        List<WeatherRecord> weatherRecords = WeatherRecord.listAll(WeatherRecord.class);
        this.favoriteWeatherList.clear();
        for(int i = 0; i < weatherRecords.size(); i++)
        {
            this.addUpdate(weatherRecords.get(i), false);
        }
    }

    public void delete(int index)
    {
        WeatherRecord weatherRecord = this.favoriteWeatherList.remove(index);
        weatherRecord.delete();
        this.sendWeatherDataChange();
    }
    private void sendShowErrorMessage(String Message) {
        if (this.weatherDataChangeHandler != null) {
            //this.weatherDataChangeHandler.send
        }
    }

    private void sendWeatherDataChange()
    {
        if (this.weatherDataChangeHandler != null) {
            this.weatherDataChangeHandler.sendEmptyMessage(0);
        }
    }

}
