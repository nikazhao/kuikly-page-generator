package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
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

object ChatListPageStyle {
    val backgroundColor = Color(0xFFF5F5F5)
    val cardBackground = Color(0xFFFFFFFF)
    val primaryText = Color(0xFF1A1A1A)
    val secondaryText = Color(0xFF666666)
    val accentColor = Color(0xFF007AFF)
    val dividerColor = Color(0xFFE0E0E0)
    
    val avatarSize = 48f
    val iconSize = 24f
    val cardCornerRadius = 12f
    val avatarCornerRadius = 24f
    
    val spacingSmall = 8f
    val spacingMedium = 12f
    val spacingLarge = 16f
    val spacingExtraLarge = 24f
}

data class ChatItem(
    val userId: String,
    val avatar: String,
    val nickname: String,
    val lastMessage: String,
    val lastTime: String
)

@Page("ChatListPage")
internal class ChatListPage : Pager() {

    private var chatList by observableList<ChatItem>()
    private var isLoadingMore by observable(false)
    private var isRefreshing by observable(false)
    private var pageNum by observable(1)
    private var hasMoreData by observable(true)

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
                        size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                        flex(1f)
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
                                    val newList = ctx.loadChatList(ctx.pageNum)
                                    ctx.chatList.clear()
                                    ctx.chatList.addAll(newList)
                                    ctx.isRefreshing = false
                                    ctx.hasMoreData = true
                                }
                            }
                        }
                        List {
                            attr {
                                size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                                flex(1f)
                            }
                            vforLazy({ ctx.chatList }) { item, _, _ ->
                                View {
                                    attr {
                                        size(pagerData.pageViewWidth, 72f)
                                        flexDirectionRow()
                                        alignItemsCenter()
                                        backgroundColor(ChatListPageStyle.cardBackground)
                                        padding(12f, 10f, 12f, 10f)
                                    }
                                    Image {
                                        attr {
                                            size(ChatListPageStyle.avatarSize, ChatListPageStyle.avatarSize)
                                            borderRadius(ChatListPageStyle.avatarCornerRadius)
                                            src(item.avatar)
                                        }
                                    }
                                    View {
                                        attr {
                                            flex(1f)
                                            flexDirectionColumn()
                                            marginLeft(ChatListPageStyle.spacingMedium)
                                            justifyContentCenter()
                                        }
                                        Text {
                                            attr {
                                                text(item.nickname)
                                                fontSize(16f)
                                                color(ChatListPageStyle.primaryText)
                                                fontWeightMedium()
                                                lines(1)
                                            }
                                        }
                                        Text {
                                            attr {
                                                text(item.lastMessage)
                                                fontSize(14f)
                                                color(ChatListPageStyle.secondaryText)
                                                lines(1)
                                                marginTop(ChatListPageStyle.spacingSmall)
                                            }
                                        }
                                    }
                                    Text {
                                        attr {
                                            text(item.lastTime)
                                            fontSize(12f)
                                            color(ChatListPageStyle.secondaryText)
                                            marginLeft(ChatListPageStyle.spacingLarge)
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

    private fun loadChatList(page: Int): MutableList<ChatItem> {
        return mutableListOf(
            ChatItem("1", "avatar1.png", "张三", "你好，今天天气不错", "10:30"),
            ChatItem("2", "avatar2.png", "李四", "收到，谢谢", "10:25"),
            ChatItem("3", "avatar3.png", "王五", "周末一起打球？", "昨天")
        )
    }
}