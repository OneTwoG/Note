package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

/**
 * Created by YTW on 2016/5/28.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "User_log.db";

    public static final String CREATE_LOG = "create table Log　（"
            + "id integer primary key autoincrement, "
            + "user_number text"
            + "log_title text"
            + "log_time text"
            + "log_class text"
            + "log_content text)";

    private Context context;

    public MyDatabaseHelper(Context context,String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, MyDatabaseName(context), factory, version);
        this.context = context;
    }


    private static String MyDatabaseName(Context context){
        String database_name = DB_NAME;
        boolean isSDcardEnable = false;

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){   //SDcard是否插入
            isSDcardEnable = true;
        }

        String dbPath = null;
        if (isSDcardEnable){
            dbPath = Environment.getExternalStorageDirectory().getPath() + "/Note/database/";
        }else {
            //未插入SDcard中，建立在内存中
            dbPath = context.getFilesDir().getPath() + "/database/";
        }

        File dbFile = new File(dbPath);
        if (!dbFile.exists()){
            dbFile.mkdirs();
        }

        database_name = dbPath + DB_NAME;
        return database_name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOG);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
