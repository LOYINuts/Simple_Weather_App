import javax.swing.*;

/**
 * @author yushihang
 * @version 1.0
 */
public class WeatherAppGui extends JFrame {
    public  WeatherAppGui(){
        // 初始化GUI并加一个标题
        super("Weather App");
        // 配置GUI当关闭界面的时候结束程序
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // 设置窗口的大小(像素)
        setSize(450,650);
        // 加载窗口到屏幕中间
        setLocationRelativeTo(null);
        // 将布局管理器设置为null，以便在GUI中手动定位组件
        setLayout(null);
        // 阻止拉伸窗口防止不协调
        setResizable(false);
    }
}
