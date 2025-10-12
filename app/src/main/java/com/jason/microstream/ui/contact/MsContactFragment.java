package com.jason.microstream.ui.contact;

import android.content.Intent;
import android.view.LayoutInflater;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jason.microstream.R;
import com.jason.microstream.account.AccountManager;
import com.jason.microstream.core.im.reqresp.MsRequester;
import com.jason.microstream.core.im.reqresp.ReqCallback;
import com.jason.microstream.core.im.reqresp.ReqWrapper;
import com.jason.microstream.core.im.reqresp.data.bean.RespUser;
import com.jason.microstream.core.im.reqresp.data.bean.contact.ReqContact;
import com.jason.microstream.core.im.reqresp.data.bean.contact.RespContact;
import com.jason.microstream.ui.base.BasicPresenter;
import com.jason.microstream.ui.base.BriefObserver;
import com.jason.microstream.ui.view_compenent.recyclerview.BasicAdapter;
import com.jason.microstream.ui.main.MainFragment;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MsContactFragment extends MainFragment implements BasicAdapter.ItemClickListener<ContactHolder.Item> {
    @Override
    protected int onCreateLayout() {
        return R.layout.fragment_contact;
    }


    @Override
    public void lazyInit() {
        super.lazyInit();
        initView();
        initData();
    }

    RecyclerView conversation_list;
    RefreshLayout contact_refresh;
    ContactAdapter contactAdapter;
    ArrayList<ContactHolder.Item> contactItems;
    private void initView() {
        conversation_list = getView().findViewById(R.id.conversation_list);
        conversation_list.setLayoutManager(new LinearLayoutManager(getContext()));
        contactItems = new ArrayList<>();
        contactAdapter = new ContactAdapter(contactItems, this);
        conversation_list.setAdapter(contactAdapter);
        contactAdapter.addHeader(LayoutInflater.from(getContext()).inflate(R.layout.header_conversation_list, null));
        contactAdapter.addFooter(LayoutInflater.from(getContext()).inflate(R.layout.header_conversation_list, null));

        contact_refresh = getView().findViewById(R.id.contact_refresh);
//        contact_refresh.setRefreshHeader(new ClassicsHeader(this));
//        contact_refresh.setRefreshFooter(new ClassicsFooter(this));
        contact_refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
            }
        });
//        contact_refresh.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(RefreshLayout refreshlayout) {
//                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
//            }
//        });

    }

    private void initData() {
        Observable.create(new ObservableOnSubscribe<RespContact>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<RespContact> emitter) throws Throwable {

                        ReqContact reqContact = new ReqContact();
                        reqContact.uid = AccountManager.get().getUid();
                        new MsRequester<RespContact>(reqContact, "/api/get_my_contact")
                                .request(RespContact.class, new ReqCallback<RespContact>() {
                                    @Override
                                    public void onSuccess(RespContact respContact) {
                                        emitter.onNext(respContact);
                                        emitter.onComplete();
                                    }

                                    @Override
                                    public void onFail(Exception exception, ReqWrapper req) {
                                        emitter.onError(exception);
                                    }
                                });

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BriefObserver<RespContact>(new CompositeDisposable()) {
                    @Override
                    public void onNext(@NonNull RespContact respContact) {
                        for (RespUser respUser : respContact.respUsers) {
                            ContactHolder.Item item = new ContactHolder.Item();
                            item.user = respUser;
                            contactItems.add(item);
                        }
                        contactAdapter.notifyDataSetChanged();
                        contact_refresh.finishRefresh(true);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        contact_refresh.finishRefresh(false);

                    }
                })
        ;
    }

    @Override
    public void onItemClick(ContactHolder.Item item, int position) {

        Intent intent = new Intent(getContext(), MyContactActivity.class);
        intent.putExtra("userName", item.user.name);
        intent.putExtra("userId", item.user.uid);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(ContactHolder.Item item, int position) {

    }

    @Override
    protected BasicPresenter<? extends BasicPresenter.View> onCreatePresenter() {
        return null;
    }






    @Override
    protected int getSelectedIcon() {
        return R.drawable.tab_contact_selected;
    }

    @Override
    protected int getUnselectedIcon() {
        return R.drawable.tab_contact_unselected;
    }


}
