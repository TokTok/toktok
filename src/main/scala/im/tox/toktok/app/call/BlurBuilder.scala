package im.tox.toktok.app.call

import android.content.Context
import android.graphics.{ Bitmap, Canvas }
import android.renderscript.{ Allocation, Element, RenderScript, ScriptIntrinsicBlur }
import android.view.View

object BlurBuilder {

  private val BITMAP_SCALE = 0.4f
  private val BLUR_RADIUS = 20.5f

  def blur(v: View): Bitmap = {
    blur(v.getContext, getScreenshot(v))
  }

  def blur(ctx: Context, image: Bitmap): Bitmap = {
    val width = Math.round(image.getWidth * BITMAP_SCALE)
    val height = Math.round(image.getHeight * BITMAP_SCALE)
    val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
    val outputBitmap = Bitmap.createBitmap(inputBitmap)
    val rs = RenderScript.create(ctx)
    val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
    val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
    theIntrinsic.setRadius(BLUR_RADIUS)
    theIntrinsic.setInput(tmpIn)
    theIntrinsic.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)
    outputBitmap
  }

  private def getScreenshot(v: View): Bitmap = {
    val screenshot = Bitmap.createBitmap(v.getWidth, v.getHeight, Bitmap.Config.ARGB_8888)
    val canvas = new Canvas(screenshot)
    v.draw(canvas)
    screenshot
  }

}
