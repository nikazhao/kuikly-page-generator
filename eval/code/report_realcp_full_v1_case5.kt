package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Refresh
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*

data class ChatItemData(
    val avatar: String,
    val nickname: String,
    val lastMessage: String,
    val time: String
)

@Page("ChatListPage")
internal class ChatListPage : Pager() {

    private var chatListData by observableList<ChatItemData>()
    private var isRefreshing by observable(false)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Refresh {
                    attr {
                        size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                        refreshing(ctx.isRefreshing)
                    }
                    List {
                        attr {
                            size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                        }
                        vforLazy({ ctx.chatListData }) { item: ChatItemData, index: Int, count: Int ->
                            View {
                                attr {
                                    size(pagerData.pageViewWidth, 72f)
                                    flexDirectionRow()
                                    alignItemsCenter()
                                    padding(12f)
                                }
                                Image {
                                    attr {
                                        size(48f, 48f)
                                        borderRadius(24f)
                                        src(ImageUri.network(item.avatar))
                                    }
                                }
                                View {
                                    attr {
                                        flex(1f)
                                        flexDirectionColumn()
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
                                        }
                                    }
                                }
                                Text {
                                    attr {
                                        text(item.time)
                                        fontSize(12f)
                                        color(Color(0xFFCCCCCC))
                                        textAlignRight()
                                    }
                                }
                            }
                        }
                    }
                    event {
                        onRefresh {
                            ctx.isRefreshing = true
                            val newData = mutableListOf<ChatItemData>()
                            newData.add(ChatItemData(
                                avatar = "https://example.com/avatar1.png",
                                nickname = "用户A",
                                lastMessage = "这是最新消息",
                                time = "刚刚"
                            ))
                            newData.addAll(ctx.chatListData)
                            ctx.chatListData.clear()
                            ctx.chatListData.addAll(newData)
                            ctx.isRefreshing = false
                        }
                    }
                }
            }
        }
    }
}