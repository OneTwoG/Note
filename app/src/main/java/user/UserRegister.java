package user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ytw.note.R;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_register);

        //初始化控件
        initView();

        mSubmitRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strNumber = mUserNumber.getText().toString();
                String strName = mUserName.getText().toString();
                String strPwd = mUserPwd.getText().toString();
                String strRePwd = mUserRePwd.getText().toString();

                //对用户输入的信息进行判断
                canRegister = isCanRegister(strNumber, strName, strPwd, strRePwd);

                if (canRegister){
                    //如果完成注册，将信息上传服务器

                }

            }
        });

    }

    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.regis_tool);
        mToolBar.setTitle("注册");
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
        }else if (strRePwd != strPwd){
            Toast.makeText(UserRegister.this, "您两次输入的密码不匹配，请确认后重新输入", Toast.LENGTH_SHORT).show();
        }

        return true;
    }
}
