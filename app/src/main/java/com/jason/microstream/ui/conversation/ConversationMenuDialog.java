package com.jason.microstream.ui.conversation;

import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;


import java.util.ArrayList;

//public class ConversationMenuDialog extends ListDialog<String> {
//
//    public Animation mExitAnim;//退出动画  
//    public Animation mEnterAnim;//进入动画  
//
//
//    public ConversationMenuDialog(@NonNull Context context, OnMenuClickListener menuClickListener) {
//        super(context);
//        this.onMenuClickListener = menuClickListener;
//    }
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        initView();
//    }
//
//
//    @Override
//    public void onItemClick(ListDialogAdapter.Item item, int position) {
//        if (onMenuClickListener == null) {
//            dismiss();
//            return;
//        }
//        if (position == 0) {
//            isRead = !isRead;
//            onMenuClickListener.onReadChange(isRead);
//        } else if (position == 1) {
//            isTop = !isTop;
//            onMenuClickListener.onTopChange(isTop);
//        } else if (position == 2) {
//            onMenuClickListener.onDelete();
//        }
//        dismiss();
//    }
//
//    boolean isRead;
//    boolean isTop;
//    private void initView() {
//        setData(isRead, isTop);
//    }
//
//    public void showWithData(boolean isRead,boolean isTop) {
//        setData(isRead, isTop);
//        show();
//    }
//
//    public void setData(boolean isRead,boolean isTop) {
//        this.isRead = isRead;
//        this.isTop = isTop;
//        ArrayList<ListDialogAdapter.Item> items = new ArrayList<>();
//        items.add(new ListDialogAdapter.Item(getContext().getString(isRead?R.string.conversation_menu_unread:R.string.conversation_menu_read)));
//        items.add(new ListDialogAdapter.Item(getContext().getString(isTop?R.string.conversation_menu_untop:R.string.conversation_menu_top)));
//        items.add(new ListDialogAdapter.Item(getContext().getString(R.string.conversation_menu_delete)));
//        showData(items);
//    }
//
//    public void show() {
//        super.show();
//        enterAnimation();//进入动画  
//    }
//
//    private void enterAnimation() {
//        if (mEnterAnim == null) {
//            mEnterAnim = new TranslateAnimation(1, 0, 1, 0, 1, 1, 1, 0);
//            mEnterAnim.setDuration(200);
//        }
//        mView.startAnimation(mEnterAnim);
//    }
//
//    private void exitAnimation() {
//        if (mExitAnim == null) {
//            mExitAnim = new TranslateAnimation(1, 0, 1, 0, 1, 0, 1, 1);
//            mExitAnim.setDuration(200);
//            mExitAnim.setAnimationListener(
//                    new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            dismissWithoutAnimator(); //动画完成执行关闭  
//                        }
//                    });
//        }
//        mView.startAnimation(mExitAnim);
//    }
//
//    private void dismissWithoutAnimator() {
//        super.dismiss();
//    }
//
//    @Override
//    public void dismiss() {
//        exitAnimation();
//    }
//
//
//    OnMenuClickListener onMenuClickListener;
//    public interface OnMenuClickListener{
//        void onReadChange(boolean isRead);
//        void onTopChange(boolean isTop);
//        void onDelete();
//    }
//
//}
