package com.hartcode.hartweather.data.record;
import com.hartcode.hartweather.libweatherapi.*;
import com.orm.*;
import com.orm.dsl.Unique;

/**
 *
 */

public class WeatherRecord extends SugarRecord {
    @Unique
    public long id;
    public int cityId;
    public String cityName;
    public String main;
    public String description;
    public String icon;
    public float temp;
    public int pressure;
    public int humidity;
    public float temp_min;
    public float temp_max;
    public long lastUpdate;

    public WeatherRecord()
    {

    }

    public WeatherRecord(long id, int cityId, String cityName, String main, String description, String icon, float temp, int pressure, int humidity, float temp_min, float temp_max, long lastUpdate)
    {
        this.id = id;
        this.cityId = cityId;
        this.cityName = cityName;
        this.main = main;
        this.description = description;
        this.icon = icon;
        this.temp = temp;
        this.pressure = pressure;
        this.humidity = humidity;
        this.temp_max = temp_max;
        this.temp_min = temp_min;
        this.lastUpdate = lastUpdate;
    }

    public WeatherRecord(Weather weather)
    {
        this.updateFromWeather(weather);
    }

    public void updateFromWeather(Weather weather)
    {
        this.id = weather.id;
        this.cityId = weather.cityId;
        this.cityName = weather.cityName;
        this.main = weather.main;
        this.description = weather.description;
        this.icon = weather.icon;
        this.temp = weather.temp;
        this.pressure = weather.pressure;
        this.humidity = weather.humidity;
        this.temp_max = weather.temp_max;
        this.temp_min = weather.temp_min;
        this.lastUpdate = weather.lastUpdate;
    }

    public void updateFromWeather(WeatherRecord weather)
    {
        this.id = weather.id;
        this.cityId = weather.cityId;
        this.cityName = weather.cityName;
        this.main = weather.main;
        this.description = weather.description;
        this.icon = weather.icon;
        this.temp = weather.temp;
        this.pressure = weather.pressure;
        this.humidity = weather.humidity;
        this.temp_max = weather.temp_max;
        this.temp_min = weather.temp_min;
        this.lastUpdate = weather.lastUpdate;
    }

    public Weather toWeather()
    {
        return new Weather(this.id, this.cityId,this.cityName,this.main,this.description,this.icon,this.temp, this.pressure,this.humidity, this.temp_min, this.temp_max, this.lastUpdate);
    }



}