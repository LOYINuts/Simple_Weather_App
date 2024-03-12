import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author yushihang
 * @version 1.0
 */
public class WeatherAppGui extends JFrame {

    private JSONObject weatherDataResult;

    public WeatherAppGui() {
        // 初始化GUI并加一个标题
        super("Weather App");
        // 配置GUI当关闭界面的时候结束程序
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // 设置窗口的大小(像素)
        setSize(450, 650);
        // 加载窗口到屏幕中间
        setLocationRelativeTo(null);
        // 将布局管理器设置为null，以便在GUI中手动定位组件
        setLayout(null);
        // 阻止拉伸窗口防止不协调
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents() {
        // 搜索栏
        JTextField searchTextField = new JTextField();

        // 设置位置和大小
        searchTextField.setBounds(15, 15, 351, 45);

        // 设置字体和大小
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        // 天气图片
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // 温度文本
        JLabel temperatureText = new JLabel("10 °C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        // 文本居中
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // 天气情况描述文本
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // 潮湿度图片
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // 潮湿度文本
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // 风速图片
        JLabel wind_speedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        wind_speedImage.setBounds(220, 500, 74, 66);
        add(wind_speedImage);

        // 风速文本
        JLabel wind_speedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        wind_speedText.setBounds(310, 500, 85, 55);
        wind_speedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(wind_speedText);

        // 搜索按钮
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));
        // 改变按钮的样式当鼠标悬停的时候
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        // 为按钮添加事件侦听
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取用户输入的地址
                String userInput = searchTextField.getText();
                // 替换用户输入的空格
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                // 获取天气数据
                weatherDataResult = WeatherApp.getWeatherData(userInput);
                // 更新图形界面
                // 这里参数写我们自己制作的JSON文件的KEY
                String weatherCondition = (String) weatherDataResult.get("weather_condition");

                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }

                // 更新温度文本
                double temperature = (double) weatherDataResult.get("temperature");
                temperatureText.setText(temperature + " °C");

                // 更新天气文本
                weatherConditionDesc.setText(weatherCondition);

                // 更新潮湿度
                long humidity = (long) weatherDataResult.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                // 更新风速
                double windspeed = (double) weatherDataResult.get("windspeed");
                wind_speedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");

            }
        });
        add(searchButton);
    }

    private ImageIcon loadImage(String resourcePath) {
        try {
            // 从路径中读取图片
            BufferedImage image = ImageIO.read(new File(resourcePath));

            // 返回一张图片
            return new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("找不到图片");
        return null;
    }
}
