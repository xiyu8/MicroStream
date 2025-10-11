package com.jason.microstream.ui.mine;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jason.microstream.LoginActivity;
import com.jason.microstream.R;
import com.jason.microstream.account.AccountManager;
import com.jason.microstream.core.im.imconpenent.ImService;
import com.jason.microstream.core.im.reqresp.MsRequester;
import com.jason.microstream.core.im.reqresp.ReqCallback;
import com.jason.microstream.core.im.reqresp.ReqWrapper;
import com.jason.microstream.core.im.reqresp.data.BaseReq;
import com.jason.microstream.core.im.reqresp.data.BaseResp;
import com.jason.microstream.manager.config.ApiConfig;
import com.jason.microstream.net.BaseRp;
import com.jason.microstream.net.RequestUser;
import com.jason.microstream.tool.NetUtil;
import com.jason.microstream.tool.log.LogTool;
import com.jason.microstream.ui.base.BasicPresenter;
import com.jason.microstream.ui.base.BriefObserver;
import com.jason.microstream.ui.compenent.recyclerview.BasicAdapter;
import com.jason.microstream.ui.main.MainFragment;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.callback.Callback;

import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MsMineFragment extends MainFragment implements BasicAdapter.ItemClickListener<MineHolder.Item>, View.OnClickListener {
    @Override
    protected int onCreateLayout() {
        return R.layout.fragment_mine;
    }

    @Override
    protected BasicPresenter<? extends BasicPresenter.View> onCreatePresenter() {
        return null;
    }

    RecyclerView mine_items;
    @Override
    public void lazyInit() {
        super.lazyInit();
        initView();
    }

    MineAdapter mineAdapter;
    ArrayList<MineHolder.Item> items;
    private void initView() {
        mine_items = getView().findViewById(R.id.mine_items);

        items = new ArrayList<>();
        mineAdapter= new MineAdapter(items,this);
        mine_items.setLayoutManager(new LinearLayoutManager(getContext()));
        mine_items.setAdapter(mineAdapter);

//        mineAdapter.enableLoadMore(false);
//        mineAdapter.setLoadMoreListener(this);
//        mineAdapter.setItemChildClickListener(this);
        View item_mine_logout = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_logout, null);
        mineAdapter.addFooter(item_mine_logout);
        item_mine_logout.findViewById(R.id.logout).setOnClickListener(this);
        item_mine_logout.findViewById(R.id.test_request).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout:
                logout();
                break;
            case R.id.test_request:
                BaseReq baseReqBean = new BaseReq();
                baseReqBean.data = "sssss";
                new MsRequester<BaseResp>(baseReqBean,"/test_api")
                        .request(BaseResp.class,new ReqCallback<BaseResp>() {
                    @Override
                    public void onSuccess(BaseResp respBean) {
                        LogTool.f("TAG", "11111:"
                                + respBean.data);
                    }

                    @Override
                    public void onFail(Exception exception, ReqWrapper req) {
                        LogTool.f( "TAG", "22222"
                                + "-exception:" + exception.getMessage()
                        );
                    }
                });
                break;
        }
    }

    private void logout() {
        getLogoutImp()
                .subscribe(new BriefObserver<Integer>(new CompositeDisposable()) {
                    @Override
                    public void onNext(@NonNull Integer integer) {
                        AccountManager.get().resetLogout();
                        ImService.getIm().reset();

                        Intent intent = new Intent(MsMineFragment.this.getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        AccountManager.get().resetLogout();
                        ImService.getIm().reset();

                        Intent intent = new Intent(MsMineFragment.this.getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    private Observable<Integer> getLogoutImp() {
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

                        String loginUrl = ApiConfig.testHost + "/auth" + ApiConfig.testLogout;
                        RequestUser requestUser = new RequestUser();
                        String contentData = new Gson().toJson(requestUser);
                        PostStringBuilder requestBuilder = OkHttpUtils.postString()
                                .url(loginUrl)
                                .content(contentData)
                                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                                ;

                        requestBuilder.addHeader("Authorization", "Bearer " + AccountManager.get().getToken());

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
                .flatMap(new Function<String, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(String ss) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<Integer>() {
                            @Override
                            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                                Gson gson = new Gson();
                                BaseRp baseRp = gson.fromJson(ss, BaseRp.class);
                                if (baseRp.ret == 0) {
//                                    LoginRet loginRet = gson.fromJson(baseRp.data, LoginRet.class);
                                    e.onNext(0);
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
    public void onItemClick(MineHolder.Item item, int position) {

    }

    @Override
    public void onItemLongClick(MineHolder.Item item, int position) {

    }


    @Override
    protected int getSelectedIcon() {
        return R.drawable.tab_mine_selected;
    }

    @Override
    protected int getUnselectedIcon() {
        return R.drawable.tab_mine_unselected;
    }


}
