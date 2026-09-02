package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.module.SharedPreferencesModule
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
                }
                Scroller {
                    attr {
                        size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    }
                    Refresh {
                        attr {
                            refreshEnable = true
                        }
                        event {
                            refreshStateDidChange { state ->
                                if (state == RefreshViewState.REFRESHING) {
                                    ctx.refreshing = true
                                    val networkModule = ctx.acquireModule<NetworkModule>(NetworkModule.MODULE_NAME)
                                    networkModule.requestGet(
                                        "https://api.example.com/chat/list",
                                        JSONObject()
                                    ) { response, success, msg ->
                                        if (success) {
                                            val newList = ctx.parseChatList(response.optString("data", ""))
                                            ctx.chatList.clear()
                                            ctx.chatList.addAll(newList)
                                            ctx.refreshing = false
                                            val sp = ctx.acquireModule<SharedPreferencesModule>(SharedPreferencesModule.MODULE_NAME)
                                            sp.setObject("chatListCache", JSONObject().apply {
                                                put("data", response.optString("data", ""))
                                                put("timestamp", System.currentTimeMillis())
                                            })
                                        } else {
                                            ctx.refreshing = false
                                        }
                                    }
                                }
                            }
                        }
                        List {
                            attr {
                                flex(1f)
                            }
                            vforLazy({ ctx.chatList }) { item: ChatItemData, index: Int, _: Int ->
                                View {
                                    attr {
                                        flexDirectionRow()
                                        alignItemsCenter()
                                        padding(12f, 10f, 12f, 10f)
                                    }
                                    Image {
                                        attr {
                                            size(48f, 48f)
                                            borderRadius(24f)
                                            src(item.avatar)
                                        }
                                    }
                                    View {
                                        attr {
                                            flex(1f)
                                            marginLeft(12f)
                                        }
                                        Text {
                                            attr {
                                                text(item.nickname)
                                                fontWeightBold()
                                                fontSize(16f)
                                            }
                                        }
                                        Text {
                                            attr {
                                                text(item.lastMessage)
                                                color(Color(0xFF999999))
                                                fontSize(14f)
                                                lines(1)
                                                marginTop(4f)
                                            }
                                        }
                                    }
                                    Text {
                                        attr {
                                            text(item.time)
                                            color(Color(0xFF999999))
                                            fontSize(12f)
                                            textAlignEnd()
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

    private fun parseChatList(data: String): MutableList<ChatItemData> {
        return mutableListOf(
            ChatItemData("https://example.com/avatar1.png", "张三", "你好，在吗？", "12:30"),
            ChatItemData("https://example.com/avatar2.png", "李四", "明天见", "12:25"),
            ChatItemData("https://example.com/avatar3.png", "王五", "好的，收到", "12:20")
        )
    }
}