import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * @author yushihang
 * @version 1.0
 */

// 从API接口中获取天气数据，界面会将获取的数据展示出来
public class WeatherApp {
    // 从给定的位置获取天气数据
    // 使用JSON文件读存数据
    public static JSONObject getWeatherData(String locationName) {
        // 使用 API 获取位置的坐标
        JSONArray locationData = getLocationData(locationName);
//        System.out.println(locationData);
        // 获取经纬度,只用返回的第一个城市
        assert locationData != null;
        JSONObject location = (JSONObject) locationData.get(0);
//        System.out.println(location);
        JSONArray coordinates = (JSONArray) location.get("center");
        System.out.println(coordinates);
        // 拆成经纬度
        double latitude = (double) coordinates.get(1);
        double longitude = (double) coordinates.get(0);
        // 使用该经纬度组合成API请求语句请求天气

        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";

        try {
            // api接口请求并获取结果
            HttpURLConnection conn = fetchAPIResponse(urlString);

            if (conn != null && conn.getResponseCode() != 200) {
                System.out.println("Error");
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            assert conn != null;
            Scanner myscanner = new Scanner(conn.getInputStream());
            while (myscanner.hasNext()) {
                // read and store data
                resultJson.append(myscanner.nextLine());
            }

            myscanner.close();
            conn.disconnect();

            // 转换数据
            JSONParser myparser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) myparser.parse(String.valueOf(resultJson));
            // 这是获取JSON的hourly的数据
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            // 获取现在的时间
            JSONArray now_time = (JSONArray) hourly.get("time");
            // 我们只获取当前时间时的天气
            int index = findIndexOfCurrentTime(now_time);

            // 获取温度
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // 获取天气码
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            // 获取潮湿度
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // 风速
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            // 使用获得的数据来构造JSON对象
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);
//            System.out.println(weatherData);
            return weatherData;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 获取给定位置的地理坐标
    public static JSONArray getLocationData(String locationName) {
        // 用+替换字符串中的空格遵循API的查询格式
        // locationName = locationName.replaceAll(" ", "+");
        // 使用这个来构建API查询URL
        String urlString = "https://api.maptiler.com/geocoding/" +
                locationName +
                ".json?key=xaH3DhDsT3jXs26QnZnL";
        try {
            // api请求
            HttpURLConnection conn = fetchAPIResponse(urlString);

            // 检查返回状态
            // 200就意味着OK
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            } else {
                // 存储API结果
                StringBuilder resultJson = new StringBuilder();
                // 使用scanner读取JSON数据存在SB里面
                Scanner myscanner = new Scanner(conn.getInputStream());
                while (myscanner.hasNext()) {
                    resultJson.append(myscanner.nextLine());
                }

                myscanner.close();
                conn.disconnect();
                // 使用JSON转换器把读取的数据转换成真正的JSON
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // 获取得到的城市的结果的列表
                return (JSONArray) resultsJsonObj.get("features");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchAPIResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            // 与API链接
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray time_list) {
        String currentTime = getCurrentTime();
        // 遍历时间列表，看哪个与我们当前的匹配

        int length = time_list.size();
        for (int i = 0; i < length; i++) {
            String t = (String) time_list.get(i);
            if (t.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime() {
        // 获取当前的日期和时间
        LocalDateTime currentDateTime = LocalDateTime.now();

        // 转换为API里面的时间的格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // 转换当前时间为API的格式并打印出来
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    // 把天气码转换为可读性更强的东西
    public static String convertWeatherCode(long weather_code) {
        /*
          Code	Description
          0	Clear sky
          1, 2, 3	Mainly clear, partly cloudy, and overcast
          45, 48	Fog and depositing rime fog
          51, 53, 55	Drizzle: Light, moderate, and dense intensity
          56, 57	Freezing Drizzle: Light and dense intensity
          61, 63, 65	Rain: Slight, moderate and heavy intensity
          66, 67	Freezing Rain: Light and heavy intensity
          71, 73, 75	Snow fall: Slight, moderate, and heavy intensity
          77	Snow grains
          80, 81, 82	Rain showers: Slight, moderate, and violent
          85, 86	Snow showers slight and heavy
          95 *	Thunderstorm: Slight or moderate
          96, 99 *	Thunderstorm with slight and heavy hail
         */
        String weatherCondition = "";
        if (weather_code == 0L) {
            weatherCondition = "Clear";
        } else if (weather_code > 0L && weather_code <= 3L) {
            // cloudy
            weatherCondition = "Cloudy";
        } else if ((weather_code >= 51L && weather_code <= 67L)
                || (weather_code >= 80L && weather_code <= 99L)) {
            // rainy
            weatherCondition = "Rain";
        } else if (weather_code >= 71L && weather_code <= 77L) {
            // snow
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }

}
