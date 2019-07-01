package com.gengqiquan.imui.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.IdRes
import androidx.core.view.marginLeft
import com.gengqiquan.imui.R
import com.gengqiquan.imui.help.IMHelp
import com.gengqiquan.imui.help.LongPressHelp
import com.gengqiquan.imui.interfaces.IimMsg
import com.gengqiquan.imui.model.MenuAction
import org.jetbrains.anko.*

abstract class RealImView(val mContext: Context) : LinearLayout(mContext), ImView {

    private var tv_header: TextView? = null
    private var tv_time: TextView? = null
    private var ll_content:LinearLayout ?= null
    override fun get(): View {

        return this
    }

    @SuppressLint("ResourceType")
    @IdRes
    private val localId = 0xff8800

    init {
        orientation = LinearLayout.VERTICAL
        layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent).apply {
            topPadding = dip(15)
        }
        tv_time = textView {
            background = resources.getDrawable(R.drawable.im_time_back)
            textColor = Color.WHITE
            textSize = 12f
            gravity = Gravity.CENTER
            includeFontPadding = false
            leftPadding = dip(6)
            rightPadding = dip(6)
            layoutParams = LinearLayout.LayoutParams(wrapContent, dip(20)).apply {
                bottomMargin = dip(20)
                gravity = Gravity.CENTER_HORIZONTAL
            }

        }
        frameLayout {
            tv_header = textView {
                textColor = Color.WHITE
                textSize = 15f
                gravity = Gravity.CENTER
                background = resources.getDrawable(R.drawable.im_header_back)
                layoutParams = FrameLayout.LayoutParams(dip(41), dip(41)).apply {
                    leftMargin = dip(15)
                    rightMargin = dip(15)
                }
            }
            ll_content = linearLayout {
                horizontalPadding = dip(63)
                orientation = HORIZONTAL
                itemView = createItemView(this).apply {
                    id = localId
                }
                addView(itemView)
                iv_fail = imageView {
                    padding=dip(8)
                    imageResource = R.drawable.im_fail
                }
            }
        }
    }

    private var iv_fail: ImageView? = null
    private var itemView: View? = null
    abstract fun createItemView(contentView: LinearLayout): View

    override fun decorator(item: IimMsg) {
        tv_header?.layoutParams = (tv_header?.layoutParams as FrameLayout.LayoutParams).apply {
            gravity = if (item.isSelf()) Gravity.RIGHT else Gravity.LEFT
        }
        itemView?.layoutParams = LinearLayout.LayoutParams(wrapContent, wrapContent)/*.apply {
            if (item.isSelf()) alignParentRight() else alignParentLeft()
        }*/
        iv_fail?.layoutParams = LinearLayout.LayoutParams(dip(36), dip(36)).apply {
//            centerVertically()
//            if (item.isSelf()) leftOf(itemView!!) else rightOf(itemView!!)
        }
        ll_content?.removeAllViews()
        if (!item.isSelf()){
            ll_content?.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            ll_content?.addView(itemView)
            ll_content?.addView(iv_fail)
        }else{
            ll_content?.gravity = Gravity.END or Gravity.CENTER_VERTICAL
            ll_content?.addView(iv_fail)
            ll_content?.addView(itemView)
        }
        if (item.status() == 3){
            iv_fail?.show()
        }else{
            iv_fail?.hide()
        }
//        iv_fail?.isShow(item.status() == 3)

        iv_fail?.singleClick {
            IMHelp.getMsgSender(context)?.send(item.realData(), true)
        }

        tv_header?.singleClick {
            IMHelp.getHeaderListener()?.click(item.identifier())
        }

//        var name = item.sender().toString()
        var name = item.getNickName()
        if( name.isNullOrEmpty() )name = item.identifier()
        if ( name.length > 2 ) {
            name = name.substring(name.length - 2)
        }
        tv_header?.text = name
        tv_time?.isShow(!item.time().isNullOrEmpty() && item.getTimeTag() == 1 )
        tv_time?.text = item.time() ?: ""
        decoratorItemView(item)
        floatBaseView().setOnLongClickListener {

            LongPressHelp.showPopAction(
                context, item.realData(),
                getMenuAction(LongPressHelp.getActions().filter { !it.isOnlySelf || item.isSelf() } as MutableList<MenuAction>),
                rootView,
                floatBaseView()
            )
            false
        }
    }

    open fun getMenuAction(actions: MutableList<com.gengqiquan.imui.model.MenuAction>): MutableList<com.gengqiquan.imui.model.MenuAction> {
        return actions
    }

    //    fun dip(value: Int) =this.dip(value)
    abstract fun decoratorItemView(item: IimMsg)

    abstract fun floatBaseView(): View
}