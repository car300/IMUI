package com.gengqiquan.imui.interfaces

import androidx.annotation.IntDef
import com.gengqiquan.imui.model.ImImage
import com.gengqiquan.imui.model.ImVideo
import java.util.*

open interface IimMsg {
    companion object {
        val SendWait = 0
        val Sending = 1
        val SendSucc = 2
        val SendFail = 3

        @IntDef(0, 1, 2, 3)
        annotation class SendType
    }

    fun uiType(): Int

    @SendType
    fun status(): Int
    fun sender(): Any
    fun text(): String
    fun time(): String?
    fun video(): ImVideo
    fun sound(listener:IPathListener): Unit
    fun img(): ImImage
    fun isSelf(): Boolean
    fun duration(): Long
    fun extra(): Any
    fun <T> realData(): T
    fun setTimeTag(timeTag:Int) : Boolean
    fun getTimeTag() : Int
    fun getTime() : Date
    fun identifier(): String
    fun setNickName(nickName:String): Boolean
    fun getNickName(): String
    fun setRevoke(isRevoke:Boolean): Boolean
    fun isRevoke(): Boolean
}
