package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Refresh
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

data class ChatItemData(
    val avatar: String,
    val nickname: String,
    val lastMessage: String,
    val time: String
)

@Page("ChatListPage")
internal class ChatListPage : Pager() {

    private var chatList by observableList<ChatItemData>()
    private var refreshing by observable(false)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flex(1f)
                    backgroundColor = Color(0xFFF5F5F5)
                }
                Scroller {
                    attr {
                        flex(1f)
                    }
                    Refresh {
                        attr {
                            refreshEnable = true
                            color = Color(0xFF07C160)
                        }
                        event {
                            refreshStateDidChange { params ->
                                val state = params?.getString("state") ?: ""
                                if (state == "start") {
                                    ctx.refreshing = true
                                    val networkModule = ctx.acquireModule<NetworkModule>(NetworkModule.MODULE_NAME)
                                    networkModule?.requestGet(
                                        "https://api.example.com/chat/list",
                                        JSONObject(),
                                        { response, success, msg ->
                                            if (success) {
                                                val newList = ctx.parseChatListData(response.toString())
                                                ctx.chatList = newList.toMutableList()
                                            }
                                            ctx.refreshing = false
                                        }
                                    )
                                }
                            }
                        }
                        List {
                            attr {
                                flex(1f)
                            }
                            vfor({ ctx.chatList }) { item ->
                                View {
                                    attr {
                                        flexDirectionRow()
                                        alignItemsCenter()
                                        padding(12f)
                                    }
                                    Image {
                                        attr {
                                            size(48f, 48f)
                                            borderRadius(24f)
                                            src(item.avatar)
                                            backgroundColor = Color(0xFFCCCCCC)
                                        }
                                    }
                                    View {
                                        attr {
                                            flex(1f)
                                            marginLeft(12f)
                                            justifyContentCenter()
                                        }
                                        Text {
                                            attr {
                                                text(item.nickname)
                                                fontSize(16f)
                                                fontWeightBold()
                                                lines(1)
                                                color(Color(0xFF333333))
                                            }
                                        }
                                        Text {
                                            attr {
                                                text(item.lastMessage)
                                                fontSize(14f)
                                                color(Color(0xFF999999))
                                                lines(1)
                                                marginTop(4f)
                                            }
                                        }
                                    }
                                    Text {
                                        attr {
                                            text(item.time)
                                            fontSize(12f)
                                            color(Color(0xFFBBBBBB))
                                            marginLeft(8f)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun parseChatListData(data: String): List<ChatItemData> {
        return listOf(
            ChatItemData("avatar1.png", "张三", "你好，最近怎么样？", "12:30"),
            ChatItemData("avatar2.png", "李四", "明天开会别忘了", "11:45"),
            ChatItemData("avatar3.png", "王五", "收到，谢谢！", "20:20")
        )
    }
}