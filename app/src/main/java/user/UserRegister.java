package user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ytw.note.MainActivity;
import com.example.ytw.note.R;

import org.json.JSONObject;

import uitl.HttpUtil;

/**
 * Created by YTW on 2016/5/6.
 */
public class UserRegister extends AppCompatActivity {

    //定义各种控件
    private Toolbar mToolBar;
    private EditText mUserNumber;
    private EditText mUserName;
    private EditText mUserPwd;
    private EditText mUserRePwd;
    private Button mSubmitRegis;
    private boolean canRegister;

    //用于对服务器返回的信息做判断
    public static final int CODE_SUCCESS = 0;   //成功
    public static final int CODE_FAILE = 1;     //失败
    public static final int CODE_NOTALL = 2;    //数据不全

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_register);

        //初始化控件
        initView();

        //完成注册按钮的监听事件
        mSubmitRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strNumber = mUserNumber.getText().toString();
                final String strName = mUserName.getText().toString();
                final String strPwd = mUserPwd.getText().toString();
                final String strRePwd = mUserRePwd.getText().toString();

                //对用户输入的信息进行判断
                canRegister = isCanRegister(strNumber, strName, strPwd, strRePwd);

                if (canRegister){
                    //如果信息无误，将信息上传服务器，完成注册
                    new Thread(){
                        @Override
                        public void run() {
                            submitUserInfo(strNumber,strName, strPwd);
                        }
                    }.start();
                }
            }
        });

    }


    /**
     * 导航栏的监听事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 提交用户信息，完成注册
     * @param strNumber
     * @param strName
     * @param strPwd
     */
    private void submitUserInfo(String strNumber, String strName, String strPwd) {
        String address = "http://139.129.39.66/api/registered.php";    //服务器地址

        //拼接用户信息
        String userInfo = "user_number=" + strNumber + "&user_name=" + strName
                            + "&user_pwd=" + strPwd;

        Log.d("UserRegister", "用户的信息是--" + userInfo);
        //将信息上传服务器，并接受返回的信息
        String ret_code = HttpUtil.sendPost(address, userInfo);

        //对服务器返回的信息进行判断
        if (ret_code == null){
            //如果返回的ret_code为null，提示用户注册失败
            handler.sendEmptyMessage(CODE_FAILE);
        }

        try{
            //对返回的信息进行JSON解析
            JSONObject result_json = new JSONObject(ret_code);
            int result_code = result_json.getInt("ret_code");

            if (result_code == CODE_SUCCESS){
                //提示用户注册成功，并跳转
                handler.sendEmptyMessage(CODE_SUCCESS);
            }else if (result_code == CODE_FAILE){
                handler.sendEmptyMessage(CODE_FAILE);
            }else {
                handler.sendEmptyMessage(CODE_NOTALL);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 0:
                    //注册成功，实现跳转
                    Intent intent = new Intent(UserRegister.this, MainActivity.class);
                    Log.d("UserRegister", "为什么不跳转");
                    startActivity(intent);
                    finish();
                    break;
                case 1:
                    Toast.makeText(UserRegister.this, "注册失败，请重新尝试", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(UserRegister.this, "请输入完整的注册信息", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.regis_tool);
        mToolBar.setTitle("注册");
        mToolBar.setNavigationIcon(R.drawable.ic_menu_back);
        setSupportActionBar(mToolBar);

        mUserNumber = (EditText) findViewById(R.id.user_number);
        mUserName = (EditText) findViewById(R.id.user_name);
        mUserPwd = (EditText) findViewById(R.id.et_pwd);
        mUserRePwd = (EditText) findViewById(R.id.et_repwd);

        mSubmitRegis = (Button) findViewById(R.id.btn_register);
    }

    public boolean isCanRegister(String strNumber, String strName, String strPwd, String strRePwd) {

        //对输入号码进行判断
        if (strNumber.length() != 11){
            Toast.makeText(UserRegister.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
        }else if (strName.isEmpty()){
            Toast.makeText(UserRegister.this, "请输入用户名", Toast.LENGTH_SHORT).show();
        }else if (strPwd.isEmpty()){
            Toast.makeText(UserRegister.this, "您还没有设置密码", Toast.LENGTH_SHORT).show();
        }else if (strPwd.length() < 6 || strPwd.length() > 12){
            Toast.makeText(UserRegister.this, "您设置的密码格式不对，请设置6-12位的密码", Toast.LENGTH_SHORT).show();
        }else if (!strRePwd.equals(strPwd)){
            Log.d("UserRegister", "strPwd--" + strPwd + ":" + "strRePwd--" + strRePwd);
            Toast.makeText(UserRegister.this, "您两次输入的密码不匹配，请确认后重新输入", Toast.LENGTH_SHORT).show();
        }

        return true;
    }
}
