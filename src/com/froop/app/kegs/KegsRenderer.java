package com.froop.app.kegs;

import android.graphics.Bitmap;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLUtils;
import android.opengl.GLSurfaceView;

class KegsRenderer implements GLSurfaceView.Renderer {
  private Bitmap mBitmap;
  private static final int mTexWidth = 1024;
  private static final int mTexHeight = 512;

  // Buffer holding the vertices
  final FloatBuffer mVertexBuffer;

  // Buffer holding the indices
  final ShortBuffer mIndexBuffer;

  // Buffer holding the texture mapping coordinates
  final FloatBuffer mTextureBuffer;

  // Vertices for drawing rect.
  static final float mVertices[] = {
    0.0f, 0.0f, 0.0f,  // 0, Top Left
    0.0f, 1.0f, 0.0f,  // 1, Bottom Left
    1.0f, 1.0f, 0.0f,  // 2, Bottom Right
    1.0f, 0.0f, 0.0f,  // 3, Top Right
  };

  // Mapping coordinates for the texture
  static final float mTextCoords[] = {
    0.0f, 1.0f,
    0.0f, 0.0f,
    1.0f, 0.0f,
    1.0f, 1.0f
  };

  // Connecting order (draws a square)
  static final short[] mIndices = { 0, 1, 2, 0, 2, 3 };

  private int mWidth = 0;
  private int mHeight = 0;
  private float mScaleX = 1.0f;
  private float mScaleY = 1.0f;
  private float mCropBorder = 0.0f;
  private boolean mScaled = false;
  private boolean mSizeChange = false;

  private int mTexId;
  private int mTexId2;
  private int mTexLast = 0;
  private boolean mTexIdOK = false;

  public KegsRenderer(Bitmap bitmap) {
    mBitmap = bitmap;

    // First, build the vertex, texture and index buffers
    ByteBuffer vbb = ByteBuffer.allocateDirect(mVertices.length * 4); // Float => 4 bytes
    vbb.order(ByteOrder.nativeOrder());
    mVertexBuffer = vbb.asFloatBuffer();
    mVertexBuffer.put(mVertices);
    mVertexBuffer.position(0);

    ByteBuffer ibb = ByteBuffer.allocateDirect(mIndices.length * 2); // Short => 2 bytes
    ibb.order(ByteOrder.nativeOrder());
    mIndexBuffer = ibb.asShortBuffer();
    mIndexBuffer.put(mIndices);
    mIndexBuffer.position(0);

    ByteBuffer tbb = ByteBuffer.allocateDirect(mTextCoords.length * 4);
    tbb.order(ByteOrder.nativeOrder());
    mTextureBuffer = tbb.asFloatBuffer();
    mTextureBuffer.put(mTextCoords);
    mTextureBuffer.position(0);
  }

  private void setupOrtho(GL10 gl) {
    gl.glMatrixMode(GL10.GL_PROJECTION);
    gl.glLoadIdentity();
    // 50.0f is 512-(400+32+30)  (the distance from the bottom of the texture to our actual bitmap)
    float cropBorder = 50.0f;
    float fudge = 0.0f;
    if (mCropBorder > 0.0f) {
      cropBorder += mCropBorder;
      fudge = 14.0f;  // Bug workaround to view the entire image better...
    }
    gl.glOrthof(0.0f, (float)mWidth, cropBorder, mHeight + cropBorder + fudge, 0.0f, 1.0f);
    gl.glMatrixMode(GL10.GL_MODELVIEW);
    gl.glLoadIdentity();
  }

  public void onSurfaceChanged(GL10 gl, int width, int height) {
    gl.glViewport(0, 0, width, height);
  }

  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    setupOrtho(gl);

//    gl.glShadeModel(GL10.GL_FLAT);
    gl.glEnable(gl.GL_TEXTURE_2D);
    checkGlError(gl, "surfaceCreatedAx1");

    gl.glDisable(GL10.GL_DITHER);
    // Later added:
    gl.glDisable(GL10.GL_LIGHTING);
    gl.glDisable(GL10.GL_DEPTH_TEST);
    gl.glDisable(GL10.GL_BLEND);
    gl.glDisable(GL10.GL_STENCIL_TEST);

    checkGlError(gl, "surfaceCreatedA0");

    Bitmap blank = Bitmap.createBitmap(mTexWidth, mTexHeight, Bitmap.Config.RGB_565);

    int[] textures = new int[2];
    gl.glGenTextures(2, textures, 0);
    checkGlError(gl, "surfaceCreatedGen");
    mTexId = textures[0];  // FIXME
    mTexId2 = textures[1];
    mTexLast = 1;
    mTexIdOK = true;

    int[] crop = new int [4];
    crop[0] = 0;
    crop[1] = mTexHeight;
    crop[2] = mTexWidth;
    crop[3] = -mTexHeight;
    for(int i=0; i < 2; i++) {
      gl.glBindTexture(gl.GL_TEXTURE_2D, textures[i]);
//      gl.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);

      gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
      gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

      gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
      gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

      gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

      GLUtils.texImage2D(gl.GL_TEXTURE_2D, 0, blank, 0);
    }
    blank.recycle();
    checkGlError(gl, "surfaceCreatedB");
  }

  private FpsCounter fpsCount = new FpsCounter("kegs", "draw");

  public void onDrawFrame(GL10 gl) {
    if (!mTexIdOK) {
      return;
    }
    fpsCount.fps();

    if (mSizeChange) {
      mSizeChange = false;
      setupOrtho(gl);
    }

    Bitmap bitmap = mBitmap;
    int format = GLUtils.getInternalFormat(bitmap);
    int type = GLUtils.getType(bitmap);
    // This was just to play around with multiple textures on the GPU,
    // it serves no benefit.
    int texId = (mTexLast == mTexId) ? mTexId2 : mTexId;
    mTexLast = texId;
    gl.glBindTexture(gl.GL_TEXTURE_2D, texId);
    GLUtils.texSubImage2D(gl.GL_TEXTURE_2D, 0, 0, 0, bitmap, format, type);
    checkGlError(gl, "texSubImage2D");

//    gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
//                GL10.GL_MODULATE);

    gl.glClearColor(0.643f, 0.776f, 0.223f, 1.0f);  // Green color for testing.
    gl.glClear(gl.GL_COLOR_BUFFER_BIT);

    gl.glLoadIdentity();
    gl.glFrontFace(GL10.GL_CCW);
    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
    gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
    gl.glScalef(mTexWidth, mTexHeight, 0.0f);
    if (mScaled) {
      // Additional scale on top of earlier scale.
      gl.glScalef(mScaleX, mScaleY, 0.0f);
    }
    gl.glDrawElements(GL10.GL_TRIANGLES, mIndices.length, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
    gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

    checkGlError(gl, "drawFrame");
  }

  private void checkGlError(GL10 gl, String op) {
    int error;
    while ((error = gl.glGetError()) != GL10.GL_NO_ERROR) {
      Log.e("kegsgl", op + ": glError " + error);
      throw new RuntimeException(op + ": glError " + error);
    }
  }

  public void updateScreenSize(BitmapSize bitmapSize) {
    // There should probably be a lock surrounding updating these.
    mWidth = bitmapSize.getViewWidth();
    mHeight = bitmapSize.getViewHeight();
    mScaleX = bitmapSize.getScaleX();
    mScaleY = bitmapSize.getScaleY();
    mCropBorder = (float)bitmapSize.getCropPixelCount();
    mScaled = bitmapSize.isScaled();
    mSizeChange = true;
    Log.e("kegs", "screen size " + mWidth + "x" + mHeight + " " + mScaleX + ":" + mScaleY + " crop=" + mCropBorder);
  }
}
