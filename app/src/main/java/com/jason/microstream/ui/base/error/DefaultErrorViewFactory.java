package com.jason.microstream.ui.base.error;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jason.microstream.R;
import com.jason.microstream.ui.base.BasicActivity;


public class DefaultErrorViewFactory implements ErrorViewFactory {

  BasicActivity basicActivity;
  public DefaultErrorViewFactory(BasicActivity basicActivity) {
    this.basicActivity = basicActivity;
  }

  @Override
  public View getErrorView(String hint, String hintSub, int img) {
    View view = LayoutInflater.from(basicActivity).inflate(R.layout.layout_default_error, null);
    if (img != 0) {
      ImageView imageView = (ImageView) view.findViewById(R.id.error_img);
      imageView.setImageResource(img);
    }
    if(hint!=null) {
      TextView textView = (TextView) view.findViewById(R.id.error_hint);
      textView.setText(hint);
    }
    view.findViewById(R.id.refresh_reload).setOnClickListener(basicActivity);
    return view;
  }
}
