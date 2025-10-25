package com.jason.microstream.ui.conversation;

import com.jason.microstream.core.im.reqresp.MsRequester;
import com.jason.microstream.core.im.reqresp.ReqCallback;
import com.jason.microstream.core.im.reqresp.ReqWrapper;
import com.jason.microstream.core.im.reqresp.data.bean.chat.ClientConversation;
import com.jason.microstream.core.im.reqresp.data.bean.chat.ReqLatestConv;
import com.jason.microstream.core.im.reqresp.data.bean.chat.RespLatestConv;
import com.jason.microstream.db.AppDatabaseManager;
import com.jason.microstream.db.entity.ConversationEntity;
import com.jason.microstream.db.entity.generator.ConversationEntityDao;
import com.jason.microstream.db.entity.generator.DaoSession;
import com.jason.microstream.mapper.MessageMapper;
import com.jason.microstream.ui.base.BasicPresenter;
import com.jason.microstream.ui.base.BriefObserver;
import com.jason.microstream.ui.conversation.holder.ConversationHolder;
//import com.jason.microstream.ui.conversation.mapper.ChatConvert;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MsConversationListPresenter extends BasicPresenter<MsConversationListPresenter.View>{
    public MsConversationListPresenter(MsConversationListPresenter.View mView) {
        super(mView);
//        ShIm.get().addConversationListener(this);
    }

    public void getAllConversions() {
        getConversions();
    }

    private void getConversions() {
        Observable.concat(getConversionsDb(0), getConversionsNet(0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BriefObserver<ArrayList<ConversationHolder.Item>>(disposable) {
                    @Override
                    public void onNext(@NonNull ArrayList<ConversationHolder.Item> items) {
                        if (getView() != null) {
                            getView().showConversions(items);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (getView() != null) {
                            getView().showConversionsError(e);
                        }
                    }
                });
        ;
    }

    private Observable<ArrayList<ConversationHolder.Item>> getConversionsDb(int pageCount) {
        return Observable.create(emitter -> {
            DaoSession session = AppDatabaseManager.getInstance().getDaoSession();
            if (session != null) {
                ConversationEntityDao dao = session.getConversationEntityDao();
                Query<ConversationEntity> query = dao.queryBuilder()
//                        .where(ConversationEntityDao.Properties.Cid.eq(userId))
                        .orderDesc(ConversationEntityDao.Properties.LastMsgTime)
                        .limit(pageCount)
                        .offset(pageCount * 20)
                        .build();
                List<ConversationEntity> conversationEntities = query.list();
                if (conversationEntities == null) {
                    conversationEntities = new ArrayList<>();
                }

                ArrayList<ClientConversation> clientConversations = MessageMapper.INSTANCE.toClientConversations(conversationEntities);
                ArrayList<ConversationHolder.Item> convItems = new ArrayList<>();
                for (ClientConversation clientConversation : clientConversations) {
                    ConversationHolder.Item item = new ConversationHolder.Item();
                    item.conv = clientConversation;
                    convItems.add(item);
                }
                emitter.onNext(convItems);
                emitter.onComplete();
            }else {
                emitter.onNext(new ArrayList<>());
            }
        });
    }

    private Observable<ArrayList<ConversationHolder.Item>> getConversionsNet(int pageCount) {
        return Observable.create(emitter -> {
            ReqLatestConv reqLatestConv = new ReqLatestConv();
            reqLatestConv.pageCount = pageCount;
            reqLatestConv.pageSize = 20;
            new MsRequester<RespLatestConv>(reqLatestConv, "/api/im/latest_conv")
                    .request(RespLatestConv.class, new ReqCallback<RespLatestConv>() {
                        @Override
                        public void onSuccess(RespLatestConv respLatestConv) {

                            ArrayList<ConversationHolder.Item> itemConvs = new ArrayList<>();
                            for (int i = respLatestConv.conversations.size() - 1; i >= 0; i--) {
                                ClientConversation conversation = respLatestConv.conversations.get(i);
                                ConversationHolder.Item itemConv = new ConversationHolder.Item();
                                itemConv.conv = conversation;
                                itemConvs.add(itemConv);
                            }

                            ArrayList<ConversationEntity> entities = MessageMapper.INSTANCE.toConversationsDb(respLatestConv.conversations);
                            DaoSession session = AppDatabaseManager.getInstance().getDaoSession();
                            if (session != null) {
                                ConversationEntityDao dao = session.getConversationEntityDao();
                                dao.insertOrReplaceInTx(entities);
                            }

                            emitter.onNext(itemConvs);
                            emitter.onComplete();
                        }

                        @Override
                        public void onFail(Exception exception, ReqWrapper req) {
                            emitter.onError(exception);
                        }
                    });

        });
    }


//    @Override
    public void onConversationUpdate() {
        getAllConversions();
    }


    public interface View extends BasicPresenter.View {
        void showConversions(@NonNull ArrayList<ConversationHolder.Item> items);
        void showConversionsError(Throwable exception);
    }
}
