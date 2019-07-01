package com.gengqiquan.imui.input

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gengqiquan.imui.model.emoji.Emoji
import com.gengqiquan.imui.model.emoji.EmojiManager
import com.gengqiquan.imui.interfaces.ISelectListener
import com.gengqiquan.imui.ui.singleClick
import org.jetbrains.anko.bottomPadding
import org.jetbrains.anko.dip
import org.jetbrains.anko.padding
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.sdk27.coroutines.onClick


class ImEmojiIconView(context: Context) : LinearLayout(context) {
    class Holder(view: ImageView) : RecyclerView.ViewHolder(view)

    private val uiAdapter by lazy {
        object : RecyclerView.Adapter<Holder>() {

            val w = (context.resources.displayMetrics.widthPixels / 8f + .5f).toInt()

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
                val iv_emoji = ImageView(context).apply {
                    layoutParams = LayoutParams(w, dip(50))
                    padding = dip(7)
                }
                return Holder(iv_emoji)
            }

            override fun getItemCount() = data.size

            override fun onBindViewHolder(holder: Holder, position: Int) {
                val iv_emoji = holder.itemView as ImageView
                iv_emoji.setImageBitmap(data[position].icon)
                iv_emoji.onClick {
                    selectedListener?.select(data[position].filter)
                }
            }
        }
    }

    var selectedListener: ISelectListener? = null
    private var data: List<Emoji> = EmojiManager.getEmojiList()
    val listUI: RecyclerView
    var spanCount = 3

    val gridLayoutManager = GridLayoutManager(context, spanCount, HORIZONTAL, false)

    init {
        listUI = recyclerView {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = gridLayoutManager
            adapter = uiAdapter
            bottomPadding = dip(15)
        }
    }


}