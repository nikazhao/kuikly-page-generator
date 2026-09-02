package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Refresh
import com.tencent.kuikly.core.views.RefreshViewState
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
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flex(1f)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Scroller {
                    attr {
                        size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                        flex(1f)
                    }
                    Refresh {
                        attr {
                            refreshEnable = true
                        }
                        event {
                            refreshStateDidChange { state ->
                                if (state == RefreshViewState.REFRESHING && !ctx.refreshing) {
                                    ctx.refreshing = true
                                    val networkModule = ctx.acquireModule<NetworkModule>(NetworkModule.MODULE_NAME)
                                    networkModule.requestGet(
                                        "https://api.example.com/chat/list",
                                        JSONObject(),
                                        { response, success, _ ->
                                            if (success) {
                                                val newList = ctx.parseChatList(response.toString())
                                                ctx.chatList.clear()
                                                ctx.chatList.addAll(newList)
                                            }
                                            ctx.refreshing = false
                                        }
                                    )
                                }
                            }
                        }
                        List {
                            attr {
                                size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                                flex(1f)
                            }
                            vforLazy({ ctx.chatList }) { item, index, _ ->
                                View {
                                    attr {
                                        size(pagerData.pageViewWidth, 72f)
                                        flexDirectionRow()
                                        alignItemsCenter()
                                        padding(12f, 8f, 12f, 8f)
                                        backgroundColor(Color.WHITE)
                                    }
                                    Image {
                                        attr {
                                            size(48f, 48f)
                                            borderRadius(24f)
                                            marginRight(12f)
                                            src(item.avatar)
                                        }
                                    }
                                    View {
                                        attr {
                                            flex(1f)
                                            flexDirectionColumn()
                                            justifyContentCenter()
                                        }
                                        Text {
                                            attr {
                                                text(item.nickname)
                                                fontSize(16f)
                                                fontWeightBold()
                                                color(Color(0xFF333333))
                                                lines(1)
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
                                            color(Color(0xFFCCCCCC))
                                            textAlignRight()
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

    private fun parseChatList(response: String): MutableList<ChatItemData> {
        return mutableListOf(
            ChatItemData("avatar1.png", "张三", "你好，最近怎么样？", "12:30"),
            ChatItemData("avatar2.png", "李四", "明天一起吃饭吗？", "11:45"),
            ChatItemData("avatar3.png", "王五", "收到，谢谢！", "11:20")
        )
    }
}