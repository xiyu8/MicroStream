package com.jason.microstream;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jason.microstream.account.AccountManager;
import com.jason.microstream.manager.config.ApiConfig;
import com.jason.microstream.core.im.imconpenent.ImService;
import com.jason.microstream.customer.LoadingDialog;
import com.jason.microstream.localbroadcast.Events;
import com.jason.microstream.localbroadcast.LocBroadcast;
import com.jason.microstream.localbroadcast.LocBroadcastReceiver;
import com.jason.microstream.model.User;
import com.jason.microstream.net.BaseRp;
import com.jason.microstream.net.LoginRet;
import com.jason.microstream.net.RequestUser;
import com.jason.microstream.tool.NetUtil;
import com.jason.microstream.tool.TextUtil;
import com.jason.microstream.ui.TestSettingActivity;
import com.jason.microstream.ui.base.BasicActivity;
import com.jason.microstream.ui.base.BasicPresenter;
import com.jason.microstream.ui.base.BriefObserver;
import com.jason.microstream.ui.main.MainActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.callback.Callback;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends BasicActivity implements LocBroadcastReceiver {

    public static User user = null;
    Gson gson;
    private boolean rememberPwd = false;

    EditText user_name;
    EditText user_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        // 在设置布局前关闭返回共享元素动画
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // 关闭“返回”时的共享元素动画
        getWindow().setSharedElementReturnTransition(null);
        super.onCreate(savedInstanceState);
        getToolBarArea().setVisibility(View.GONE);
        gson = new Gson();
        user_name = findViewById(R.id.user_name);
        user_pwd = findViewById(R.id.user_pwd);


        if (Build.BRAND.toLowerCase().equals("samsung")
//                || Build.BRAND.toLowerCase().equals("redmi")
//                || Build.BRAND.toLowerCase().equals("vivo")
        ) {
            user_name.setText("user1");
        } else if (Build.BRAND.toLowerCase().equals("honor")
//                ||Build.BRAND.toLowerCase().equals("huawei")
        ) {
            user_name.setText("user2");
        } else {
            user_name.setText("user3");
        }

    }

    @Override
    protected BasicPresenter onCreatePresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    private ObservableEmitter<User> loginEmitter;
    private String userPwd;
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:

                String name = user_name.getText().toString();
                userPwd= user_pwd.getText().toString();

                if (TextUtil.isEmpty(name) || TextUtil.isEmpty(userPwd)) {
                    Toast.makeText(LoginActivity.this, "empty account", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoadingDialog loadingDialog = new LoadingDialog(this);

                loadingDialog.showDialog();
                login(name, userPwd)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BriefObserver<User>(disposable) {
                            @Override
                            public void onNext(User user) {
                                LocBroadcast.getInstance().registerBroadcast(LoginActivity.this, new String[]{Events.ACTION_ON_LOGIN, Events.ACTION_ON_LOGIN_FAIL});
                                loadingDialog.dismissDialog();

                                Toast.makeText(LoginActivity.this, "成功", Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(LoginActivity.this, MainActivity1_.class));
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                            @Override
                            public void onError(Throwable e) {
                                loadingDialog.dismissDialog();

                                Toast.makeText(LoginActivity.this, "失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                ;
                break;

            case R.id.test_setting:
                startActivity(new Intent(this, TestSettingActivity.class));
                break;
        }
    }

    private Observable<User> login(String name,String pwd) {
        return getLoginImp(name, pwd)
                .flatMap((Function<LoginRet, ObservableSource<User>>) this::handleLogin);
    }


    private Observable<User> handleLogin(LoginRet loginRet) {
        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<User> emitter) throws Throwable {
                user = loginRet.user;

                AccountManager.get().resetAccount(user, loginRet.token, loginRet.expireAt, rememberPwd ? userPwd : null);
                ImService.getIm().auth(AccountManager.get().getToken(), AccountManager.get().getUid());

                emitter.onNext(user);
                emitter.onComplete();
            }
        });
    }

    private Observable<LoginRet> getLoginImp(String name,String pwd) {
        return Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {

//                        String loginUrl = ApiConfig.testHost + ApiConfig.testLogin + "userData=%7B%22userName%22:%20%22" + name + "%22,%22password%22:%20%22" + pwd + "%22%7D";
//
//                        GetBuilder requestBuilder = OkHttpUtils.get()
//                                .url(loginUrl);
//      //                for (String key : param.keySet()) {
//      //                  requestBuilder.addParams(key,param.get(key));
//      //                }

                        String loginUrl = ApiConfig.testHost + "/auth" + ApiConfig.testLogin;
                        RequestUser requestUser = new RequestUser(name, pwd);
                        String contentData = new Gson().toJson(requestUser);
                        PostStringBuilder requestBuilder = OkHttpUtils.postString()
                                .url(loginUrl)
                                .content(contentData)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                ;

//                        requestBuilder.addHeader("Authorization", "Bearer " + token);

                        requestBuilder.build()
                                .execute(new Callback() {
                                    @Override
                                    public Object parseNetworkResponse(Response response, int id) throws Exception {

                                        ResponseBody body = response.body();
                                        if (body != null) {
                                            MediaType mediaType = body.contentType();
                                            if (mediaType != null) {
                                                if (NetUtil.mediaTypeIsText(mediaType)) {
                                                    String resp = body.string();
                                                    emitter.onNext(resp);
                                                    body = ResponseBody.create(mediaType, resp);
                                                    return response.newBuilder().body(body).build();
                                                } else {
                                                    emitter.onError(new Error("response data type error"));
                                                }
                                            }
                                        }
                                        return body;
                                    }

                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        emitter.onError(new Error(e.getMessage()));
                                    }

                                    @Override
                                    public void onResponse(Object response, int id) {
                                    }
                                });
                    }
                })
                .flatMap(new Function<String, ObservableSource<LoginRet>>() {
                    @Override
                    public ObservableSource<LoginRet> apply(String ss) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<LoginRet>() {
                            @Override
                            public void subscribe(ObservableEmitter<LoginRet> e) throws Exception {
                                BaseRp baseRp = gson.fromJson(ss, BaseRp.class);
                                if (baseRp.ret == 0) {
                                    LoginRet loginRet = gson.fromJson(baseRp.data, LoginRet.class);
                                    e.onNext(loginRet);
                                    e.onComplete();
                                } else {
                                    e.onError(new Error(baseRp.ret+":"+baseRp.errorMsg));
                                }
                            }
                        });
                    }
                });
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {

    }
}