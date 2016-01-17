package com.hartcode.libweatherapi.libopenweatherapi;

import com.hartcode.hartweather.libweatherapi.*;
import com.hartcode.hartweather.libweatherapi.Weather;
import com.hartcode.libweatherapi.libopenweatherapi.data.find.*;
import com.hartcode.libweatherapi.libopenweatherapi.data.weather.*;

import org.slf4j.*;

import java.io.*;
import java.util.*;
import retrofit2.*;


public class OpenWeatherMapWeatherAPI implements IWeatherAPI
{

    private static final Logger logger = LoggerFactory.getLogger(OpenWeatherMapWeatherAPI.class);
    private static final String url = "http://api.openweathermap.org";
    private final String apiKey;
    IRetrofitOpenWeatherMapAPIService weatherMapAPIService = null;
    private String units;
    private final String countryCode;

    public OpenWeatherMapWeatherAPI(String apiKey, Unit unit, String countryCode)
    {
        if (apiKey == null)
        {
            throw new IllegalArgumentException("apiKey cannot be null.");
        }
        if (unit == null)
        {
            throw new IllegalArgumentException("unit cannot be null.");
        }
        if (countryCode == null)
        {
            throw new IllegalArgumentException("countryCode cannot be null.");
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.weatherMapAPIService = retrofit.create(IRetrofitOpenWeatherMapAPIService.class);
        this.apiKey = apiKey;
        this.countryCode = ", " + countryCode;
        this.setUnits(unit);
    }

    public void setUnits(Unit unit)
    {
        switch (unit)
        {
            case Celsius:
                this.units = "metric";
                break;
            case Fahrenheit:
            default:
                this.units = "imperial";
                break;
        }
    }

    @Override
    public Weather getWeatherByCity(int cityId) throws IOException
    {
        Weather retval = null;
        try
        {
            Call<OpenWeather> openWeatherCall =
                    weatherMapAPIService.getWeatherByCity(cityId, this.apiKey, this.units);
            OpenWeather ow = openWeatherCall.execute().body();
            if (ow != null)
            {
                if (ow.cod == 200)
                {
                    retval = new Weather(0, ow.id, ow.coord.lat, ow.coord.lon, ow.name, ow.weather.get(0).main, ow.weather.get(0).description, ow.weather.get(0).icon, ow.main.temp, ow.main.pressure, ow.main.humidity, ow.main.temp_min, ow.main.temp_max, ow.dt, ow.cod);
                } else
                {
                    retval = new Weather(ow.cod);
                }
            }

            logger.debug(ow.toString());
        }
        catch (IOException e)
        {
            throw new IOException("Network I/O error", e);
        }
        return retval;
    }

    @Override
    public Weather getWeatherByLatLon(double lat, double lon) throws IOException
    {
        Weather retval = null;
        try
        {
            Call<OpenWeather> openWeatherCall =
                    weatherMapAPIService.getWeatherByLatLon(lat, lon, this.apiKey, this.units);
            OpenWeather ow = openWeatherCall.execute().body();
            if (ow != null)
            {
                if (ow.cod == 200)
                {
                    retval = new Weather(0, ow.id, ow.coord.lat, ow.coord.lon, ow.name, ow.weather.get(0).main, ow.weather.get(0).description, ow.weather.get(0).icon, ow.main.temp, ow.main.pressure, ow.main.humidity, ow.main.temp_min, ow.main.temp_max, ow.dt, ow.cod);
                } else
                {
                    retval = new Weather(ow.cod);
                }
            }
            logger.debug(ow.toString());
        }
        catch (IOException e)
        {
            throw new IOException("Network I/O error", e);
        }
        return retval;
    }

    @Override
    public List<Weather> findCityByNameOrZip(String question) throws IOException, IllegalArgumentException
    {
        List<Weather> retval = null;
        try
        {
            if (question != null && question.length() >= 3)
            {
                // if question is a zipcode we need to add country code to the end.
                if (Character.isDigit(question.charAt(0)) &&
                        Character.isDigit(question.charAt(1)) &&
                        Character.isDigit(question.charAt(2)))
                {
                    question += countryCode;
                }

                Call<SearchData> openWeatherCall =
                        weatherMapAPIService.findCityByNameOrZip(question, this.apiKey, this.units, "like");
                SearchData search = openWeatherCall.execute().body();
                logger.debug(search.toString());
                if (search != null)
                {
                    retval = new ArrayList<>();
                    if (search.cod == 200)
                    {

                        for (int i = 0; i < search.list.size(); i++)
                        {
                            OpenWeather ow = search.list.get(i);
                            if (ow != null)
                            {
                                retval.add(new Weather(0, ow.id, ow.coord.lat, ow.coord.lon, ow.name, ow.weather.get(0).main, ow.weather.get(0).description, ow.weather.get(0).icon, ow.main.temp, ow.main.pressure, ow.main.humidity, ow.main.temp_min, ow.main.temp_max, ow.dt, 200));
                            }
                        }
                    } else
                    {
                        Weather weather = new Weather(search.cod);
                        retval.add(weather);
                    }
                    logger.debug(retval.toString());
                }
            }else
            {
                throw new IllegalArgumentException("Search string must be at least 3 characters long.");
            }
        }
        catch (IOException e)
        {
            throw new IOException("Network I/O error", e);
        }
        return retval;
    }
}
