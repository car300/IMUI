package com.gengqiquan.imui.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gengqiquan.imui.input.ImInputUI
import com.gengqiquan.imui.interfaces.IMoreOldMsgListener
import com.gengqiquan.imui.interfaces.IimMsg
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import java.util.*
import kotlin.math.absoluteValue

class IMUI(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    lateinit var symbolTime: Date
    lateinit var senderUserNickName: String
    var myNickName: String ?= null

    private val uiAdapter by lazy {
        object : RecyclerView.Adapter<ImHolder>() {
            override fun getItemViewType(position: Int): Int {
                if (allInit == 1 && position == 0) {
                    return DefaultIMViewFactory.MORE_REFRESH
                }
                return data[position - allInit].uiType()
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImHolder {
                return ImHolder.get(parent, viewType)
            }

            override fun getItemCount() = data.size + allInit

            override fun onBindViewHolder(holder: ImHolder, position: Int) {
                if (allInit == 1 && position == 0) {
                    return
                }

                var timMsg =  data[position - allInit]

                if( timMsg.getTimeTag() == 0 ){//如果没有处理过tag才进行处理,如果处理过了,不做处理,以免列表反向滑动出错
                    if( !::symbolTime.isInitialized ){
                        symbolTime = data[position - allInit].getTime()
                    }

                    var cCalendar  = Calendar.getInstance()
                    var sCalendar  = Calendar.getInstance()

                    cCalendar.time = timMsg.getTime()
                    sCalendar.time = symbolTime

                    if(( cCalendar.timeInMillis - sCalendar.timeInMillis ).absoluteValue <= 5*60*1000 && position != 0 ) {//5分钟内隐藏
                        data[position - allInit].setTimeTag(2)
                    } else {//超过5分钟展示
                        symbolTime = timMsg.getTime()
                        data[position - allInit].setTimeTag(1)
                    }

                    if (timMsg.isSelf()) {//设置姓名
                        timMsg.setNickName(myNickName ?: "我")
                    } else {
                        timMsg.setNickName(senderUserNickName ?: "")
                    }

                }

                holder.imView.decorator(data[position - allInit])
            }
        }
    }

    private var data: MutableList<IimMsg> = arrayListOf()
    fun oldMsgs(oldData: List<IimMsg>, init: Boolean = false) {
        if (init) {
            data.clear()
        }
        allInit = 1
        data.addAll(0, oldData)
        uiAdapter.notifyItemRangeInserted(0, oldData.size)
//        uiAdapter.notifyDataSetChanged()
        if (init) {
            scrollToNeed(oldData.size - 1)
        }
        mIsLoadMore = false
    }

    fun newMsgs(newData: List<IimMsg>) {
        allInit = 1
        val start = data.size
        data.addAll(newData)
//        uiAdapter.notifyItemRangeInserted(start+allInit, newData.size+allInit)
        uiAdapter.notifyDataSetChanged()
        scrollToNeed(data.size - 1)
    }

    fun updateMsgs(msgs: List<IimMsg>) {
        msgs.forEach {
            val index = data.indexOf(it)
            if (index >= 0) {
                uiAdapter.notifyItemChanged(index + allInit)
            }

        }

        listUI.scrollToPosition(uiAdapter.itemCount-1)

    }

    fun delete(any: Any) {
        val index = data.indexOf(any)
        if (index > -1) {
            data.removeAt(index)
            uiAdapter.notifyItemRemoved(index + allInit)
        }
    }

    fun refresh() {
        uiAdapter.notifyDataSetChanged()
        scrollToNeed(data.size - 1)
    }

    private fun scrollToNeed(position: Int) {
        linearLayoutManager.scrollToPosition(position + allInit)

    }

    val listUI: RecyclerView
    val inputUI: ImInputUI = ImInputUI(context)
    private val linearLayoutManager = LinearLayoutManager(context)

    init {
        orientation = VERTICAL
        listUI = recyclerView {
            layoutParams = LayoutParams(matchParent, 0).apply {
                weight = 1f
            }
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = linearLayoutManager
            adapter = uiAdapter
            bottomPadding = dip(15)
            onFocusChange { v, hasFocus ->
                if (!hasFocus) {
                    scrollToNeed(data.size - 1)
                }
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy < -1) {
                        inputUI.reset()
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    checkLoadMore(1)
                }
            })
        }
        uiAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                checkLoadMore(2)
            }
        })
        addView(inputUI)
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            var lastHeight = 0
            override fun onGlobalLayout() {
                var height = listUI.height

                if (Math.abs(lastHeight - height) > dip(50)) {
                    scrollToNeed(data.size - 1)
                }
                lastHeight = height

            }
        })
    }

    private fun checkLoadMore(index: Int) {
        if (!mIsLoadMore && allInit == 1) {
            if (isLastItemVisible(index)) {
                mIsLoadMore = true
                moreOldMsgListener?.more()
            }
        }
    }

    private fun isLastItemVisible(index: Int): Boolean {
        if (uiAdapter!!.itemCount == 0) {
            return true
        }
        val lastVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition()
        return lastVisiblePosition < index
    }

    private var mIsLoadMore = false
    private var moreOldMsgListener: IMoreOldMsgListener? = null
    fun setMoreOldMsgListener(listener: IMoreOldMsgListener) {
        moreOldMsgListener = listener
    }

    private var allInit = 0
    fun allInit() {
        allInit = 0
        mIsLoadMore = false
        uiAdapter.notifyDataSetChanged()
    }

    fun getData() : MutableList<IimMsg>{
        return data;
    }


}
