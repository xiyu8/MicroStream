//package com.jason.microstream.ui.conversation;
//
//import com.jason.microstream.ui.base.BasicPresenter;
//import com.shinemo.imsdk.core.component.ConversationApi;
//import com.shinemo.openim.base.BasicPresenter;
//import com.shinemo.openim.base.BriefObserver;
//import com.shinemo.openim.uikit.main.conversation.holder.ConversationHolder;
//import com.shinemo.openim.uikit.main.conversation.mapper.ChatConvert;
//import com.shinemo.qoffice.biz.im.data.IConversationManager;
//import com.shinemo.qoffice.biz.im.data.impl.ConversationImpl;
//import com.shinemo.qoffice.common.ServiceManager;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
//import io.reactivex.rxjava3.annotations.NonNull;
//import io.reactivex.rxjava3.core.Observable;
//import io.reactivex.rxjava3.core.ObservableEmitter;
//import io.reactivex.rxjava3.core.ObservableOnSubscribe;
//import io.reactivex.rxjava3.schedulers.Schedulers;
//
//@Deprecated
//public class ConversionListPresenter extends BasicPresenter<ConversionListPresenter.View>
//        implements ConversationApi.OnConversationChangeListener {
//
//
//    public ConversionListPresenter(View mView) {
//        super(mView);
////        ShIm.get().addConversationListener(this);
//    }
//
//    public void getAllConversions() {
//        getConversions(-1);
//    }
//    public void getConversions(int type) {
//
//        Observable
//                .create(new ObservableOnSubscribe<ArrayList<ConversationHolder.Item>>() {
//                    @Override
//                    public void subscribe(@NonNull ObservableEmitter<ArrayList<ConversationHolder.Item>> emitter) throws Throwable {
//
//                        IConversationManager mConversationManager = ServiceManager.getInstance()
//                                .getConversationManager();
//                        List<ConversationImpl> mList = mConversationManager.getTabConversations();
//
//
//
//
//
//                        ArrayList<ConversationHolder.Item> items = ChatConvert.toConverItems(new ArrayList<>(mList));
//                        emitter.onNext(items);
//                        emitter.onComplete();
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BriefObserver<ArrayList<ConversationHolder.Item>>(disposable) {
//                    @Override
//                    public void onNext(@NonNull ArrayList<ConversationHolder.Item> items) {
//                        if (getView() != null) {
//                            getView().showConversions(items);
//                        }
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        if (getView() != null) {
//                            getView().showConversionsError(e);
//                        }
//                    }
//                });
//
//    }
//
//    @Override
//    public void onCoversationUpdate() {
//        getAllConversions();
//    }
//
//
//    public interface View extends BasicPresenter.View {
//        void showConversions(@NonNull ArrayList<ConversationHolder.Item> items);
//        void showConversionsError(Throwable exception);
//    }
//}
