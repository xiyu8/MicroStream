package com.jason.microstream.ui.conversation;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gyf.immersionbar.ImmersionBar;
import com.jason.microstream.R;
import com.jason.microstream.broadcastevent.EventNetworkChanged;
import com.jason.microstream.core.im.reqresp.data.bean.chat.ClientConversation;
import com.jason.microstream.localbroadcast.Events;
import com.jason.microstream.localbroadcast.LocBroadcast;
import com.jason.microstream.localbroadcast.LocBroadcastReceiver;
import com.jason.microstream.ui.base.BriefObserver;
import com.jason.microstream.ui.chat.MsChatActivity;
import com.jason.microstream.ui.view_compenent.recyclerview.BasicAdapter;
import com.jason.microstream.ui.view_compenent.recyclerview.LoadMoreHolder;
import com.jason.microstream.ui.conversation.holder.ConversationHolder;
import com.jason.microstream.ui.main.MainFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MsConversationListFragment extends MainFragment<MsConversationListPresenter>
        implements MsConversationListPresenter.View
        , BasicAdapter.ItemClickListener<ConversationHolder.Item>
        , LoadMoreHolder.LoadMoreListener, BasicAdapter.ItemChildClickListener<ConversationHolder.Item>, LocBroadcastReceiver {
    @Override
    protected int onCreateLayout() {
        return R.layout.fragment_conversation_list;
    }

    @Override
    protected MsConversationListPresenter onCreatePresenter() {
        return new MsConversationListPresenter(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 视图绘制前开始过渡
        ImageView trans_logo = getView().findViewById(R.id.splash_logo);
        trans_logo.getViewTreeObserver()
                .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        trans_logo.getViewTreeObserver().removeOnPreDrawListener(this);
                        // 让 ActivityB 继续执行共享元素过渡
                        (getActivity()).supportStartPostponedEnterTransition();
                        return true;
                    }
                });
    }

    @Override
    public void lazyInit() {
        super.lazyInit();
        //消息列表、网络、通知设置、置顶删除已读、列表顺序更新、
        initView();
        initData();
        if (conversation_list != null) {
            getPresenter().getAllConversions();
        }

    }


    RecyclerView conversation_list;
    ConversationAdapter conversationAdapter;
    Map<String, ConversationHolder.Item> itemsMap;
    LinkedList<ConversationHolder.Item> items;
    private void initView() {
        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .navigationBarDarkIcon(true)
                .titleBarMarginTop(getView().findViewById(R.id.conversation_list_top))
                .init();
        if(getView()==null) return;

        conversation_list = getView().findViewById(R.id.conversation_list);

        items = new LinkedList<>();
        itemsMap = new HashMap<>();
        conversationAdapter = new ConversationAdapter(items,this);
        conversation_list.setLayoutManager(new LinearLayoutManager(getContext()));
        conversation_list.setAdapter(conversationAdapter);

        conversationAdapter.enableLoadMore(false);
        conversationAdapter.setLoadMoreListener(this);
        conversationAdapter.setItemChildClickListener(this);

        conversationAdapter.addHeader(LayoutInflater.from(getContext()).inflate(R.layout.header_conversation_list, null));
    }

    String[] EVENTS = {Events.ACTION_ON_CONV_ADD, Events.ACTION_ON_CONV_UPDATE};
    private void initData() {
        LocBroadcast.getInstance().registerBroadcast(this, EVENTS);
        getPresenter().getAllConversions();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

//    ConversationMenuDialog menuDialog;
//    IConversation menuDialogConversation;

    @Override
    public void onItemClick(ConversationHolder.Item item, int position) {
        Intent intent = new Intent(getContext(), MsChatActivity.class);
        intent.putExtra(MsChatActivity.KEY_C_ID, item.conv.cid);
        intent.putExtra(MsChatActivity.KEY_C_TYPE, 1);
        intent.putExtra("userName", item.conv.cName);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(ConversationHolder.Item item, int position) {
//        menuDialogConversation = ServiceManager.getInstance().getConversationManager().getConversation(item.cid);
//        menuDialog = new ConversationMenuDialog(getContext(), this);
//        menuDialog.showWithData(item.unreadCount == 0, item.isTop);
    }



    public void onReadChange(boolean isRead) {

//        if (menuDialogConversation.getUnreadCount() > 0) {
//            ServiceManager.getInstance().getConversationManager()
//                    .clearUnreadMessage(menuDialogConversation.getCid(), menuDialogConversation.getConversationType(), menuDialogConversation.getLastMsgId());
//        } else {
//            ConversationImpl conversation = (ConversationImpl) menuDialogConversation;
//            conversation.setUnreadCountNew(1);
//            EventBus.getDefault().post(new EventConversationChange(conversation.getCid()));
//            DatabaseManager.getInstance().getConversationManager().refresh(conversation);
//        }

    }



    public void onTopChange(boolean isTop) {
//        if (menuDialogConversation.getConversationType() == IConversation.TYPE_GROUP) {
//            ServiceManager.getInstance().getConversationManager()
//                    .setGroupTop(Long.valueOf(menuDialogConversation.getCid()), menuDialogConversation.getName(), !menuDialogConversation.isTop());
//        } else {
//            ServiceManager.getInstance().getConversationManager()
//                    .setSingleTop(menuDialogConversation.getCid(), menuDialogConversation.getName(), !menuDialogConversation.isTop());
//        }
    }



    public void onDelete() {
//        ServiceManager.getInstance().getConversationManager().deleteConversation(menuDialogConversation.getCid());
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEventMainThread(EventConversationChange event) {
//        if (event.backgrougChange || event.isBackMask) {
//            return;
//        }
//        getPresenter().getAllConversions();
//
//
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventNetworkChanged event) {
        updateTipUI();
    }
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

    private void updateTipUI() {
//        if (mNetText != null && mNotifyLayout != null) {
//            if (!NetworkUtils.isNetworkAvailable(getActivity())) {
//                mNetText.setVisibility(View.VISIBLE);
//                mNotifyLayout.setVisibility(View.GONE);
//            } else if (showNotifyUI()) {
//                mNetText.setVisibility(View.GONE);
//                mNotifyLayout.setVisibility(View.VISIBLE);
//            } else {
//                mNetText.setVisibility(View.GONE);
//                mNotifyLayout.setVisibility(View.GONE);
//            }
//        }
    }

    @Override
    public void onClickReload() {
        onLoadMore();
    }

    @Override
    public void onLoadMore() {

        Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Integer> emitter) throws Throwable {

                        sleep(2000);
                        emitter.onNext(0);

                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BriefObserver<Integer>(new CompositeDisposable()) {
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Integer integer) {
                        conversationAdapter.loadMoreFail();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }
                });
    }


    @Override
    public void onItemChildClick(View view, ConversationHolder.Item item, int position) {

    }

    @Override
    public void onItemChildLongClick(View view, ConversationHolder.Item item, int position) {

    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        LocBroadcast.getInstance().unRegisterBroadcast(this, EVENTS);
        super.onDestroy();
    }


    @Override
    public void showConversions(ArrayList<ConversationHolder.Item> items) {
        this.items.clear();
        itemsMap.clear();
        for (ConversationHolder.Item item : items) {
            itemsMap.put(item.conv.cid, item);
        }
        this.items.addAll(items);
        conversationAdapter.notifyDataSetChanged();
        conversationAdapter.loadMoreComplete();
    }

    @Override
    public void showConversionsError(Throwable exception) {

    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        if (broadcastName.equals(Events.ACTION_ON_CONV_ADD)) {
            ClientConversation conversation = (ClientConversation) obj;
            ConversationHolder.Item item = new ConversationHolder.Item();
            item.conv = conversation;
            itemsMap.put(item.conv.cid, item);
            items.addFirst(item);
            requireActivity().runOnUiThread(() -> {
                conversationAdapter.notifyItemInserted(0 + 1);
            });
        } else if (broadcastName.equals(Events.ACTION_ON_CONV_UPDATE)) {
            ClientConversation conversation = (ClientConversation) obj;
            ConversationHolder.Item item = itemsMap.get(conversation.cid);
            if (item != null) {
                int fromPosition = items.indexOf(item);
                items.remove(item);
                items.addFirst(item);
                item.conv = conversation;
                getActivity().runOnUiThread(() -> {
                    conversationAdapter.notifyItemMoved(fromPosition + 1, 0 + 1);
                    conversationAdapter.notifyItemChanged(0 + 1);
                });
            } else {
                //TODO:
            }
        }
    }


    @Override
    protected int getSelectedIcon() {
        return R.drawable.tab_conversation_selected;
    }

    @Override
    protected int getUnselectedIcon() {
        return R.drawable.tab_conversation_unselected;
    }
}
