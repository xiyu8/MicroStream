package com.jason.microstream.ui.base.error;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jason.microstream.R;
import com.jason.microstream.ui.base.BasicActivity;

public class VCEmptyFactory implements EmptyViewFactory {
  BasicActivity basicActivity;
  public VCEmptyFactory(BasicActivity context) {
    this.basicActivity = context;
  }

  @Override
  public View getEmptyView(String hint,String hintSub, int img) {
    View view = LayoutInflater.from(basicActivity).inflate(R.layout.layout_default_empty, null);
    if (img != 0) {
      ImageView imageView = (ImageView) view.findViewById(R.id.empty_img);
      imageView.setImageResource(img);
    }
    if(hint!=null){
      TextView textView = (TextView) view.findViewById(R.id.empty_hint);
      textView.setText(hint);
    }
    if(hintSub!=null){
      TextView textView = (TextView) view.findViewById(R.id.empty_hint_sub);
      textView.setText(hintSub);
    }
    return view;
  }

  @Override
  public View getErrorView(String hint,String hintSub, int img) {
    View view = LayoutInflater.from(basicActivity).inflate(R.layout.layout_default_error, null);
    if (img != 0) {
      ImageView imageView = (ImageView) view.findViewById(R.id.error_img);
      imageView.setImageResource(img);
    }
    if(hint!=null) {
      TextView textView = (TextView) view.findViewById(R.id.error_hint);
      textView.setText(hint);
    }
    view.findViewById(R.id.error_hint).setOnClickListener(basicActivity);
    return view;
  }

  public View getConfListEmptyView(EmptyType emptyType) {
    switch (emptyType) {
      case CONF_LIST:
        View view = getEmptyView("暂无待开会议", "", R.drawable.video_list_empty3);
        return view;
      default:
        break;
    }

    View view = getEmptyView("暂无待开会议", "让会议不受空间限制", R.drawable.video_list_empty3);
    return view;
  }

  public View getConfListErrorView(ErrorType emptyType) {
    View view = getEmptyView("在线会议", "让会议不受空间限制", R.drawable.video_list_empty);
    return view;
  }

  public enum EmptyType{
    CONF_LIST(1);
    int value;

    EmptyType(int value) {
      this.value = value;
    }
  }
  public enum ErrorType{
    CONF_LIST(1);
    int value;

    ErrorType(int value) {
      this.value = value;
    }
  }

}
