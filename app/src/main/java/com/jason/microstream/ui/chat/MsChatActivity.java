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
import com.jason.microstream.core.im.im_mode.msg.ImMsgConfig;
import com.jason.microstream.core.im.im_mode.msg.ImSendCallback;
import com.jason.microstream.core.im.im_mode.msg.TextMsg;
import com.jason.microstream.localbroadcast.Events;
import com.jason.microstream.localbroadcast.LocBroadcast;
import com.jason.microstream.localbroadcast.LocBroadcastReceiver;
import com.jason.microstream.tool.DensityUtils;
import com.jason.microstream.ui.base.BasicActivity;
import com.jason.microstream.ui.base.BasicPresenter;
import com.jason.microstream.ui.chat.adapter.MessageAdapter;
import com.jason.microstream.ui.chat.message.ItemMessage;
import com.jason.microstream.ui.view_compenent.recyclerview.BasicAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class MsChatActivity extends BasicActivity implements
        BasicAdapter.ItemChildClickListener<ItemMessage>, BasicAdapter.ItemClickListener<ItemMessage>
        , LocBroadcastReceiver {
    public final String[] EVENTS = {Events.ACTION_ON_MSG_RECEIVE, Events.ACTION_ON_SDP_OFFER_RECEIVE, Events.ACTION_ON_LOGIN, Events.ACTION_ON_LOGOUT};

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
//                    if (sendContent != null && !sendContent.equals("")
//                            && (msgItems.size() == 0 || msgItems.get(msgItems.size() - 1).status != ItemMessage.Status.GEN_ING)) {
//                        if (currentDigitalStaff == null || sessionId == null) {
//                            ToastUtil.show(AiChatDetailActivity.this, R.string.ai_service_exception);
//                            getCurrentConver();
//                            return true;
//                        }
//                        sendAIContent(sendContent);
////                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
////                        im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                    }

                sendTextMsg(sendContent);
                main_edit.setText("");
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
            main_edit.setBackground(getDrawable(R.drawable.shape_white_solid_corner8));
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
    String userName;
    String userId;
    ArrayList<ItemMessage> msgItems = new ArrayList<>();
    MessageAdapter messageAdapter;
    View msg_list_occupate;
    private void initData() {
        Intent comeIntent = getIntent();
        userName = comeIntent.getStringExtra("userName");
        userId = comeIntent.getStringExtra("userId");

        setToolBarTitle(userName);


        messageAdapter = new MessageAdapter(msgItems, this);
        messageAdapter.setItemChildClickListener(this);
        msg_list.setLayoutManager(new LinearLayoutManager(this));
        msg_list.setAdapter(messageAdapter);
        msg_list_occupate = LayoutInflater.from(this).inflate(R.layout.view_occupate2, null);
        messageAdapter.addFooter(msg_list_occupate);

        LocBroadcast.getInstance().registerBroadcast(this, EVENTS);

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
        TextMsg textMsg = new TextMsg();
        textMsg.text = sendContent;
        textMsg.fromId = AccountManager.get().getUid();
        textMsg.toId = userId;
        textMsg.state = ImMsgConfig.SendState.SENDING;

        ItemMessage itemMessage = new ItemMessage();
        itemMessage.msg = textMsg;
        msgItems.add(itemMessage);
        final int tempPosition = msgItems.size() - 1;
        messageAdapter.notifyDataSetChanged();
        msg_list.scrollToPosition(msgItems.size());


        ImModeMgr.getImManager().sendTextMsg(textMsg, new ImSendCallback() {
            @Override
            public void onSendSuccess(TextMsg textMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.notifyItemChanged(tempPosition);
                    }
                });
            }

            @Override
            public void onSendFail(TextMsg textMsg, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.notifyItemChanged(tempPosition);
                    }
                });

            }
        });

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (obj instanceof TextMsg) {
                    TextMsg textMsg = (TextMsg) obj;

                    ItemMessage itemMessage = new ItemMessage();
                    itemMessage.msg = textMsg;
                    msgItems.add(itemMessage);
                    final int tempPosition = msgItems.size() - 1;
                    messageAdapter.notifyItemChanged(tempPosition);
                    msg_list.scrollToPosition(msgItems.size());

                } else {

                }

            }
        });

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