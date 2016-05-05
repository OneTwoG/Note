package uitl;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 工具类
 * Created by YTW on 2016/4/24.
 */
public class HttpUtil {

    public static String sendPost(String url, String params) {

        OutputStream os = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        byte[] buffer = params.getBytes();

        try {
            URL readUrl = new URL(url);
            Log.d("HttpUtil", readUrl.toString() + "");
            //打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) readUrl.openConnection();
            //设置通用的请求属性
            connection.setRequestMethod("POST");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            //发送POST必须设置下面两行,允许输入 输出
            connection.setDoOutput(true);
            connection.setDoInput(true);

            //设置连接超时时间和读取时间
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            os = new DataOutputStream(connection.getOutputStream());
            os.write(buffer, 0, buffer.length);
            os.flush();
            os.close();

            //定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //使用finally关闭输入 输出流
            try {
                if (os != null) {
                    os.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }
}
