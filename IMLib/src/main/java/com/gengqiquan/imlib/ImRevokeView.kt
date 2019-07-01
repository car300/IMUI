package com.gengqiquan.imlib

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.gengqiquan.imlib.model.CustomType
import com.gengqiquan.imlib.model.PreCustomElem
import com.gengqiquan.imui.R
import com.gengqiquan.imui.interfaces.IimMsg
import com.gengqiquan.imui.ui.ImView
import com.gengqiquan.imui.ui.isShow
import com.tencent.imsdk.TIMCustomElem
import com.tencent.imsdk.TIMElemType
import org.jetbrains.anko.*

class ImRevokeView(val context: Context) : ImView {
    override fun get(): View {

        return LinearLayout(context).apply {
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
            tv_content = textView {
                background = resources.getDrawable(R.drawable.im_audio_back)
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

        }
    }

    private var tv_content: TextView? = null
    private var tv_time: TextView? = null
    override fun decorator(item: IimMsg) {
        tv_time?.isShow(!item.time().isNullOrEmpty() && item.getTimeTag() == 1 )
        tv_time?.text = item.time() ?: ""
        val nickName = item.getNickName() ?: item.sender()
        val text = if (item.isSelf()) "你" else "\"${nickName}\""
        val elem = (item as RealMsg).elem
        if (elem.getType() == TIMElemType.Custom) {
            val customElem = elem as TIMCustomElem
            val preCustomElem = PreCustomElem.create(String(customElem.data))
            if (preCustomElem.type == CustomType.revoke){
                tv_content?.text = preCustomElem.type_desc
                return
            }
        }
        tv_content?.text = text + "撤回了一条消息"
    }


}