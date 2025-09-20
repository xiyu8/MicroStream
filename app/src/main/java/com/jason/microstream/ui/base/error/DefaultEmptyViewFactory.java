package com.jason.microstream.ui.base.error;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jason.microstream.R;
import com.jason.microstream.ui.base.BasicActivity;

public class DefaultEmptyViewFactory implements EmptyViewFactory {

    BasicActivity vcActivity;
  public DefaultEmptyViewFactory(BasicActivity context) {
    this.vcActivity = context;
  }

  @Override
  public View getEmptyView(String hint,String hintSub, int img) {
    View view = LayoutInflater.from(vcActivity).inflate(R.layout.layout_default_error, null);
    if (img != 0) {
      ImageView imageView = (ImageView) view.findViewById(R.id.error_img);
      imageView.setImageResource(img);
    }
    if(hint!=null){
      TextView textView = (TextView) view.findViewById(R.id.error_hint);
      textView.setText(hint);
    }
    return view;
  }

  @Override
  public View getErrorView(String hint,String hintSub, int img) {
    View view = LayoutInflater.from(vcActivity).inflate(R.layout.layout_default_empty, null);
    if (img != 0) {
      ImageView imageView = view.findViewById(R.id.empty_img);
      imageView.setImageResource(img);
    }
    if(hint!=null) {
      TextView textView = view.findViewById(R.id.empty_hint);
      textView.setText(hint);
    }
    view.findViewById(R.id.empty_hint).setOnClickListener(vcActivity);
    return view;
  }
}
