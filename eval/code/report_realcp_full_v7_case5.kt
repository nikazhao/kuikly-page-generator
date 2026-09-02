package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Refresh
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

data class ChatItem(
    val userId: String = "",
    val avatar: String = "",
    val nickname: String = "",
    val lastMessage: String = "",
    val time: String = ""
)

object ChatListPageStyle {
    val backgroundColor = Color(0xFFF5F5F5)
    val itemBackground = Color(0xFFFFFFFF)
    val primaryText = Color(0xFF1A1A1A)
    val secondaryText = Color(0xFF666666)
    val timeText = Color(0xFF999999)
    val badgeColor = Color(0xFFFF4444)
    val dividerColor = Color(0xFFEEEEEE)
    
    val avatarSize = 48f
    val avatarRadius = 24f
    val badgeSize = 20f
    val iconSize = 24f
    
    val pagePadding = 16f
    val itemPadding = 12f
    val avatarMarginRight = 12f
    val contentSpacing = 8f
    val badgeOffset = -4f
}

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
                    flex(1f)
                    backgroundColor(ChatListPageStyle.backgroundColor)
                }
                Scroller {
                    attr {
                        flex(1f)
                        size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    }
                    Refresh {
                        attr {
                            refreshEnable = true
                        }
                        event {
                            refreshStateDidChange { state ->
                                if (state == RefreshViewState.REFRESHING) {
                                    ctx.pageNum = 1
                                    ctx.isRefreshing = true
                                    val newList = mutableListOf<ChatItem>()
                                    newList.add(ChatItem(
                                        avatar = "https://example.com/avatar1.png",
                                        nickname = "张三",
                                        lastMessage = "你好，最近怎么样？",
                                        time = "10:30"
                                    ))
                                    newList.add(ChatItem(
                                        avatar = "https://example.com/avatar2.png",
                                        nickname = "李四",
                                        lastMessage = "好的，明天见",
                                        time = "09:15"
                                    ))
                                    ctx.chatList.clear()
                                    ctx.chatList.addAll(newList)
                                    ctx.isRefreshing = false
                                }
                            }
                        }
                        List {
                            attr {
                                flex(1f)
                                size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                            }
                            vforLazy({ ctx.chatList }) { item: ChatItem, index: Int, _: Int ->
                                View {
                                    attr {
                                        flexDirectionRow()
                                        alignItemsCenter()
                                        padding(ChatListPageStyle.itemPadding)
                                        backgroundColor(ChatListPageStyle.itemBackground)
                                    }
                                    event {
                                        click {
                                            val router = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                            router?.openPage("ChatDetailPage", JSONObject().apply {
                                                put("userId", item.userId)
                                                put("nickname", item.nickname)
                                            })
                                        }
                                    }
                                    View {
                                        attr {
                                            size(ChatListPageStyle.avatarSize, ChatListPageStyle.avatarSize)
                                            borderRadius(ChatListPageStyle.avatarRadius)
                                            backgroundColor(Color(0xFFE0E0E0))
                                            marginRight(ChatListPageStyle.avatarMarginRight)
                                        }
                                    }
                                    View {
                                        attr {
                                            flex(1f)
                                        }
                                        Text {
                                            attr {
                                                text(item.nickname)
                                                fontSize(16f)
                                                color(ChatListPageStyle.primaryText)
                                                fontWeightMedium()
                                            }
                                        }
                                        Text {
                                            attr {
                                                text(item.lastMessage)
                                                fontSize(14f)
                                                color(ChatListPageStyle.secondaryText)
                                                lines(1)
                                                marginTop(4f)
                                            }
                                        }
                                    }
                                    Text {
                                        attr {
                                            text(item.time)
                                            fontSize(12f)
                                            color(ChatListPageStyle.timeText)
                                        }
                                    }
                                }
                            }
                            event {
                                onLoadMore {
                                    if (ctx.hasMore && !ctx.isRefreshing) {
                                        ctx.pageNum += 1
                                        val moreList = mutableListOf<ChatItem>()
                                        moreList.addAll(ctx.chatList)
                                        moreList.add(ChatItem(
                                            avatar = "https://example.com/avatar3.png",
                                            nickname = "王五",
                                            lastMessage = "收到，谢谢",
                                            time = "昨天"
                                        ))
                                        ctx.chatList.clear()
                                        ctx.chatList.addAll(moreList)
                                        if (ctx.pageNum >= 3) {
                                            ctx.hasMore = false
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