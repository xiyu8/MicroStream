package com.jason.microstream;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jason.microstream.model.User;
import com.jason.microstream.net.BaseRp;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.OtherRequestBuilder;
import com.zhy.http.okhttp.callback.Callback;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity {

    public static final String ip = "10.2.113.3";
    public static final String port = "8887";



    public static User user = null;

    Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        gson = new Gson();
    }


    String apiLogin = "http://"+ip+":8009/demo-0.0.1-SNAPSHOT/login?userData=%7B%22userName%22:%20%22user2%22,%22password%22:%20%22123%22%7D";

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                String name = ((TextView) findViewById(R.id.user_name)).getText().toString();
                String pwd = ((TextView) findViewById(R.id.user_pwd)).getText().toString();
                String loginUrl = "http://"+ip+":8009/demo-0.0.1-SNAPSHOT/login?userData=%7B%22userName%22:%20%22"+name+"%22,%22password%22:%20%22"+pwd+"%22%7D";

                GetBuilder requestBuilder= OkHttpUtils
                        .get()
                        .url(loginUrl);
//                for (String key : param.keySet()) {
//                  requestBuilder.addParams(key,param.get(key));
//                }


                Observable
                        .create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                                requestBuilder.build()
                                        .execute(new Callback() {
                                            @Override
                                            public Object parseNetworkResponse(Response response, int id) throws Exception {

                                                ResponseBody body = response.body();
                                                if (body != null) {
                                                    MediaType mediaType = body.contentType();
                                                    if (mediaType != null) {
                                                        if (isText(mediaType)) {
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
                        .flatMap(new Function<String, ObservableSource<User>>() {
                            @Override
                            public ObservableSource<User> apply(String ss) throws Exception {
                                return Observable.create(new ObservableOnSubscribe<User>() {
                                    @Override
                                    public void subscribe(ObservableEmitter<User> e) throws Exception {
                                        BaseRp baseRp = gson.fromJson(ss, BaseRp.class);
                                        if (baseRp.ret == 0) {
                                            user = gson.fromJson(baseRp.content, User.class);
                                            e.onNext(user);
                                        } else {
                                            e.onError(new Error("parse rp exception"));
                                        }
                                    }
                                });
                            }
                        })
                        .flatMap(new Function<User, ObservableSource<User>>() {
                            @Override
                            public ObservableSource<User> apply(User user) throws Exception {
                                return Observable.create(new ObservableOnSubscribe<User>() {
                                    @Override
                                    public void subscribe(ObservableEmitter<User> e) throws Exception {
                                        bindService(new Intent(LoginActivity.this, NioPeriodChronicService.class), new ServiceConnection() {
                                            @Override
                                            public void onServiceConnected(ComponentName name, IBinder service) {
                                                NioPeriodChronicService.NioBinder nioBinder = (NioPeriodChronicService.NioBinder) service;
                                                nioBinder.registerNIoSelector(new NioPeriodChronicService.View() {
                                                    @Override
                                                    public void showError(String ss) {

                                                    }

                                                    @Override
                                                    public void showData(String ss) {

                                                    }

                                                    @Override
                                                    public void showConnection(String ip, String port, String user, String tt) {

                                                    }
                                                });
                                                nioBinder.initWriteThread();
                                                nioBinder.nioConnect(ip,port,user.getUid(),user.getToken());
                                                e.onNext(user);
                                            }
                                            @Override
                                            public void onServiceDisconnected(ComponentName name) {}
                                        }, Context.BIND_AUTO_CREATE);
                                    }
                                })
                                        .subscribeOn(AndroidSchedulers.mainThread());
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<User>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(User user) {
                                Toast.makeText(LoginActivity.this, "成功", Toast.LENGTH_SHORT).show();
                                LoginActivity.user = user;
                                startActivity(new Intent(LoginActivity.this, MainActivity1_.class));
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(LoginActivity.this, "失败"+e.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                break;

        }
    }


    protected boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
            )
                return true;
        }
        return false;
    }









}