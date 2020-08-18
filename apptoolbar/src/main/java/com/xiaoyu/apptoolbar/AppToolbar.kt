package com.xiaoyu.apptoolbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

/**
 * XiaoYu
 * 2020/8/18 00:15
 * 标题可以在中间的TooBar
 */
class AppToolbar : Toolbar {

    companion object {
        private const val TITLE_VIEW_FILED_NAME = "mTitleTextView"
        private const val SUB_TITLE_VIEW_FILED_NAME = "mSubtitleTextView"
    }

    var isReLayoutTitle = true
        set(value) {
            field = value
            invalidate()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) :
            this(context, attrs, androidx.appcompat.R.attr.toolbarStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        context.obtainStyledAttributes(attrs, R.styleable.AppToolbar, defStyleAttr, 0).apply {
            isReLayoutTitle = getBoolean(R.styleable.AppToolbar_isReLayoutTitle, isReLayoutTitle)
        }.recycle()
    }

    private var mTitleTextView: TextView? = null
    private var mSubTitleTextView: TextView? = null

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (!isReLayoutTitle) return
        val parentMidWidth = measuredWidth / 2
        val parentMidHeight = measuredHeight / 2
        val titleTextView = getTitleTextView()
        val subTitleView = getSubTitleView()
        layoutTitleView(titleTextView, subTitleView, parentMidWidth, parentMidHeight)
        layoutSubTitleView(titleTextView, subTitleView, parentMidWidth, parentMidHeight)
    }

    /**
     * 对TitleView重新布局
     */
    private fun layoutTitleView(
        titleTextView: TextView?,
        subTitleView: TextView?,
        parentMidWidth: Int,
        parentMidHeight: Int
    ) {
        if (titleTextView == null) return
        val left = parentMidWidth - titleTextView.measuredWidth / 2
        //如果subTitleView!=null,
        // top 需要向上平移 subTitleView 高度的一半，
        // 用来给subTitleView展示
        val top =
            parentMidHeight - titleTextView.measuredHeight / 2 -
                    (if (subTitleView == null) 0 else subTitleView.measuredHeight / 2)
        val right = left + titleTextView.measuredWidth
        val bottom = top + titleTextView.measuredHeight
        titleTextView.layout(left, top, right, bottom)
    }

    /**
     * 对SubTitleView重新布局
     */
    private fun layoutSubTitleView(
        titleTextView: TextView?,
        subTitleView: TextView?,
        parentMidWidth: Int,
        parentMidHeight: Int
    ) {
        if (subTitleView == null) return
        val left = parentMidWidth - subTitleView.measuredWidth / 2
        //如果titleView!=null
        // 则top 为 titleView的bottom,
        // 否则subTitleView 就在中间
        val top = titleTextView?.bottom ?: parentMidHeight - subTitleView.measuredHeight / 2
        val right = left + subTitleView.measuredWidth
        val bottom = top + subTitleView.measuredHeight
        subTitleView.layout(left, top, right, bottom)
    }

    private fun getTitleTextView(): TextView? {
        if (mTitleTextView == null) {
            mTitleTextView = getSuperView(TITLE_VIEW_FILED_NAME)
        }
        return mTitleTextView
    }

    private fun getSubTitleView(): TextView? {
        if (mSubTitleTextView == null) {
            mSubTitleTextView = getSuperView(SUB_TITLE_VIEW_FILED_NAME)
        }
        return mSubTitleTextView
    }

    @Suppress("UNCHECKED_CAST")
    private fun <V : View> getSuperView(viewName: String): V? {
        val superclass = this::class.java.superclass ?: return null
        val titleViewMethod = superclass.getDeclaredField(viewName)
        titleViewMethod.isAccessible = true
        return try {
            titleViewMethod.get(this) as V
        } catch (e: Exception) {
            null
        }
    }
}