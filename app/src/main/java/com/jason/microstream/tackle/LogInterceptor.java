package com.jason.microstream.tackle;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class LogInterceptor implements Interceptor {

  private final String TAG = "OKHTTP";
  private static  boolean showLog;
  private long requestTime ;

  public LogInterceptor(boolean showLog) {
    this.showLog = showLog;
  }
  public  static  void setShowLog(boolean showLog) {
    LogInterceptor.showLog = showLog;
  }
  @Override
  public Response intercept(Chain chain) throws IOException {
    if (!showLog) return chain.proceed(chain.request());

    logRequest(chain.request());

    return logResponse(chain.proceed(chain.request()));
  }

  private Response logResponse(Response response) {
    try {
      String logString="";
      logString = logString + "========response'log=======\n";
      Response.Builder builder = response.newBuilder();
      Response clone = builder.build();
      logString = logString + "url : " + clone.request().url()+"\n" +
              "code : " + clone.code()+"\n"+
              "protocol : " + clone.protocol()+"\n";
      if (!TextUtils.isEmpty(clone.message())) {
        logString = logString + "message : " + clone.message()+"\n";
      }

      ResponseBody body = clone.body();
      if (body != null) {
        MediaType mediaType = body.contentType();
        if (mediaType != null) {
          logString = logString + "responseBody's contentType : " + mediaType.toString()+"\n";
          if (isText(mediaType)) {
            String resp = body.string();
            logString = logString + "responseBody's content : " + resp+"\n";
            print(logString);

            body = ResponseBody.create(mediaType, resp);
            return response.newBuilder().body(body).build();
          } else {
            logString = logString + "responseBody's content : " + " maybe [file part] , too large too print , ignored!"+"\n";
          print(logString);
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return response;
  }

  private void logRequest(Request request) {
    try {
      String url = request.url().toString();
      Headers headers = request.headers();
      String logString="";
      requestTime= System.currentTimeMillis();
      logString += "========request'log=======\n";
      logString = logString + "method : " + request.method() +"\n";
      logString = logString + "url : " + url + "\n";
      if (headers != null && headers.size() > 0) {
        logString = logString + "headers : " + headers.toString() + "\n";
      }
      RequestBody requestBody = request.body();
      if (requestBody != null) {
        MediaType mediaType = requestBody.contentType();
        if (mediaType != null) {
          logString = logString + "requestBody's contentType : " + mediaType.toString() + "\n";
          if (isText(mediaType)) {
            logString = logString + "requestBody's content : " + bodyToString(request) + "\n";
          } else {
            logString = logString + "requestBody's content : " + " maybe [file part] , too large too print , ignored!" + "\n";
          }
        }
      }
    print(logString);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String bodyToString(final Request request) {
    try {
      final Request copy = request.newBuilder().build();
      final Buffer buffer = new Buffer();
      copy.body().writeTo(buffer);
      return buffer.readUtf8();
    } catch (final IOException e) {
      return "something error when show requestBody.";
    }
  }

  private boolean isText(MediaType mediaType) {
    if (mediaType.type() != null && mediaType.type().equals("text")) {
      return true;
    }
    if (mediaType.subtype() != null) {
      if (mediaType.subtype().equals("json") ||
              mediaType.subtype().equals("xml") ||
              mediaType.subtype().equals("html") ||
              mediaType.subtype().equals("webviewhtml")
      )
        return true;
    }
    return false;
  }

  private void print(String msg){
    if (msg == null || msg.length() == 0)
      return;

    int segmentSize = 3 * 1024;
    long length = msg.length();
    if (length <= segmentSize ) {// 长度小于等于限制直接打印
      if (msg.contains("E/OKHTTP:")){
        String s = msg.replaceAll("E/OKHTTP:", "");
        Log.e(TAG, s);
      }else{
        Log.e(TAG, msg);
      }
    }else {
      while (msg.length() > segmentSize ) {// 循环分段打印日志
        String logContent = msg.substring(0, segmentSize );
        msg = msg.replace(logContent, "");
        if (logContent.contains("E/OKHTTP:")){
          String s = logContent.replaceAll("E/OKHTTP:", "");
          Log.e(TAG, s);
        }else{
          Log.e(TAG, logContent);
        }
      }
      if (msg.contains("E/OKHTTP:")){
        String s = msg.replaceAll("E/OKHTTP:", "");
        Log.e(TAG, s);
      }else{
        Log.e(TAG, msg);
      }
    }
  }




  private String unicodeToString(String str) {

    Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");

    Matcher matcher = pattern.matcher(str);

    char ch;

    while (matcher.find()) {

      ch = (char) Integer.parseInt(matcher.group(2), 16);

      str = str.replace(matcher.group(1), ch + "");

    }

    return str;

  }
}
