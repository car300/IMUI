package com.gengqiquan.demo

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.tencent.imsdk.*
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import com.gengqiquan.imlib.uitls.JsonUtil
import com.tencent.imsdk.TIMCallBack
import com.tencent.imsdk.TIMManager
import com.gengqiquan.imui.help.IMHelp
import com.gengqiquan.imui.help.LongPressHelp
import com.gengqiquan.imui.input.ButtonFactory
import com.gengqiquan.imlib.RealMsg
import com.gengqiquan.imlib.TIMViewFactory
import com.gengqiquan.imlib.audio.TIMAudioRecorder
import com.gengqiquan.imlib.TIMMsgBuilder
import com.gengqiquan.imlib.model.CustomElem
import com.gengqiquan.imlib.video.CameraActivity
import com.gengqiquan.imlib.video.listener.MediaCallBack
import com.gengqiquan.imui.help.ToastHelp
import com.gengqiquan.imui.interfaces.*
import com.gengqiquan.imui.model.MenuAction
import com.gengqiquan.permission.QQPermission
import com.gengqiquan.qqresult.QQResult
import com.tencent.imsdk.TIMMessage
import com.tencent.imsdk.ext.message.TIMConversationExt
import com.tencent.imsdk.ext.message.TIMMessageExt
import com.tencent.imsdk.ext.message.TIMUserConfigMsgExt
import com.xhe.photoalbum.PhotoAlbum
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val command = arrayOf("chmod", "777", cacheDir.absolutePath)
        var builder = ProcessBuilder(*command)
        try {
            builder.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val command2 = arrayOf("chmod", "777", cacheDir.toString())
        builder = ProcessBuilder(*command2)
        try {
            builder.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }


//        val list = arrayListOf<RealMsg>()
//        list.add(RealMsg(TXMsg("你好", "", TXMsg.Type.TEXT), true))
//        list.add(RealMsg(TXMsg("你好防水防汗首付开上飞洒放松放松,你好防水防汗首付开上飞洒放松放松", "", TXMsg.Type.TEXT), false))
//        list.add(
//            RealMsg(
//                TXMsg(
//                    "",
//                    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1556532526983&di=c6d64102aaa1d612e260eb8176366b93&imgtype=0&src=http%3A%2F%2Fs11.sinaimg.cn%2Fmw690%2F006hikKrzy7slvzPwSKba%26690",
//                    TXMsg.Type.IMG
//                )
//                , true
//            )
//        )
//        list.add(
//            RealMsg(
//                TXMsg(
//                    "",
//                    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1556532526982&di=1f14a4ba8e5ad52d27b99299fa3b6803&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201512%2F12%2F20151212193107_ujGZV.jpeg",
//                    TXMsg.Type.IMG
//                ), false
//            )
//        )
//        list.add(RealMsg(TXMsg("", "", TXMsg.Type.VIDEO), true))
//        list.add(RealMsg(TXMsg("", "", TXMsg.Type.VIDEO), false))
        IMHelp.init(this, TIMAudioRecorder(), TIMMsgBuilder(), object : ImImageDisplayer {
            override fun display(url: String, imageView: ImageView, displayListener: DisplayListener?) {
                Glide.with(imageView.context)
                    .load(url)
                    .asBitmap()
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                            imageView.setImageBitmap(resource)
                            displayListener?.ready()
                        }
                    })
            }
        })
        IMHelp.addImViewFactory(TIMViewFactory())
//        im_ui.appendMsgs(list.toMutableList())
        TIMManager.getInstance().init(
            applicationContext, TIMSdkConfig(1400205051)
                .enableLogPrint(true)
//                .enableCrashReport(false)
                .setLogLevel(TIMLogLevel.DEBUG)
                .setLogPath(Environment.getExternalStorageDirectory().getPath() + "/justfortest/")
        )
        val userConfig = TIMUserConfig().setGroupEventListener { }
            .setUserStatusListener(object : TIMUserStatusListener {
                override fun onUserSigExpired() {

                }

                override fun onForceOffline() {
                }
            })
            .setConnectionListener(object : TIMConnListener {
                override fun onConnected() {

                }

                override fun onWifiNeedAuth(p0: String?) {
                }

                override fun onDisconnected(p0: Int, p1: String?) {
                }
            })
                .setGroupEventListener { }
        userConfig.refreshListener = object : TIMRefreshListener {
            override fun onRefreshConversation(msgs: MutableList<TIMConversation>) {

            }

            override fun onRefresh() {
            }
        }
        userConfig.setUploadProgressListener { timMessage, _, _, progress ->
            Log.d(tag, "进度" + "===" + timMessage.status().status + "==" + +progress)
        }
        TIMManager.getInstance().userConfig = TIMUserConfigMsgExt(userConfig)
            .setMessageRevokedListener {
                im_ui.refresh()
            }
        TIMManager.getInstance().addMessageListener(object : TIMMessageListener {
            override fun onNewMessages(msgs: MutableList<TIMMessage>): Boolean {
                Log.d(tag, "新消息" + msgs.size)
                val list = mutableListOf<IimMsg>()
                msgs.forEach {
                    list.addAll(RealMsg.create(it))
                }
                list.reverse()
                im_ui.newMsgs(list)
                return true
            }
        })
        TIMManager.getInstance().login(indent, sig, object : TIMCallBack {
            override fun onError(code: Int, desc: String) {
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                Log.d(tag, "login failed. code: $code errmsg: $desc")

            }

            override fun onSuccess() {
                Log.d(tag, "getLocalMessage login succ")
                afterLogin()

            }
        })


    }

    fun afterLogin() {
        val con = TIMManager.getInstance().getConversation(
            TIMConversationType.C2C,    //会话类型：单聊
            tel
        )
        val conversationExt = TIMConversationExt(con)
        val count = 10
        fun getMessage(list: MutableList<TIMMessage>, finish: (List<TIMMessage>) -> Unit) {
            conversationExt.getMessage(count, lastMsg, object : TIMValueCallBack<List<TIMMessage>> {
                override fun onSuccess(msgs: List<TIMMessage>) {
                    Log.e(tag, "获取漫游消息" + msgs.size.toString())
                    if (msgs.isEmpty()) {
                        finish(list)
                        return
                    }
                    lastMsg = msgs.last()
                    list.addAll(msgs.filter { it.status() != TIMMessageStatus.HasDeleted })
                    if (list.size >= count || msgs.size < count) {
                        finish(list)
                        return
                    }
                    getMessage(list, finish)
                }

                override fun onError(p0: Int, p1: String?) {
                    Log.e(tag, "获取漫游消息失败" + p0.toString() + ":" + p1)
                }
            })
        }

        fun loadMore(init: Boolean = false) {
            getMessage(mutableListOf()) {
                if (it.isNotEmpty()) {
                    if (it.size < count) {
                        im_ui.allInit()
                    }

                    val list = mutableListOf<IimMsg>()
                    it.forEach {
                        list.addAll(RealMsg.create(it))
                    }
                    list.reverse()
                    im_ui.oldMsgs(list, init)
                }else{
                    im_ui.allInit()
                }
            }
//            conversationExt.getMessage(count, lastMsg, object : TIMValueCallBack<List<TIMMessage>> {
//                override fun onSuccess(msgs: List<TIMMessage>) {
//                    Log.e(tag, "获取漫游消息" + msgs.size.toString())
//
//                }
//
//                override fun onError(p0: Int, p1: String?) {
//                    Log.e(tag, "获取漫游消息失败" + p0.toString() + ":" + p1)
//                }
//            })
        }
        loadMore(true)
        im_ui.setMoreOldMsgListener(object : IMoreOldMsgListener {
            override fun more() {
                loadMore()
            }
        })
        IMHelp.registerMsgSender(this, object : IMsgSender {

            override fun send(msg: Any, repeat: Boolean, senderListener: ISenderListener?) {
                var timMessage = msg as TIMMessage
                if (repeat) {
                    TIMMessageExt(timMessage).remove()
                    im_ui.delete(RealMsg.decorate(timMessage))
                    val newMsg = TIMMessage()
                    newMsg.copyFrom(timMessage)
                    timMessage = newMsg
                }
                val realMsg = RealMsg.create(timMessage)
                im_ui.newMsgs(realMsg)
                senderListener?.sending()
//                im_ui.newMsgs(RealMsg.create(msg as TIMMessage))
                con.sendMessage(msg, object : TIMValueCallBack<TIMMessage> {
                    override fun onSuccess(msg: TIMMessage) {
                        Log.e(tag, "onSuccess" + msg.toString())
                        senderListener?.success()
//                        im_ui.updateMsgs(RealMsg.create(msg))
//                        realMsg.forEach { it.success() }
                        im_ui.updateMsgs(realMsg)

                    }

                    override fun onError(p0: Int, p1: String?) {
                        Log.e(tag, "onError" + p0.toString() + ":" + p1)
                        senderListener?.failure()
                        realMsg.forEach { TIMMessageExt(it.realData()).customInt = 3 }
                        im_ui.updateMsgs(realMsg)
                    }
                })
            }
        })

        im_ui.inputUI.otherProxy(object : OtherProxy {
            override fun proxy(type: Int, send: (Any) -> Unit) {
                if (type == ButtonFactory.PICTURE) {
                    QQPermission.with(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .silence()
                            .requestPermissions {
                                QQResult.startActivityWith(
                                        this@MainActivity,
                                        PhotoAlbum(this@MainActivity).setLimitCount(4).albumIntent
                                )
                                        .result {
                                            IMHelp.getMsgBuildPolicy()
                                                    .buildImgMessageList(PhotoAlbum.parseResult(it))
                                                    .forEach(send)
                                        }
                            }
                    return
                }
                if (type == ButtonFactory.CAMERA) {
                    QQPermission.with(this@MainActivity, Manifest.permission.CAMERA
                            , Manifest.permission.RECORD_AUDIO)
                            .silence()
                            .requestPermissions {
                                val captureIntent = Intent(this@MainActivity, CameraActivity::class.java)
                                CameraActivity.mCallBack = object : MediaCallBack {
                                    override fun onImageSuccess(path: String) {
                                        send(IMHelp.getMsgBuildPolicy().buildImgMessage(path))
                                    }

                                    override fun onVideoSuccess(videoData: Intent) {
                                        val imgPath = videoData.getStringExtra("image_path")
                                        val videoPath = videoData.getStringExtra("video_path")
                                        val imgWidth = videoData.getIntExtra("width", 0)
                                        val imgHeight = videoData.getIntExtra("height", 0)
                                        val duration = videoData.getLongExtra("duration", 0)
                                        val msg =
                                                IMHelp.getMsgBuildPolicy()
                                                        .buildVideoMessage(imgPath, videoPath, imgWidth, imgHeight, duration)
                                        send(msg)
                                    }
                                }
                                startActivity(captureIntent)
                            }
                    return
                }
                if (type == ButtonFactory.CAR) {
                    val ele = CustomElem.create(json)
                    Log.d(tag, ele.toString())
                    Log.d(tag, JsonUtil.toJson(ele))
                    val msg =
                        IMHelp.getMsgBuildPolicy()
                            .buildCustomMessage(JsonUtil.toJson(ele))
                    send(msg)
                    return
                }
                if (type == ButtonFactory.CARD) {
                    val ele = CustomElem.create(json)
                    Log.d(tag, ele.toString())
                    Log.d(tag, JsonUtil.toJson(ele))
                    im_ui.newMsgs(RealMsg.create(TIMMsgBuilder.buildPreCustomMessage(JsonUtil.toJson(ele))))
                    return
                }
            }

        })
        im_ui.inputUI.audioListener(object :IAudioListener{
            override fun audioClick(f: () -> Unit) {
                QQPermission.with(this@MainActivity, Manifest.permission.RECORD_AUDIO)
                        .silence()
                        .requestPermissions {
                            f()
                        }
            }
        })
        var list = mutableListOf<MenuAction>()
        list.add(MenuAction("撤回", {
            Log.e(tag, "调用撤回")
            conversationExt.revokeMessage(it as TIMMessage, object : TIMCallBack {
                override fun onSuccess() {
                    // TODO: 2019-05-07 撤回成功
                    Log.e(tag, "撤回成功")
                    im_ui.refresh()
                }

                override fun onError(p0: Int, p1: String?) {
                    ToastHelp.toastShortMessage("只能撤回2分钟以内的消息")
                    Log.e(tag, "撤回失败" + p0.toString() + ":" + p1)
                }
            })
        }, true))
        list.add(MenuAction("删除") {
            if (!TIMMessageExt(it as TIMMessage).remove()) {
                Log.e(tag, "删除失败")
                return@MenuAction
            }
//            lastMsg = null
//            loadMore()
            im_ui.delete(RealMsg.decorate(it))
            Log.e(tag, "删除成功")
        })
        LongPressHelp.init(list)

    }

    val json = "{\n" +
            "    \"type\":\"share\",\n" +
            "    \"type_desc\":\"个人名片\",\n" +
            "    \"platform\":{\n" +
            "        \"from\":\"che300_pro\",\n" +
            "        \"os\":\"android\",\n" +
            "        \"version\":\"2.2.4.0\",\n" +
            "        \"device_info\":\"(OPPO A83,Android 7.1.1)\",\n" +
            "        \"sender_user_id\":\"fjjkwerwiuafsjfkjf\",\n" +
            "        \"receive_user_id\":\"wtwouafjklfaksjf\",\n" +
            "        \"latitude\":\"\",\n" +
            "        \"longitude\":\"\"\n" +
            "    },\n" +
            "    \"data\":{\n" +
            "        \"router_url\":\"che300://open/webv/http://m.che300.com\",\n" +
            "        \"action_type\":\"1\",\n" +
            "        \"style\":0,\n" +
            "        \"msg\":{\n" +
            "            \"title\":\"2013款 朗逸 改款 1.4TFSI 手动豪华版 5周年纪念款\",\n" +
            "            \"content\":\"公司：南京市大锤二手车经营管理中心\",\n" +
            "            \"module\":\"模块来源\",\n" +
            "            \"pic_url\":\"http://img.wxcha.com/file/201603/07/7ec4c7c1f7.jpg\"\n" +
            "        }\n" +
            "    }\n" +
            "}"
    var lastMsg: TIMMessage? = null
    val tag = "immmmmm"
    val tel = "129a14e8a4b3a123"
    val indent = "8849cc559d324811"
    val sig = "eJxlj8FOg0AURfd8BWFbYx8Dr4BJFwQqYlubVrTuCJmZ1oFCYRixavx3I2rEeLfn5N7cN03XdSNZ3J5nlB6fKpWql5ob*oVugHH2C*tasDRTqSXZP8hPtZA8zXaKyx6aiEgAho5gvFJiJ74N17U9ShE9ZhHbNc2B2bIi7ee*qmwAAgj4RxH7Hi5n6yCOHqNmfGhCENvlZp6wURzchZe5HZ1urLJwMuq59QzC632Q*MIP*f3qtelUTmKFDwtn4rvVerVpRyWd5EUxp2x7kFdy3OHzdDqYVKLkP988Ao5n4YB2XLbiWPUCARNNYsFnDO1d*wDI4V5d"
}
