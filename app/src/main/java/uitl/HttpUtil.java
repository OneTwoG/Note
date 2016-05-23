package uitl;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

    /**
     * 图片上传方法
     * @param serverurl
     * @param filepath
     * @return
     */
    public String uploadFileToPhpServer(String serverurl, String filepath) {
        Log.v("pi_2", "path0:" + filepath);
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        Log.v("pi_2", "end:" + end);

        try {
            URL url = new URL(serverurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn == null) {
                return null;
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setChunkedStreamingMode(1024 * 1024);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "UTF-8");

            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            File file = new File(filepath);

            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            Log.v("pi_2", "path1:" + filepath.substring(filepath.lastIndexOf("/") + 1));
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                    + filepath.substring(filepath.lastIndexOf("/") + 1) + "\"" + end);
//            dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
//                    + filepath+ "\"" + end);

            dos.writeBytes(end);

            DataInputStream in = new DataInputStream(new FileInputStream(file));

            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                dos.write(bufferOut, 0, bytes);
            }
            in.close();
            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            dos.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "UTF-8"));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String str = sb.toString();
            return str;

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

    }
}
