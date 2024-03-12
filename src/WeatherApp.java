import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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

        return null;
    }

    // 获取给定位置的地理坐标
    public static JSONArray getLocationData(String locationName) {
        // 用+替换字符串中的空格遵循API的查询格式
        // locationName = locationName.replaceAll(" ", "+");
        // 使用这个来构建API查询URL
        String urlString = "https://api.maptiler.com/geocoding/" +
                locationName +
                ".json?key=AWUCu85b7MfktM2GT5Y2";
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
                JSONArray locationData = (JSONArray) resultsJsonObj.get("features");
                return locationData;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HttpURLConnection fetchAPIResponse(String urlString) {
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
}
