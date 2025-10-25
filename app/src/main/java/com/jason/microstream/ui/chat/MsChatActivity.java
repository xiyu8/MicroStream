package com.jason.microstream.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gyf.immersionbar.ImmersionBar;
import com.jason.microstream.R;
import com.jason.microstream.account.AccountManager;
import com.jason.microstream.core.im.im_mode.ImModeMgr;
import com.jason.microstream.core.im.im_mode.msg.BaseMsg;
import com.jason.microstream.core.im.im_mode.msg.ImMsgConfig;
import com.jason.microstream.core.im.im_mode.msg.ImSendCallback;
import com.jason.microstream.core.im.im_mode.msg.TextMsg;
import com.jason.microstream.core.im.reqresp.MsRequester;
import com.jason.microstream.core.im.reqresp.ReqCallback;
import com.jason.microstream.core.im.reqresp.ReqWrapper;
import com.jason.microstream.core.im.reqresp.data.bean.chat.ReqCreateChat;
import com.jason.microstream.core.im.reqresp.data.bean.chat.RespCreateChat;
import com.jason.microstream.core.im.reqresp.data.bean.contact.ReqContact;
import com.jason.microstream.core.im.reqresp.data.bean.contact.RespContact;
import com.jason.microstream.core.im.reqresp.data.bean.message.ReqLatestMessages;
import com.jason.microstream.core.im.reqresp.data.bean.message.RespLatestMessages;
import com.jason.microstream.db.AppDatabaseManager;
import com.jason.microstream.db.entity.ConversationEntity;
import com.jason.microstream.db.entity.MessageEntity;
import com.jason.microstream.db.entity.generator.ConversationEntityDao;
import com.jason.microstream.db.entity.generator.DaoSession;
import com.jason.microstream.db.entity.generator.MessageEntityDao;
import com.jason.microstream.localbroadcast.Events;
import com.jason.microstream.localbroadcast.LocBroadcast;
import com.jason.microstream.localbroadcast.LocBroadcastReceiver;
import com.jason.microstream.mapper.MessageMapper;
import com.jason.microstream.mapper.MessageMapper2;
import com.jason.microstream.tool.DensityUtils;
import com.jason.microstream.tool.ToastUtil;
import com.jason.microstream.ui.base.BasicActivity;
import com.jason.microstream.ui.base.BasicPresenter;
import com.jason.microstream.ui.base.BriefObserver;
import com.jason.microstream.ui.chat.adapter.MessageAdapter;
import com.jason.microstream.ui.chat.message.ItemMessage;
import com.jason.microstream.ui.view_compenent.recyclerview.BasicAdapter;

import org.greenrobot.greendao.query.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MsChatActivity extends BasicActivity implements
        BasicAdapter.ItemChildClickListener<ItemMessage>, BasicAdapter.ItemClickListener<ItemMessage>
        , LocBroadcastReceiver {
    public static final String KEY_C_ID = "cid";
    public static final String KEY_C_TYPE = "c_type";

    private final String[] EVENTS = {Events.ACTION_ON_MSG_RECEIVE, Events.ACTION_ON_SDP_OFFER_RECEIVE, Events.ACTION_ON_LOGIN, Events.ACTION_ON_LOGOUT};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }


    RecyclerView msg_list;
//    View ai_msg_list_occupate;
//    AiGuideView ai_guide_view;

    View new_session_area;
    ImageView new_session_icon;
    TextView new_session_text;
    View history_session_area;

    View ai_cmd_shortcut;
    EditText main_edit;

    View main_center_area;
    View input_area;
    ConstraintLayout ai_root;
    int main_center_areaHeight;

    private int blankHeight = 0;
    private int screenHeight;
    private InputMethodManager imm;
    private void initView() {
        initBarArea();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Intent intent = getIntent();
//        sessionId = intent.getStringExtra(IN_SID);
//        long tempAiRoleId = intent.getLongExtra(IN_STAFF, 1);
//        this.aiRoleId = tempAiRoleId;


        msg_list = findViewById(R.id.msg_list);
//        ai_guide_view = findViewById(R.id.ai_guide_view);
        new_session_area = findViewById(R.id.new_session_area);
        new_session_text = findViewById(R.id.new_session_text);
        new_session_icon = findViewById(R.id.new_session_icon);
        history_session_area = findViewById(R.id.history_session_area);
        ai_cmd_shortcut = findViewById(R.id.ai_cmd_shortcut);
        main_edit = findViewById(R.id.main_edit);
        main_center_area = findViewById(R.id.main_center_area);
        input_area = findViewById(R.id.input_area);
        ai_root = findViewById(R.id.ai_root);
        new Handler().post(() -> main_center_areaHeight = main_center_area.getMeasuredHeight());
        main_edit.setImeOptions(EditorInfo.IME_ACTION_SEND);
        main_edit.setRawInputType(InputType.TYPE_CLASS_TEXT);
        main_edit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                String sendContent = main_edit.getText().toString();
                    if (sendContent != null && !sendContent.equals("")
                            && (msgItems.size() == 0 || msgItems.get(msgItems.size() - 1).msg.getState() != ImMsgConfig.SendState.SENDING)) {
//                        if (cId == null) {
//                            ToastUtil.show(AiChatDetailActivity.this, R.string.ai_service_exception);
//                            getCurrentConver();
//                            return true;
//                        }
                        sendTextMsg(sendContent);
                        main_edit.setText("");
//                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                        im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }

//                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            return true;
        });


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View content = findViewById(android.R.id.content);
        screenHeight = content.getHeight();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        processAllTouch();

        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                int rectBottom = rect.bottom;
                if (rectBottom > screenHeight) screenHeight = rectBottom;
                int newBlankheight = screenHeight - rectBottom;
//                if (newBlankheight < 0) newBlankheight = 0;

                if (newBlankheight != blankHeight) {
                    if (newBlankheight <= 0) {
//                    // keyboard close
//                    if (onKeyBoardStatusChangeListener != null) {
//                        onKeyBoardStatusChangeListener.OnKeyBoardClose(blankHeight);
//                    }
                        showHideMode(true, newBlankheight);
                    } else {
//                    // keyboard pop
//                    if (onKeyBoardStatusChangeListener != null) {
//                        onKeyBoardStatusChangeListener.OnKeyBoardPop(newBlankheight);
//                    }

                        msg_list.scrollToPosition(msgItems.size());
                        showHideMode(false, newBlankheight);
                    }
                }
                blankHeight = newBlankheight;
            }
        });

    }


    private void showHideMode(boolean isShow, int newBlankheight) {
        if (isShow) {
            history_session_area.setVisibility(View.VISIBLE);
            new_session_area.setVisibility(View.VISIBLE);
            ai_cmd_shortcut.setVisibility(View.VISIBLE);
            main_edit.setBackground(getDrawable(R.drawable.shape_gray_bg_corner8));
//            main_center_area.getLayoutParams().height = main_center_areaHeight;
//            main_center_area.requestLayout();
//            //代码修改ConstraintLayout约束关系
//            ConstraintSet set = new ConstraintSet();
//            //克隆父布局(ConstraintLayout)的约束关系
//            set.clone(ai_root);
//            //清除childView布局文件里设置的某个约束
//            set.clear(R.id.input_area, ConstraintSet.TOP);
//            //设置新的约束关系，下面效果相当于 app:layout_constraintBottom_toBottomOf="parent"
//            set.connect(R.id.input_area, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
//            set.connect(R.id.input_area, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
//            //应用到父布局
//            set.applyTo(ai_root);
            ((LinearLayout.LayoutParams) (ai_root.getLayoutParams())).bottomMargin = 0;
            ai_root.requestLayout();
            msg_list_occupate.setPadding(0, DensityUtils.dp2px(this, 50), 0, 0);
        } else {
            history_session_area.setVisibility(View.GONE);
            new_session_area.setVisibility(View.GONE);
            ai_cmd_shortcut.setVisibility(View.GONE);
            main_edit.setBackground(getDrawable(R.drawable.shape_ai_main_edit_bg));
//            main_center_area.getLayoutParams().height = main_center_areaHeight - newBlankheight;
//            main_center_area.requestLayout();
//            ConstraintSet set = new ConstraintSet();
//            set.clone(ai_root);
//            set.clear(R.id.input_area, ConstraintSet.BOTTOM);
//            set.connect(R.id.input_area, ConstraintSet.TOP, R.id.main_center_area, ConstraintSet.BOTTOM);
//            set.connect(R.id.input_area, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
//            set.applyTo(ai_root);
            ((LinearLayout.LayoutParams) (ai_root.getLayoutParams())).bottomMargin = newBlankheight;
            ai_root.requestLayout();
            msg_list_occupate.setPadding(0, DensityUtils.dp2px(this, 0), 0, 0);
        }
    }

    private void initBarArea() {
        getToolBarArea().setVisibility(View.VISIBLE);
        ImmersionBar.with(this)
                .titleBarMarginTop(parentLinearLayout)
                .init();
        setToolbarBg("#ffffff");
        setToolBarTitle("");
        setToolbarBoundary("#cccccc");
    }

    private void processAllTouch() {
//        main_edit.setOnTouchListener(touchListener);
        ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
        processAllTouchImp(viewGroup);
    }

    private void processAllTouchImp(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (!(view instanceof EditText)) {
                view.setOnTouchListener(touchListener);
                if (view instanceof ViewGroup) {
                    processAllTouchImp((ViewGroup) view);
                }
            }
        }
    }
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            hideKeyBoard();
            return false;
        }
    };

    public void hideKeyBoard() {
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
//                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }

        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String userName;
    private String userId;
    private String cId;
    private int cType;
    private ArrayList<ItemMessage> msgItems = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private View msg_list_occupate;
    private void initData() {
        Intent comeIntent = getIntent();
        userName = comeIntent.getStringExtra("userName");
        userId = comeIntent.getStringExtra("userId");
        cId = comeIntent.getStringExtra(KEY_C_ID);
        cType = comeIntent.getIntExtra(KEY_C_TYPE, 0);
        if (cId == null || cType == 0) { //single chat
            if (Long.parseLong(userId) < Long.parseLong(AccountManager.get().getUid())) {
                cId = "s_" + userId + "_" + AccountManager.get().getUid();
            } else {
                cId = "s_" + AccountManager.get().getUid() + "_" + userId;
            }
            cType = 1;
        } else {
            String[] ss = cId.split("_");
            if (ss[0].equals("s")) {
                cType = 1;
            } else {
                cType = 2;
            }
            if (ss[1].equals(AccountManager.get().getUid())) {
                userId = ss[2];
            } else {
                userId = ss[1];
            }
            if (userName == null) {
                userName = "";
            }
        }
        setToolBarTitle(userName);


        messageAdapter = new MessageAdapter(msgItems, this);
        messageAdapter.setItemChildClickListener(this);
        msg_list.setLayoutManager(new LinearLayoutManager(this));
        msg_list.setAdapter(messageAdapter);
        msg_list_occupate = LayoutInflater.from(this).inflate(R.layout.view_occupate2, null);
        messageAdapter.addFooter(msg_list_occupate);

        LocBroadcast.getInstance().registerBroadcast(this, EVENTS);

        getLatestMessages();
    }

    private void getLatestMessages() {
        Observable.concat(getLatestMessagesDb(0), getLatestMessagesNet(0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BriefObserver<ArrayList<ItemMessage>>(disposable) {
                    @Override
                    public void onNext(@NonNull ArrayList<ItemMessage> itemMessages) {
                        if (itemMessages.size() != 0) {
                            msgItems.clear();
                            msgItems.addAll(itemMessages);
                            messageAdapter.notifyDataSetChanged();
                            msg_list.scrollToPosition(msgItems.size() - 1);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                })
        ;
    }

    private Observable<ArrayList<ItemMessage>> getLatestMessagesDb(int pageCount) {
        return Observable.create(emitter -> {
            DaoSession session = AppDatabaseManager.getInstance().getDaoSession();
            if (session != null) {
                MessageEntityDao dao = session.getMessageEntityDao();
                Query<MessageEntity> query = dao.queryBuilder()
                        .where(MessageEntityDao.Properties.Cid.eq(userId))
                        .orderDesc(MessageEntityDao.Properties.SendTime)
                        .limit(pageCount)
                        .offset(pageCount * 20)
                        .build();
                List<MessageEntity> msgEntities = query.list();
                if (msgEntities == null) {
                    msgEntities = new ArrayList<>();
                }

                ArrayList<BaseMsg> baseMsgs = MessageMapper2.INSTANCE.toMessageConcrete(msgEntities);
                ArrayList<ItemMessage> msgItems = new ArrayList<>();
                for (int i = baseMsgs.size() - 1; i >= 0; i--) {
                    BaseMsg baseMsg = baseMsgs.get(i);
                    ItemMessage itemMessage = new ItemMessage();
                    itemMessage.msg = baseMsg;
                    msgItems.add(itemMessage);
                }

                emitter.onNext(msgItems);
                emitter.onComplete();
            }else {
                emitter.onNext(new ArrayList<>());
            }
        });
    }

    private Observable<ArrayList<ItemMessage>> getLatestMessagesNet(int pageCount) {
        return Observable.create(emitter -> {
            ReqLatestMessages reqLatestMessages = new ReqLatestMessages();
            reqLatestMessages.pageCount = pageCount;
            reqLatestMessages.pageSize = 20;
            reqLatestMessages.cid = cId;
            new MsRequester<RespLatestMessages>(reqLatestMessages, "/api/im/latest_messages")
                    .request(RespLatestMessages.class, new ReqCallback<RespLatestMessages>() {
                        @Override
                        public void onSuccess(RespLatestMessages respLatestMessages) {

                            ArrayList<ItemMessage> itemMessages = new ArrayList<>();
                            for (int i = respLatestMessages.messages.size() - 1; i >= 0; i--) {
                                BaseMsg baseMsg = respLatestMessages.messages.get(i);
                                ItemMessage itemMessage = new ItemMessage();
                                itemMessage.msg = MessageMapper2.toConcreteMsg(baseMsg);
                                itemMessages.add(itemMessage);
                            }

                            ArrayList<MessageEntity> entities = MessageMapper.INSTANCE.toMessageDb(respLatestMessages.messages);
                            DaoSession session = AppDatabaseManager.getInstance().getDaoSession();
                            if (session != null) {
                                MessageEntityDao dao = session.getMessageEntityDao();
                                dao.insertOrReplaceInTx(entities);
                            }

                            emitter.onNext(itemMessages);
                            emitter.onComplete();
                        }

                        @Override
                        public void onFail(Exception exception, ReqWrapper req) {
                            emitter.onError(exception);
                        }
                    });

        });
    }


    @Override
    public void onItemChildClick(View view, ItemMessage item, int position) {

    }

    @Override
    public void onItemChildLongClick(View view, ItemMessage item, int position) {

    }

    @Override
    public void onItemClick(ItemMessage item, int position) {

    }

    @Override
    public void onItemLongClick(ItemMessage item, int position) {

    }

    private void sendTextMsg(String sendContent) {
        if (msgItems.size() == 0) { //没有历史消息，代表单聊会话没被创建过
            ReqCreateChat reqCreateChat = new ReqCreateChat();
            if (Long.parseLong(userId) < Long.parseLong(AccountManager.get().getUid())) {
                reqCreateChat.cid = "s_" + userId + "_" + AccountManager.get().getUid();
            } else {
                reqCreateChat.cid = "s_" + AccountManager.get().getUid() + "_" + userId;
            }
            reqCreateChat.cName = userName;
            reqCreateChat.cType = ReqCreateChat.ChatType.TYPE_SINGLE;
            reqCreateChat.cState = ReqCreateChat.ChatState.TYPE_NORMAL;
            reqCreateChat.withUid = userId;
            reqCreateChat.memberCount = 2;
            Observable
                    .create((ObservableOnSubscribe<RespCreateChat>) emitter -> {
                        // ......
                        new MsRequester<RespCreateChat>(reqCreateChat, "/api/im/create_single_chat")
                                .request(RespCreateChat.class, new ReqCallback<RespCreateChat>() {
                                    @Override
                                    public void onSuccess(RespCreateChat respCreateChat) {
                                        emitter.onNext(respCreateChat);
                                        emitter.onComplete();
                                    }

                                    @Override
                                    public void onFail(Exception exception, ReqWrapper req) {
                                        emitter.onError(exception);
                                    }
                                });


                    })
                    .map(respCreateChat -> {
                        DaoSession session = AppDatabaseManager.getInstance().getDaoSession();
                        if (session != null) {
                            ConversationEntityDao dao = session.getConversationEntityDao();
                            dao.insertOrReplaceInTx(MessageMapper2.INSTANCE.toConversationDb(reqCreateChat, respCreateChat));
                            LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_CONV_ADD
                                    , MessageMapper.INSTANCE.toClientConversation(MessageMapper2.INSTANCE.toConversationDb(reqCreateChat, respCreateChat)));
                        }
                        return respCreateChat;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(new BriefObserver<RespCreateChat>(disposable) {
                        @Override
                        public void onNext(@NonNull RespCreateChat respCreateChat) {
//                            this.respCreateChat = respCreateChat;
                            sendTextMsgImp(sendContent);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            main_edit.setText(sendContent);
                            ToastUtil.show(MsChatActivity.this, "服务异常：会话创建失败");
                        }
                    })
            ;
        } else {
            sendTextMsgImp(sendContent);
        }

//        DaoSession session = AppDatabaseManager.getInstance().getDaoSession();
//        if (session != null) {
//            MessageEntityDao dao = session.getMessageEntityDao();
//            dao.insertOrReplaceInTx(MessageMapper2.INSTANCE.toTextMessageDb(textMsg));
//        }

    }
    private void sendTextMsgImp(String sendContent) {
        TextMsg textMsg = new TextMsg();
        textMsg.text = sendContent;
        textMsg.setContent(sendContent);
        textMsg.setContentAbstract(sendContent);
        textMsg.setFromId(AccountManager.get().getUid());
        textMsg.setFromName(AccountManager.get().getUsername());
        textMsg.setToId(userId);
        textMsg.setToName(userName);
        String cid;
        if (Long.parseLong(userId) < Long.parseLong(AccountManager.get().getUid())) {
            cid = "s_" + userId + "_" + AccountManager.get().getUid();
        } else {
            cid = "s_" + AccountManager.get().getUid() + "_" + userId;
        }
        textMsg.setCid(cid);
        textMsg.setState(ImMsgConfig.SendState.SENDING);

        ItemMessage itemMessage = new ItemMessage();
        itemMessage.msg = textMsg;
        msgItems.add(itemMessage);
        final int tempPosition = msgItems.size() - 1;
        runOnUiThread(() -> {
            messageAdapter.notifyDataSetChanged();
            msg_list.scrollToPosition(msgItems.size());
        });

        DaoSession session = AppDatabaseManager.getInstance().getDaoSession();
        MessageEntityDao dao = session.getMessageEntityDao();
        Observable.create((ObservableOnSubscribe<TextMsg>) emitter -> {
                    ImModeMgr.getImManager().sendTextMsg(textMsg, new ImSendCallback() {
                        @Override
                        public void onSendSuccess(TextMsg textMsg1) {
                            dao.insertOrReplaceInTx(MessageMapper2.INSTANCE.toTextMessageDb(textMsg1));
                            LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_MSG_ADD, textMsg1);

                            DaoSession session = AppDatabaseManager.getInstance().getDaoSession();
                            if (session != null) {
                                ConversationEntityDao dao = session.getConversationEntityDao();
                                List<ConversationEntity> conversationEntities = dao.queryBuilder()
                                        .where(ConversationEntityDao.Properties.Cid.eq(cid))
                                        .build()
                                        .list();
                                if (conversationEntities.size() > 0) {
                                    ConversationEntity conversation = conversationEntities.get(0);
                                    conversation.setLastMsgContent(textMsg.text);
                                    conversation.setLastMsgTime(textMsg.sendTime);
                                    conversation.setLastMsgId(textMsg1.seqId);
                                    conversation.setLastMsgSenderName(textMsg.getFromName());
                                    conversation.setLastMsgSenderId(textMsg.getFromId());
                                }
                                LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_CONV_UPDATE, MessageMapper.INSTANCE.toClientConversation(conversationEntities.get(0)));
                            }

                            emitter.onNext(textMsg1);
                            emitter.onComplete();
                        }

                        @Override
                        public void onSendFail(TextMsg textMsg1, IOException e) {
                            dao.insertOrReplaceInTx(MessageMapper2.INSTANCE.toTextMessageDb(textMsg1));
                            LocBroadcast.getInstance().sendBroadcast(Events.ACTION_ON_MSG_ADD, textMsg1);
                            emitter.onError(e);
                        }
                    });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BriefObserver<TextMsg>(disposable) {
                    @Override
                    public void onNext(@NonNull TextMsg textMsg) {
                        messageAdapter.notifyItemChanged(tempPosition);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        messageAdapter.notifyItemChanged(tempPosition);
                    }
                })
        ;
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        runOnUiThread(() -> {
            switch (broadcastName) {
                case Events.ACTION_ON_MSG_RECEIVE:
                    handleMsgReceive(obj);
                    break;
                case Events.ACTION_ON_SDP_OFFER_RECEIVE:
                    break;
                case Events.ACTION_ON_LOGIN:
//                    connection_status.setText("connected");
                    break;
                case Events.ACTION_ON_LOGOUT:
//                    connection_status.setText("unconnected");
                    break;
            }
        });

    }

    private void handleMsgReceive(Object obj) {
        if (obj instanceof BaseMsg) {
            if (((BaseMsg) obj).getCid().equals(cId)) {
                BaseMsg baseMsg = MessageMapper2.toConcreteMsg((BaseMsg) obj);
                ItemMessage itemMessage = new ItemMessage();
                itemMessage.msg = baseMsg;
                msgItems.add(itemMessage);

                runOnUiThread(() -> {
                    final int tempPosition = msgItems.size() - 1;
                    messageAdapter.notifyItemChanged(tempPosition);
                    msg_list.scrollToPosition(msgItems.size());
                });

            }
        }

    }

    @Override
    protected BasicPresenter onCreatePresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ms_chat;
    }

    @Override
    protected void onDestroy() {
        LocBroadcast.getInstance().unRegisterBroadcast(this, EVENTS);
        super.onDestroy();
    }
}