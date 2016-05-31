package Tool;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by YTW on 2016/5/15.
 */
public class MyTool {
    private static String sdState = Environment.getExternalStorageState();

    /**
     * 从服务器上下载文件,并保存在SDCard中
     * @param url
     * @param path
     * @param fileName
     * @return
     */
    public boolean downFile(String url, String path, String fileName) {
        //创建一个字节流对象
        InputStream is = null;
        try {
            //判断文件是否存在
            File file = isFileExist(path,fileName);
            if (file.exists()) {
                return true;
            } else {
                //从URL中获取输入流
                is = getHttpInputStream(url + fileName);
                File resultFile = writeFileToSDcard(path, fileName, is);
                if (resultFile == null){
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断SDCard上的文件是否存在
     * @param path
     * @param fileName
     * @return
     */
    private File isFileExist(String path, String fileName) {
        File file = new File(Environment.getExternalStorageDirectory() + path + File.separator + fileName);
        return file;
    }

    /**
     * 将文件写入SDCard
     * @param path
     * @param fileName
     * @param is
     * @return
     * @throws IOException
     */
    private File writeFileToSDcard(String path, String fileName, InputStream is) throws IOException {
        File file = null;
        OutputStream os = null;

        createSDDir(path);
        try {
            file = createFileInSDCard(fileName, path);
            os = new FileOutputStream(file);

            byte[] bytes = new byte[4 * 1024];
            int temp;

            while ((temp = is.read()) != -1) {
                os.write(bytes, 0, temp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭字节输出流

            os.close();
        }
        return file;
    }

    /**
     * 在SDCard上创建文件
     * @param fileName
     * @param dir
     * @return
     * @throws IOException
     */
    private File createFileInSDCard(String fileName, String dir) throws IOException {
        File file = new File(Environment.getExternalStorageDirectory() + dir + File.separator + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 创建保存图片的SD卡的路径
     *
     * @param path
     */
    private File createSDDir(String path) {
        File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + path + File.separator);
        return dirFile;
    }

    /**
     * 从服务器上获取字节流
     * @param s
     * @return
     */
    private InputStream getHttpInputStream(String s) {

        try {
            URL url = new URL(s);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            return conn.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 删除文件或者目录
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (sdState.equals(Environment.MEDIA_MOUNTED)) {
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                }
                // 如果它是一个目录
                else if (file.isDirectory()) {
                    // 声明目录下所有的文件 files[];
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                    }
                }
                file.delete();
            }
        }
    }

    /**
     * 将信息保存本地，以便下次显示
     */
    public static void SDcardSave(String filePath, String content) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            byte[] bytes = content.getBytes();
            fos.write(bytes);
            fos.close();
            Log.d("tool", "写入成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从SD卡中读取数据
     *
     * @param strUrl
     * @return
     */
    public static String readSDcard(String strUrl) {
        StringBuffer sb = new StringBuffer();

        try {
            //判断文件是否存在
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File file = new File(Environment.getExternalStorageDirectory().getPath() + strUrl);

                if (file.exists()) {
                    //打开文集那输入流
                    FileInputStream fis = new FileInputStream(file);

                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    fis.close();
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
