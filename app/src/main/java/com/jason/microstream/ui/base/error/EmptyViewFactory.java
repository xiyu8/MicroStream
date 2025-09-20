package com.jason.microstream.ui.base.error;

import android.view.View;

public interface EmptyViewFactory {

  View getEmptyView(String hint,String hintSub,int img);

  View getErrorView(String hint,String hintSub,int img);

}
