package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Refresh
import com.tencent.kuikly.core.views.RefreshViewState
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*

data class ChatItem(
    val avatar: String,
    val nickname: String,
    val lastMessage: String,
    val time: String
)

@Page("ChatListPage")
internal class ChatListPage : Pager() {

    private var chatList by observableList<ChatItem>()
    private var isRefreshing by observable(false)
    private var pageNum by observable(1)
    private var hasMore by observable(true)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    backgroundColor(Color(0xFFF5F5F5))
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
                                    ctx.isRefreshing = true
                                    ctx.pageNum = 1
                                    val newList = mutableListOf<ChatItem>()
                                    newList.add(ChatItem("avatar1.png", "张三", "你好，最近怎么样？", "10:30"))
                                    newList.add(ChatItem("avatar2.png", "李四", "明天一起吃饭吗？", "10:25"))
                                    newList.add(ChatItem("avatar3.png", "王五", "收到，谢谢！", "10:20"))
                                    ctx.chatList.clear()
                                    ctx.chatList.addAll(newList)
                                    ctx.isRefreshing = false
                                }
                            }
                        }
                        View {
                            attr {
                                flexDirection(FlexDirection.COLUMN)
                                width(pagerData.pageViewWidth)
                            }
                            vfor({ ctx.chatList }) { item ->
                                View {
                                    attr {
                                        flexDirection(FlexDirection.ROW)
                                        width(pagerData.pageViewWidth)
                                        padding(12f, 10f, 12f, 10f)
                                        backgroundColor(Color.WHITE)
                                        alignItemsCenter()
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
                                            flexDirection(FlexDirection.COLUMN)
                                            marginLeft(12f)
                                            justifyContentCenter()
                                        }
                                        Text {
                                            attr {
                                                text(item.nickname)
                                                fontSize(16f)
                                                color(Color(0xFF333333))
                                                fontWeightMedium()
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
                    }
                }
            }
        }
    }
}