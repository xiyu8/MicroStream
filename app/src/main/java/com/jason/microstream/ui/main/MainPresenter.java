package com.jason.microstream.ui.main;

import com.jason.microstream.ui.base.BasicPresenter;

public class MainPresenter extends BasicPresenter<BasicPresenter.View> {
    public MainPresenter(View view) {
        super(view);
    }


    public interface View extends BasicPresenter.View{

    }
}
