package user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ytw.note.MainActivity;
import com.example.ytw.note.R;

import org.json.JSONObject;

import uitl.HttpUtil;


/**
 * Created by YTW on 2016/4/24.
 */
public class UserLogin extends AppCompatActivity implements View.OnClickListener {

    //定义变量
    private EditText mUserCount;
    private EditText mUserPwd;
    private Button mLogin;
    private Button mMissPwd;
    private ImageView mBack;
    private ImageView mRegister;

    //1,2便是登录失败,0表示登录成功
    private static final int LOGIN_NULL = 1;
    private static final int LOGIN_OK = 0;
    private static final int LOGIN_ERROR = 2;
    private boolean isLoginok = false;

    //setResult返回的requestCode
    private static final int RESULT_OK = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);

        //初始化控件
        initView();

        //设置按钮的监听事件
        setListener();
    }

    @Override
    public void onBackPressed() {
        //调用此方法判断是否将 登录TextView设置成用户名还是继续显示 登录
        finish();
        super.onBackPressed();
    }


    private void setListener() {
        mLogin.setOnClickListener(this);
        mMissPwd.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }

    private void initView() {
        mUserCount = (EditText) findViewById(R.id.tv_user_count);
        mUserPwd = (EditText) findViewById(R.id.tv_user_pwd);
        mLogin = (Button) findViewById(R.id.btn_login);
        mMissPwd = (Button) findViewById(R.id.btn_missPwd);
        mBack = (ImageView) findViewById(R.id.iv_back);
        mRegister = (ImageView) findViewById(R.id.iv_register);
    }

    /**
     * 执行登录操作
     */
    public void setLogin() {

        //获取 用户登录账号 和 用户登录密码 的字符
        final String mmUserCount = mUserCount.getText().toString();
        final String mmUserPwd = mUserPwd.getText().toString();

        //先进行本地判断，文本框的内容是否为空
        if (mmUserCount.isEmpty()) {
            Toast.makeText(UserLogin.this, "请输入用户名", Toast.LENGTH_SHORT).show();
        } else if (mmUserPwd.isEmpty()) {
            Toast.makeText(UserLogin.this, "请输入密码", Toast.LENGTH_SHORT).show();
        } else {
            new Thread() {
                @Override
                public void run() {
                    Log.d("UserLogin", "准备进入服务器");
                    //从服务器验证 用户名 和 密码 是否正确
                    checkFromServer(mmUserCount, mmUserPwd);
                }
            }.start();
        }
    }

    private void checkFromServer(String mmUserName, String mmUserPass) {
        //用户已经输入 用户名 和 密码
        //从服务器获取信息进行判断
        String address = "http://139.129.39.66/api/user_login.php";
        String info = "user_name=" + mmUserName + "&" + "user_pass=" + mmUserPass;

        //上传服务器验证，并获取返回的信息
        String result_code = HttpUtil.sendPost(address, info);

        //对服务器返回的结果进行解析 判断
        if (result_code == null) {
            //返回结果为空，显示登陆失败
            handler.sendEmptyMessage(LOGIN_NULL);
        }

        try {
            //对返回的结果进行JSON解析
            JSONObject jsonObject = new JSONObject(result_code);
            int ret_code = jsonObject.getInt("ret_code");
            if (ret_code == LOGIN_OK) {
                //登录成功
                handler.sendEmptyMessage(LOGIN_OK);
            } else if (ret_code == LOGIN_ERROR) {
                //登录失败,用户名或者密码错误
                handler.sendEmptyMessage(LOGIN_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_NULL:
                    //登录失败
                    Toast.makeText(UserLogin.this, "登录失败 请重新尝试登陆", Toast.LENGTH_LONG).show();
                    break;
                case LOGIN_OK:
                    //登录成功,跳转，并将mUserCount的值通过Intent传到MainActivity，
                    // 将登录TextView设置成当前用户名
                    isLoginok = true;

                    Intent intent = new Intent(UserLogin.this, MainActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("user_name", mUserCount.getText().toString());
                    bundle.putBoolean("isLogin", true);
                    intent.putExtras(bundle);
                    setResult(LOGIN_OK, intent);
                    finish();
                    break;
                case LOGIN_ERROR:
                    //账号或者密码错误
                    Toast.makeText(UserLogin.this, "用户名或者密码错误，请重新输入尝试登录", Toast.LENGTH_LONG).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                //执行登录操作
                //对用户名和密码进行判断
                setLogin();
                break;
            case R.id.btn_missPwd:
                //执行忘记密码操作
                Toast.makeText(UserLogin.this, "这个还没准备好", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_register:
                //跳转到用户注册界面
                Intent intent = new Intent(UserLogin.this, UserRegister.class);
                startActivity(intent);
                break;
        }
    }
}
