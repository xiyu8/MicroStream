package com.jason.microstream.ui.view_compenent.avatar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jason.microstream.ui.conversation.avatar.GroupUser;

import java.util.List;

public class AvatarView extends FrameLayout {
    public AvatarView(@NonNull Context context) {
        super(context);
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }



    public void setAvatar(String cid, int typeGroup, boolean b, List<GroupUser> memberAvatars, String chatName) {

    }

}
