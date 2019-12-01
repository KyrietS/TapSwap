package mwo.lab.tapswap.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageSwitcher
import android.widget.Toast
import mwo.lab.tapswap.views.LoadingView
import java.lang.Exception


class ImageSwitcherPicasso(
    private val context: Context,
    private val imageSwitcher: ImageSwitcher,
    private val loading: LoadingView
) : Target {

    override fun onBitmapLoaded(bitmap: Bitmap, loadedFrom: Picasso.LoadedFrom) {
        imageSwitcher.setImageDrawable(BitmapDrawable(context.resources, bitmap))
        loading.finish()
    }

    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?)  {
        Toast.makeText(context, "onBitmapFailed", Toast.LENGTH_SHORT).show()
        Log.d("omg", e?.message)
        loading.finish()
    }

    override fun onPrepareLoad(drawable: Drawable?) {

    }

}