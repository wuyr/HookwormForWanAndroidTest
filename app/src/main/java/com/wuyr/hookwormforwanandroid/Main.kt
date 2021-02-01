package com.wuyr.hookwormforwanandroid

import android.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.wuyr.hookworm.core.Hookworm
import com.wuyr.hookworm.extensions.findViewByIDName
import com.wuyr.hookworm.extensions.setOnClickProxy
import com.wuyr.hookworm.utils.get
import com.wuyr.hookworm.utils.set

/**
 * @author wuyr
 * @github https://github.com/wuyr/
 * @since 2021-01-25 上午10:54
 */
object Main {

    private fun Any?.logD() = Log.d("Main", toString())

    @JvmStatic
    fun main(processName: String) {
        Hookworm.transferClassLoader = true
        val mainActivity = "per.goweii.wanandroid.module.main.activity.MainActivity"
        // 拦截mainActivity的布局加载
        Hookworm.registerPostInflateListener(mainActivity) { _, resourceName, rootView ->
            rootView?.apply {
                if (resourceName == "banner") {
                    hookBanner(resourceName)
                }
                hookArticleItem(resourceName)
                removeTabs(resourceName)
            }
        }
    }

    private fun View.removeTabs(resourceName: String) {
        // 查找ll_bb，监听其子View的添加
        findViewByIDName<ViewGroup>("ll_bb")?.setOnHierarchyChangeListener(
            object : ViewGroup.OnHierarchyChangeListener {
                override fun onChildViewAdded(parent: View, child: View) {
                    // 转成ViewGroup
                    (parent as ViewGroup).run {
                        // 当子View数量大于2时移除最后一个
                        if (childCount > 2) {
                            removeViewAt(2)
                        }
                    }
                }

                override fun onChildViewRemoved(parent: View?, child: View?) {
                }
            })
        // 查找id名为“vp_tab”的ViewPager
        findViewByIDName<ViewPager>("vp_tab")?.let { viewPager ->
            // 监听Adapter变更
            viewPager.addOnAdapterChangeListener { _, _, newAdapter ->
                viewPager.post {
                    newAdapter?.let { adapter ->
                        // 取出Adapter变量mPages
                        adapter::class.get<Array<*>>(adapter, "mPages")?.let { pages ->
                            // 通过反射创建长度为2的数组
                            val newPages = java.lang.reflect.Array.newInstance(
                                Class.forName("per.goweii.basic.core.adapter.TabFragmentPagerAdapter\$Page"),
                                2
                            ) as Array<Any?>
                            // 只取前面2个元素
                            newPages[0] = pages[0]
                            newPages[1] = pages[1]
                            // 重新赋值
                            adapter::class.set(adapter, "mPages", newPages)
                        }
                        // 通知Adapter数据变更
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun View.hookArticleItem(resourceName: String) {
        // 根据id名“rv” 找到首页文章列表RecyclerView实例
        findViewByIDName<RecyclerView>("rv")?.let { recyclerView ->
            // 监听Item的attach状态
            recyclerView.addOnChildAttachStateChangeListener(object :
                RecyclerView.OnChildAttachStateChangeListener {

                private val dialog = AlertDialog.Builder(context).setMessage("禁止点击！").create()

                private val onClickProxy: (view: View, oldListener: View.OnClickListener?) -> Unit =
                    { view, oldListener ->
                        // 查找id名为“tv_title”的TextView
                        view.findViewByIDName<TextView>("tv_title")?.let { titleView ->
                            // 检查是否包含 “每日一问” 字眼
                            if (titleView.text.toString().contains("每日一问")) {
                                // 有则交给宿主处理
                                oldListener?.onClick(view)
                            } else {
                                // 没有就弹出dialog
                                dialog.show()
                            }
                        } ?: oldListener?.onClick(view) // 没找到，交给宿主去处理
                    }

                override fun onChildViewAttachedToWindow(child: View) {
                    // 在Item每次attach之后重新设置点击代理
                    child.setOnClickProxy(onClickProxy)
                }

                override fun onChildViewDetachedFromWindow(child: View) {
                }
            })
        }
    }

    private fun View.hookBanner(resourceName: String) {
        // 根据id名称: "bannerViewPager" 查找ViewPager
        findViewByIDName<ViewPager>("bannerViewPager")?.let { viewPager ->
            viewPager.addOnAdapterChangeListener(object : ViewPager.OnAdapterChangeListener {

                private val adapter = ImageAdapter(context)

                override fun onAdapterChanged(
                    viewPager: ViewPager, oldAdapter: PagerAdapter?, newAdapter: PagerAdapter?
                ) {
                    viewPager.removeOnAdapterChangeListener(this)
                    viewPager.adapter = adapter
                    viewPager.addOnAdapterChangeListener(this)
                }
            })
        }
    }
}