//package com.jason.microstream.ui.conversation;
//
//import static java.lang.Thread.sleep;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//
//import com.jason.microstream.R;
//import com.jason.microstream.ui.base.BasicFragment;
//import com.jason.microstream.ui.base.BriefObserver;
//import com.jason.microstream.ui.compenent.recyclerview.BasicAdapter;
//import com.jason.microstream.ui.compenent.recyclerview.LoadMoreHolder;
//import com.jason.microstream.ui.conversation.holder.ConversationHolder;
//import com.jason.microstream.ui.conversation.holder.IConversation;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.util.ArrayList;
//
//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
//import io.reactivex.rxjava3.core.Observable;
//import io.reactivex.rxjava3.core.ObservableEmitter;
//import io.reactivex.rxjava3.core.ObservableOnSubscribe;
//import io.reactivex.rxjava3.disposables.CompositeDisposable;
//import io.reactivex.rxjava3.schedulers.Schedulers;
//
//@Deprecated
//public class ConversionListFragment extends BasicFragment<MsConversationListPresenter> implements
//        BasicAdapter.ItemClickListener<ConversationHolder.Item>
//        , MsConversationListPresenter.View, LoadMoreHolder.LoadMoreListener, BasicAdapter.ItemChildClickListener<ConversationHolder.Item> {
//    @Override
//    protected int onCreateLayout() {
//        return R.layout.fragment_conversation_list;
//    }
//
//    @Override
//    protected MsConversationListPresenter onCreatePresenter() {
//        return new MsConversationListPresenter(this);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        //消息列表、网络、通知设置、置顶删除已读、列表顺序更新、
//        initView();
//        initData();
//    }
//
//    RecyclerView conversation_list;
//    ConversationAdapter conversationAdapter;
//    ArrayList<ConversationHolder.Item> items;
//    private void initView() {
//        if(getView()==null) return;
//
//        conversation_list = getView().findViewById(R.id.conversation_list);
//
//        items = new ArrayList<>();
//        conversationAdapter = new ConversationAdapter(items,this);
//        conversation_list.setLayoutManager(new LinearLayoutManager(getContext()));
//        conversation_list.setAdapter(conversationAdapter);
//
//        conversationAdapter.enableLoadMore(true);
//        conversationAdapter.setLoadMoreListener(this);
//        conversationAdapter.setItemChildClickListener(this);
//
//        conversationAdapter.addHeader(LayoutInflater.from(getContext()).inflate(R.layout.header_conversation_list, null));
//    }
//
//    private void initData() {
//        getPresenter().getAllConversions();
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (conversation_list != null) {
//            getPresenter().getAllConversions();
//        }
//
//    }
//
//    @Override
//    public void onItemClick(ConversationHolder.Item item, int position) {
//        ChatDetailActivity.startActivity(getContext(), item.cid, item.type);
//    }
//
//    ConversationMenuDialog menuDialog;
//    IConversation menuDialogConversation;
//    @Override
//    public void onItemLongClick(ConversationHolder.Item item, int position) {
//        menuDialogConversation = ServiceManager.getInstance().getConversationManager().getConversation(item.cid);
//        menuDialog = new ConversationMenuDialog(getContext(), this);
//        menuDialog.showWithData(item.unreadCount == 0, item.isTop);
//    }
//
//    @Override
//    public void onReadChange(boolean isRead) {
//
//        if (menuDialogConversation.getUnreadCount() > 0) {
//            ServiceManager.getInstance().getConversationManager()
//                    .clearUnreadMessage(menuDialogConversation.getCid(), menuDialogConversation.getConversationType(), menuDialogConversation.getLastMsgId());
//        } else {
//            ConversationImpl conversation = (ConversationImpl) menuDialogConversation;
//            conversation.setUnreadCountNew(1);
//            EventBus.getDefault().post(new EventConversationChange(conversation.getCid()));
//            DatabaseManager.getInstance().getConversationManager().refresh(conversation);
//        }
//
//    }
//
//    @Override
//    public void onTopChange(boolean isTop) {
//        if (menuDialogConversation.getConversationType() == IConversation.TYPE_GROUP) {
//            ServiceManager.getInstance().getConversationManager()
//                    .setGroupTop(Long.valueOf(menuDialogConversation.getCid()), menuDialogConversation.getName(), !menuDialogConversation.isTop());
//        } else {
//            ServiceManager.getInstance().getConversationManager()
//                    .setSingleTop(menuDialogConversation.getCid(), menuDialogConversation.getName(), !menuDialogConversation.isTop());
//        }
//    }
//
//    @Override
//    public void onDelete() {
//        ServiceManager.getInstance().getConversationManager().deleteConversation(menuDialogConversation.getCid());
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventMainThread(EventConversationChange event) {
//        if (event.backgrougChange || event.isBackMask) {
//            return;
//        }
//        getPresenter().getAllConversions();
//
//
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventMainThread(EventNetworkChanged event) {
//        updateTipUI();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventMainThread(AceConnect event) {
////        if (mNetText != null && mNotifyLayout != null) {
////            if (event.isSuccess) {
////                if (showNotifyUI()) {
////                    mNetText.setVisibility(View.GONE);
////                    mNotifyLayout.setVisibility(View.VISIBLE);
////                } else {
////                    mNetText.setVisibility(View.GONE);
////                    mNotifyLayout.setVisibility(View.GONE);
////                }
////            }
////        }
//    }
//
//    private void updateTipUI() {
////        if (mNetText != null && mNotifyLayout != null) {
////            if (!NetworkUtils.isNetworkAvailable(getActivity())) {
////                mNetText.setVisibility(View.VISIBLE);
////                mNotifyLayout.setVisibility(View.GONE);
////            } else if (showNotifyUI()) {
////                mNetText.setVisibility(View.GONE);
////                mNotifyLayout.setVisibility(View.VISIBLE);
////            } else {
////                mNetText.setVisibility(View.GONE);
////                mNotifyLayout.setVisibility(View.GONE);
////            }
////        }
//    }
//
//
//    @Override
//    public void showConversions(ArrayList<ConversationHolder.Item> items) {
//        this.items.clear();
//        this.items.addAll(items);
//        conversationAdapter.notifyDataSetChanged();
////        conversationAdapter.loadMoreComplete();
//    }
//
//    @Override
//    public void showConversionsError(Throwable exception) {
//
//    }
//
//
//    @Override
//    public void onClickReload() {
//        onLoadMore();
//    }
//
//    @Override
//    public void onLoadMore() {
//
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//                    @Override
//                    public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Integer> emitter) throws Throwable {
//
//                        sleep(2000);
//                        emitter.onNext(0);
//
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BriefObserver<Integer>(new CompositeDisposable()) {
//                    @Override
//                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Integer integer) {
//                        conversationAdapter.loadMoreFail();
//                    }
//
//                    @Override
//                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
//
//                    }
//                });
//    }
//
//    @Override
//    public void onItemChildClick(View view, ConversationHolder.Item item, int position) {
//
//    }
//
//    @Override
//    public void onItemChildLongClick(View view, ConversationHolder.Item item, int position) {
//
//    }
//
//    @Override
//    public void onDestroy() {
//        EventBus.getDefault().unregister(this);
//        super.onDestroy();
//    }
//
//}
