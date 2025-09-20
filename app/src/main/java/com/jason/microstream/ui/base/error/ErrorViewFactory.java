package com.jason.microstream.ui.base.error;

import android.view.View;

public interface ErrorViewFactory {


  View getErrorView(String hint, String hintSub, int img);


}
