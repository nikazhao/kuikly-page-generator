package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.RefreshViewState
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*

data class ChatItem(
    var avatar: String = "",
    var nickname: String = "",
    var lastMessage: String = "",
    var time: String = ""
)

@Page("ChatListPage")
internal class ChatListPage : Pager() {

    private var chatList by observableList<ChatItem>()
    private var isRefreshing by observable(false)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flex(1f)
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Scroller {
                    Refresh {
                        attr {
                            refreshEnable = true
                        }
                        List {
                            attr {
                                flex(1f)
                            }
                            vforLazy({ ctx.chatList }) { item: ChatItem, index: Int, count: Int ->
                                View {
                                    attr {
                                        flexDirectionRow()
                                        alignItemsCenter()
                                        padding(16f, 12f, 16f, 12f)
                                        backgroundColor(Color(0xFFFFFFFF))
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
                                                fontSize(16f)
                                                fontWeightMedium()
                                                color(Color(0xFF333333))
                                            }
                                        }
                                        Text {
                                            attr {
                                                text(item.lastMessage)
                                                fontSize(14f)
                                                color(Color(0xFF999999))
                                                marginTop(4f)
                                                lines(1)
                                            }
                                        }
                                    }
                                    Text {
                                        attr {
                                            text(item.time)
                                            fontSize(12f)
                                            color(Color(0xFFCCCCCC))
                                            marginLeft(8f)
                                        }
                                    }
                                }
                            }
                        }
                        refreshStateDidChange { state ->
                            if (state == RefreshViewState.REFRESHING) {
                                ctx.isRefreshing = true
                                val newList = mutableListOf<ChatItem>()
                                newList.add(ChatItem().apply {
                                    avatar = "https://example.com/avatar1.png"
                                    nickname = "张三"
                                    lastMessage = "你好，最近怎么样？"
                                    time = "10:30"
                                })
                                newList.add(ChatItem().apply {
                                    avatar = "https://example.com/avatar2.png"
                                    nickname = "李四"
                                    lastMessage = "周末一起吃饭吗？"
                                    time = "10:25"
                                })
                                ctx.chatList.clear()
                                ctx.chatList.addAll(newList)
                                ctx.isRefreshing = false
                            }
                        }
                    }
                }
            }
        }
    }
}