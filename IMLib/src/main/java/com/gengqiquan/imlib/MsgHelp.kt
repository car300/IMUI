package com.gengqiquan.imlib

import android.widget.TextView
import com.gengqiquan.imlib.model.CustomElem

/**
 * @author mazhichao
 * @date 2019/06/17 18:04
 */
object MsgHelp {

    interface ShareOption{
        fun click(elem:CustomElem)
        fun decoratorItemView(elem:CustomElem,tv:TextView?)
    }

    var shareOption:ShareOption ?= null
}