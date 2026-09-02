package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.module.RouterModule
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

object ChatListPageStyle {
    val backgroundColor = Color(0xFFF5F5F5L)
    val cardBackground = Color(0xFFFFFFFFL)
    val primaryText = Color(0xFF1A1A1AL)
    val secondaryText = Color(0xFF666666L)
    val accentColor = Color(0xFF007AFFL)
    val dividerColor = Color(0xFFE0E0E0L)
    
    val avatarSize = 48f
    val iconSize = 24f
    val listItemHeight = 72f
    
    val spacingSmall = 8f
    val spacingMedium = 12f
    val spacingLarge = 16f
    val spacingExtraLarge = 24f
    val spacingHuge = 40f
    
    val borderRadiusSmall = 8f
    val borderRadiusMedium = 12f
    val borderRadiusLarge = 16f
    
    val titleFontSize = 16f
    val subtitleFontSize = 14f
    val captionFontSize = 12f
}

data class ChatItem(
    val userId: String = "",
    val avatar: String = "",
    val nickname: String = "",
    val lastMessage: String = "",
    val time: String = ""
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
                    backgroundColor(ChatListPageStyle.backgroundColor)
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
                                    newList.add(ChatItem(avatar = "url1", nickname = "张三", lastMessage = "你好", time = "10:30"))
                                    newList.add(ChatItem(avatar = "url2", nickname = "李四", lastMessage = "在吗？", time = "10:25"))
                                    ctx.chatList.clear()
                                    ctx.chatList.addAll(newList)
                                    ctx.isRefreshing = false
                                }
                            }
                        }
                        List {
                            attr {
                                size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                            }
                            vforLazy({ ctx.chatList }) { item: ChatItem, index: Int, _: Int ->
                                View {
                                    attr {
                                        width(pagerData.pageViewWidth)
                                        flexDirection(FlexDirection.ROW)
                                        padding(12f, 10f, 12f, 10f)
                                        backgroundColor(Color.WHITE)
                                    }
                                    event {
                                        click {
                                            if (index < ctx.chatList.size) {
                                                val chatItem = ctx.chatList[index]
                                                val router = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                                router.openPage("ChatDetailPage", JSONObject().apply {
                                                    put("userId", chatItem.userId)
                                                    put("nickname", chatItem.nickname)
                                                })
                                            }
                                        }
                                    }
                                    Image {
                                        attr {
                                            size(ChatListPageStyle.avatarSize, ChatListPageStyle.avatarSize)
                                            borderRadius(ChatListPageStyle.avatarSize / 2f)
                                            src(item.avatar)
                                        }
                                    }
                                    View {
                                        attr {
                                            flex(1f)
                                            marginLeft(12f)
                                            flexDirection(FlexDirection.COLUMN)
                                        }
                                        View {
                                            attr {
                                                flexDirection(FlexDirection.ROW)
                                                alignItemsCenter()
                                            }
                                            Text {
                                                attr {
                                                    flex(1f)
                                                    text(item.nickname)
                                                    fontSize(ChatListPageStyle.titleFontSize)
                                                    fontWeightMedium()
                                                    color(ChatListPageStyle.primaryText)
                                                }
                                            }
                                            Text {
                                                attr {
                                                    text(item.time)
                                                    fontSize(ChatListPageStyle.captionFontSize)
                                                    color(ChatListPageStyle.secondaryText)
                                                }
                                            }
                                        }
                                        Text {
                                            attr {
                                                marginTop(4f)
                                                text(item.lastMessage)
                                                fontSize(ChatListPageStyle.subtitleFontSize)
                                                color(ChatListPageStyle.secondaryText)
                                                lines(1)
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
}