package com.jason.microstream.ui.contact;

import com.jason.microstream.R;
import com.jason.microstream.ui.base.BasicPresenter;
import com.jason.microstream.ui.main.MainFragment;

public class MsContactFragment extends MainFragment {
    @Override
    protected int onCreateLayout() {
        return R.layout.fragment_contact;
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
