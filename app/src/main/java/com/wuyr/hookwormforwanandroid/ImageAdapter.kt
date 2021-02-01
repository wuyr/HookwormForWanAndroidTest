package com.wuyr.hookwormforwanandroid

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide

/**
 * @author wuyr
 * @github https://github.com/wuyr/HookwormForWanAndroidTest
 * @since 2021-01-25 下午2:28
 */
class ImageAdapter(context: Context) : PagerAdapter() {

    private val imageUrls = arrayOf(
        "https://c-ssl.duitang.com/uploads/item/201708/13/20170813095305_FSQhj.thumb.700_0.jpeg",
        "https://c-ssl.duitang.com/uploads/item/201512/05/20151205212633_nFx3d.thumb.700_0.jpeg",
        "https://c-ssl.duitang.com/uploads/item/201606/12/20160612235102_z3dja.thumb.700_0.jpeg",
        "https://c-ssl.duitang.com/uploads/item/201707/27/20170727121828_Z5TRA.thumb.700_0.png",
        "https://c-ssl.duitang.com/uploads/item/201707/27/20170727122213_3HBaN.thumb.700_0.png",
        "https://c-ssl.duitang.com/uploads/item/201512/04/20151204202153_nEUMt.thumb.700_0.jpeg"
    )

    private val imageViews = ArrayList<ImageView>().apply {
        imageUrls.forEach { url ->
            add(ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                Glide.with(context).load(url).into(this)
            })
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int) =
        imageViews[position].also { container.addView(it) }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) =
        container.removeView(imageViews[position])

    override fun getCount() = imageUrls.size

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`
}