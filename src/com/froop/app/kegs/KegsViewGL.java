package com.froop.app.kegs;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.opengl.GLSurfaceView;

class KegsViewGL extends GLSurfaceView implements KegsThread.UpdateScreen {
  // Reported area of this view, see updateScreenSize()
  private int mWidth = 0;
  private int mHeight = 0;

  private Bitmap mBitmap;
  private KegsRenderer mRenderer;

  public KegsViewGL(Context context, AttributeSet attrs) {
    super(context, attrs);

    Bitmap bitmap = Bitmap.createBitmap(1024, 512, // OpenGL FIXME
                                        Bitmap.Config.RGB_565);
    mBitmap = bitmap;

    mRenderer = new KegsRenderer(bitmap);
    setRenderer(mRenderer);
    setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    setFocusable(true);
    setFocusableInTouchMode(true);
    requestFocus();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    setMeasuredDimension(mWidth, mHeight);
  }

  @Override
  public InputConnection onCreateInputConnection(EditorInfo attrs) {
    // Bug workaround to force KEYCODE_DEL.
    return InputFix.getInputConnection(this, attrs);
  }

  public Bitmap getBitmap() {
    return mBitmap;
  }

  public void updateScreen() {
    requestRender();
  }

  public void updateScreenSize(BitmapSize bitmapSize) {
    mWidth = bitmapSize.getViewWidth();
    mHeight = bitmapSize.getViewHeight();
    requestLayout();
    mRenderer.updateScreenSize(bitmapSize);
  }
}
