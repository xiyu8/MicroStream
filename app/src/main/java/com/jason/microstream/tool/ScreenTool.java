package com.jason.microstream.tool;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class ScreenTool {

//  /**
//   * view截图
//   * @return
//   */
//  public static void viewShot( final View v,  final String filePath,
//                               final ShotCallback shotCallback){
//    if (null == v) {
//      return;
//    }
//    v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//      @Override
//      public void onGlobalLayout() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//          v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//        } else {
//          v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//        }
//        // 核心代码start
//        Bitmap bitmap = Bitmap.createBitmap(v.getWidth() , v.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(bitmap);
//        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
//        v.draw(c);
//        // end
//        String savePath = filePath;
//        if (TextUtils.isEmpty(savePath)){
//          savePath = createImagePath();
//        }
//        try {
//          compressAndGenImage(bitmap,savePath);
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//        if (null != shotCallback){
//          shotCallback.onShotComplete(bitmap,savePath);
//        }
//      }
//    });
//  }


  /**
   * 屏幕截图
   * @param activity
   * @return
   */
  public static Bitmap screenShot(Activity activity, String filePath) {
    if (activity == null){
      return null;
    }
    View view = activity.getWindow().getDecorView();
    //允许当前窗口保存缓存信息
    view.setDrawingCacheEnabled(true);
    view.buildDrawingCache();

    int navigationBarHeight =/* ScreenUtils.getNavigationBarHeight(view.getContext())*/0;


    //获取屏幕宽和高
    int width = /*ScreenUtils.getScreenWidth(view.getContext())*/view.getWidth();
    int height = /*ScreenUtils.getScreenHeight(view.getContext())*/view.getHeight();

    // 全屏不用考虑状态栏，有导航栏需要加上导航栏高度
    Bitmap bitmap = null;
    try {
      bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width,
              height + navigationBarHeight);
    } catch (Exception e) {
      // 这里主要是为了兼容异形屏做的处理，我这里的处理比较仓促，直接靠捕获异常处理
      // 其实vivo oppo等这些异形屏手机官网都有判断方法
      // 正确的做法应该是判断当前手机是否是异形屏，如果是就用下面的代码创建bitmap


      String msg = e.getMessage();
      // 部分手机导航栏高度不占窗口高度，不用添加，比如OppoR15这种异形屏
      if (msg.contains("<= bitmap.height()")){
        try {
          bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width,
                  height);
        } catch (Exception e1) {
          msg = e1.getMessage();
          // 适配Vivo X21异形屏，状态栏和导航栏都没有填充
          if (msg.contains("<= bitmap.height()")) {
            try {
              bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width,
                      height - /*ScreenUtils.getStatusHeight(view.getContext())*/view.getHeight());
            } catch (Exception e2) {
              e2.printStackTrace();
            }
          }else {
            e1.printStackTrace();
          }
        }
      }else {
        e.printStackTrace();
      }
    }

    //销毁缓存信息
    view.destroyDrawingCache();
    view.setDrawingCacheEnabled(false);

    if (null != bitmap){
      try {
        compressAndGenImage(bitmap,filePath);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return bitmap;
  }



  public interface ShotCallback{
    void onShotComplete(Bitmap bitmap,String savePath);
  }


  private static final String IMAGE_FILE_NAME_TEMPLATE = "Image%s.jpg";
  private static final String IMAGE_FILE_PATH_TEMPLATE = "%s/%s";

//  /**
//   * 存储到sdcard
//   *
//   * @param bmp
//   * @param maxSize 为0不压缩
//   * @return
//   */
//  public static String saveToSD(Bitmap bmp,int maxSize) {
//    if (bmp == null){
//      return "";
//    }
//    //判断sd卡是否存在
//    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//      //文件名
//      long systemTime = System.currentTimeMillis();
//      String imageDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(systemTime));
//      String mFileName = String.format(IMAGE_FILE_NAME_TEMPLATE, imageDate);
//
//      //文件全名
//      String mstrRootPath = FileUtil.getPackageDCIMPath(AFApplication.applicationContext);
//      String filePath = String.format(IMAGE_FILE_PATH_TEMPLATE, mstrRootPath, mFileName);
//
//      File file = new File(filePath);
//      if (!file.exists()) {
//        try {
//          file.createNewFile();
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//      }
//      try {
//        compressAndGenImage(bmp,filePath,maxSize);
//      } catch (FileNotFoundException e) {
//        e.printStackTrace();
//      } catch (IOException e) {
//        e.printStackTrace();
//      } finally {
//        bmp.recycle();
//      }
//
//      return filePath;
//    }
//    return "";
//  }

//  public static String createImagePath(){
//    //判断sd卡是否存在
//    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//      //文件名
//      long systemTime = System.currentTimeMillis();
//      String imageDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date(systemTime));
//      String mFileName = String.format(IMAGE_FILE_NAME_TEMPLATE, imageDate);
//
//      //文件全名
//      String mstrRootPath = FileUtil.getDCIMPath();
//      String filePath = String.format(IMAGE_FILE_PATH_TEMPLATE, mstrRootPath, mFileName);
//      File file = new File(filePath);
//      if (!file.exists()) {
//        try {
//          file.createNewFile();
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//      }
//      return filePath;
//    }
//    return "";
//  }

  public static void compressAndGenImage(Bitmap image, String outPath, int maxSize) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    // scale
    int options = 100;
    // Store the bitmap into output stream(no compress)
    image.compress(Bitmap.CompressFormat.JPEG, options, os);
    // Compress by loop
    if (maxSize != 0) {
      while (os.toByteArray().length / 1024 > maxSize) {
        // Clean up os
        os.reset();
        // interval 10
        options -= 10;
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
      }
    }

    // Generate compressed image file
    FileOutputStream fos = new FileOutputStream(outPath);
    fos.write(os.toByteArray());
    fos.flush();
    fos.close();
  }

  public static void compressAndGenImage(Bitmap image, String outPath) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    // scale
    int options = 70;
    // Store the bitmap into output stream(no compress)
    image.compress(Bitmap.CompressFormat.JPEG, options, os);

    // Generate compressed image file
    FileOutputStream fos = new FileOutputStream(outPath);
    fos.write(os.toByteArray());
    fos.flush();
    fos.close();
  }




}
