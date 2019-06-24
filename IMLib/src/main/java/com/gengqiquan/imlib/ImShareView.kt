package com.gengqiquan.imlib

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.*
import com.gengqiquan.imlib.model.CustomElem
import com.gengqiquan.imlib.model.CustomType
import com.gengqiquan.imlib.model.ShareElem
import com.gengqiquan.imui.help.IMHelp
import com.gengqiquan.imui.interfaces.IheaderListener
import com.gengqiquan.imui.interfaces.IimMsg
import com.gengqiquan.imui.ui.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class ImShareView(context: Context) : RealImView(context) {
    var ll_content: LinearLayout? = null
    var tv_title: TextView? = null
    var tv_desc: TextView? = null
    var tv_from: TextView? = null
    var iv_img: ImageView? = null
    var v_line: View? = null
    var tv_content:TextView? = null
    var fl_content:RelativeLayout? = null

    override fun floatBaseView() = ll_content!!
    override fun createItemView(contentView: RelativeLayout): View {
        fl_content = RelativeLayout(context).apply {
            layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent)
            ll_content = linearLayout().apply {
                orientation = VERTICAL
                backgroundResource = R.drawable.im_share_msg_back
                horizontalPadding = dip(13)
                topPadding = dip(13)
                layoutParams = RelativeLayout.LayoutParams(matchParent, wrapContent)
                tv_title = textView {
                    textColor = Color.BLACK
                    textSize = 15f
                    includeFontPadding = false
                    maxLines = 2

                }
                linearLayout {
                    layoutParams = LinearLayout.LayoutParams(matchParent, wrapContent).apply {
                        topMargin = dip(10)
                    }

                    tv_desc = textView {
                        textColor = 0xff999999.toInt()
                        textSize = 12f
                        includeFontPadding = false
                        maxLines = 3
                        layoutParams = LinearLayout.LayoutParams(0, wrapContent).apply {
                            weight = 1f
                        }
                    }
                    iv_img = imageView {
                        layoutParams = LinearLayout.LayoutParams(dip(45), dip(45)).apply {
                            gravity = Gravity.RIGHT xor Gravity.BOTTOM
                            bottomMargin = dip(13)
                        }
                        scaleType = ImageView.ScaleType.FIT_XY
                    }

                }
                v_line = view {
                    backgroundColor = 0xfff0f0f0.toInt()
                    layoutParams = LinearLayout.LayoutParams(matchParent, dip(0.5f))
                }
                tv_from = textView {
                    textColor = 0xff999999.toInt()
                    textSize = 11f
                    gravity = Gravity.CENTER_VERTICAL
                    includeFontPadding = false
                    layoutParams = LinearLayout.LayoutParams(matchParent, dip(25))
                }
            }

            tv_content = textView().apply {
                textColor = Color.BLACK
                textSize = 18f
                includeFontPadding = false
                layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent).apply {
                    alignParentLeft()
                }
                backgroundResource = R.drawable.im_text
            }
        }

        return fl_content!!
    }


    override fun decoratorItemView(item: IimMsg) {
        val elem = item.extra() as CustomElem
        when (elem.type) {
            CustomType.share -> {
                ll_content?.show()
                tv_content?.gone()
                val data = elem.data as ShareElem
                tv_title?.text = data.msg.title
                tv_desc?.text = data.msg.content
                tv_from?.isShow(!TextUtils.isEmpty(data.msg.module))
                v_line?.isShow(!TextUtils.isEmpty(data.msg.module))
                tv_from?.text = data.msg.module
                IMHelp.getImageDisplayer().display(data.msg.pic_url, iv_img!!)
            }
            else ->  {
                ll_content?.gone()
                tv_content?.show()
                MsgHelp.shareOption?.decoratorItemView(elem,tv_content)
            }
        }

        ll_content?.singleClick {
            MsgHelp.shareOption?.click(elem)
        }
    }


}