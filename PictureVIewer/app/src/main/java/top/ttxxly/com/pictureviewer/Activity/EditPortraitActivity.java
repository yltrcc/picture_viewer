package top.ttxxly.com.pictureviewer.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import top.ttxxly.com.pictureviewer.Adapter.GlideAdapter;
import top.ttxxly.com.pictureviewer.R;
import top.ttxxly.com.pictureviewer.Utils.SharedPreferenceUtils;
import top.ttxxly.com.pictureviewer.Utils.StreamUtils;
import top.ttxxly.com.pictureviewer.models.Photos;
import top.ttxxly.com.pictureviewer.models.User;

public class EditPortraitActivity extends AppCompatActivity {
    final int EDIT_PORTRAIT_CODE = 1;    //修改 portrait
    private String Url = "http://10.0.2.2/picture_viewer";
    private List<Photos.PhotosBean> photos = new ArrayList<Photos.PhotosBean>();
    private String nickname = "";
    private String password = "";
    private String mobile = "";
    private String portrait = "";
    private int Position = 0;//点击的位置

    private int code = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            code = msg.what;
            switch (msg.what) {
                case -1:
                    Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Photos data = new Gson().fromJson(msg.obj.toString(), Photos.class);
                    photos = data.getPhotos();
                    mPortrait.setAdapter(new GlideAdapter(photos));
                    Toast.makeText(getApplicationContext(), "头像请求成功", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    User User_data = new Gson().fromJson(msg.obj.toString(), User.class);
                    Toast.makeText(getApplicationContext(), User_data.getMessage(), Toast.LENGTH_SHORT).show();
                    SharedPreferenceUtils.putString("UserNickname", User_data.getNickname(), getApplicationContext());
                    SharedPreferenceUtils.putString("UserPassword", User_data.getPassword(), getApplicationContext());
                    SharedPreferenceUtils.putString("UserMobile", User_data.getMobile(), getApplicationContext());
                    SharedPreferenceUtils.putString("UserPortrait", User_data.getPortrait(), getApplicationContext());
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("url", photos.get(Position).getUrl());
                    intent.putExtras(bundle);
                    setResult(EDIT_PORTRAIT_CODE, intent);
                    finish();

                    break;
            }
        }
    };
    private GridView mPortrait;
    private int i;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_portrait);

        StartRequestFromPHP();
        ImageView mReturn = (ImageView) findViewById(R.id.img_edit_portrait_return_top);
        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPortrait = (GridView) findViewById(R.id.GV_edit_portrait);
        mPortrait.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                portrait = photos.get(position).getUrl();
                Position = position;
                StartRequestFromPHP1();
            }
        });
    }


    private void StartRequestFromPHP() {

        //新建线程
        new Thread() {
            public void run() {
                try {
                    SendRequestToEditUserInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void SendRequestToEditUserInfo() {

        mUserId = SharedPreferenceUtils.getString("UserId", "", getApplicationContext());
        String keys = "portrait";
        HttpURLConnection conn = null;
        try {
            // 创建一个URL对象
            String url = Url + "/interface/selectpic.php" + "?userid=" + mUserId + "&keys=" + keys;
            Log.i("URl", url);
            URL mURL = new URL(url);
            // 调用URL的openConnection()方法,获取HttpURLConnection对象
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("GET");// 设置请求方法为post
            conn.setReadTimeout(3000);// 设置读取超时为1秒
            conn.setConnectTimeout(3000);// 设置连接网络超时为1秒
            conn.setDoOutput(true);// 设置此方法,允许向服务器输出内容
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();// 调用此方法就不必再使用conn.connect()方法
            if (responseCode == 200) {

                InputStream is = conn.getInputStream();
                String data = StreamUtils.Stream2String(is);
                Log.i("data", data);
                Photos value = new Gson().fromJson(data, Photos.class);

                String flat = value.getFlat();
                Message msg = new Message();
                if (flat.equals("success")) {
                    Log.i("Status", "修改用户信息请求成功！！！");
                    msg.what = 1;
                    msg.obj = data;
                } else {
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            } else {
                Log.i("访问失败", "responseCode");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("访问失败1", "无法连接服务器");
        } finally {
            if (conn != null) {
                conn.disconnect();// 关闭连接
            }
        }

    }



    private void StartRequestFromPHP1() {

        //新建线程
        new Thread() {
            public void run() {
                try {
                    SendRequestToEditUserInfo1();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void SendRequestToEditUserInfo1() {

        HttpURLConnection conn = null;
        try {
            // 创建一个URL对象
            String url = Url + "/interface/update_user.php" + "?userid=" + mUserId + "&nickname=" + nickname + "&mobile=" + mobile + "&password=" + password + "&portrait=" + portrait;
            Log.i("URl", url);
            URL mURL = new URL(url);
            // 调用URL的openConnection()方法,获取HttpURLConnection对象
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("GET");// 设置请求方法为post
            conn.setReadTimeout(3000);// 设置读取超时为1秒
            conn.setConnectTimeout(3000);// 设置连接网络超时为1秒
            conn.setDoOutput(true);// 设置此方法,允许向服务器输出内容
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();// 调用此方法就不必再使用conn.connect()方法
            if (responseCode == 200) {

                InputStream is = conn.getInputStream();
                String data = StreamUtils.Stream2String(is);
                Log.i("data", data);
                User value = new Gson().fromJson(data, User.class);

                String flat = value.getFlat();
                Message msg = new Message();
                if (flat.equals("success")) {
                    Log.i("Status", "修改用户信息请求成功！！！");
                    msg.what = 2;
                    msg.obj = data;
                } else {
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            } else {
                Log.i("访问失败", "responseCode");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("访问失败1", "无法连接服务器");
        } finally {
            if (conn != null) {
                conn.disconnect();// 关闭连接
            }
        }

    }
}