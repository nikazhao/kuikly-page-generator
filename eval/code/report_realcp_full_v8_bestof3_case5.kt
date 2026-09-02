package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.RefreshViewState
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.Refresh
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

@Page("ChatListPage")
internal class ChatListPage : Pager() {

    private var chatList by observableList<ChatItem>()
    private var isRefreshing by observable(false)
    private var pageSize by observable(20)
    private var currentPage by observable(1)
    private var hasMoreData by observable(true)

    data class ChatItem(
        val userId: String,
        val avatar: String,
        val nickname: String,
        val lastMessage: String,
        val lastTime: String,
        val unreadCount: Int = 0
    )

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    backgroundColor(Color(0xFFF5F5F5L))
                }
                Scroller {
                    attr {
                        size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                        flexDirection(FlexDirection.COLUMN)
                    }
                    Refresh {
                        attr {
                            refreshEnable = true
                        }
                        event {
                            refreshStateDidChange { state ->
                                if (state == RefreshViewState.REFRESHING) {
                                    ctx.isRefreshing = true
                                    ctx.currentPage = 1
                                    ctx.hasMoreData = true
                                    ctx.chatList.clear()
                                    ctx.chatList.addAll(ctx.fetchChatList(1, ctx.pageSize))
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
                                        width(pagerData.pageViewWidth)
                                        flexDirection(FlexDirection.ROW)
                                        padding(12f, 10f, 12f, 10f)
                                        alignItemsCenter()
                                    }
                                    event {
                                        click {
                                            val router = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                            router.openPage("ChatDetailPage", JSONObject().apply {
                                                put("userId", item.userId)
                                                put("nickname", item.nickname)
                                            })
                                        }
                                        longPress {
                                            ctx.chatList.remove(item)
                                        }
                                    }
                                    Image {
                                        attr {
                                            size(48f, 48f)
                                            borderRadius(24f)
                                            src(item.avatar)
                                            marginRight(12f)
                                        }
                                    }
                                    View {
                                        attr {
                                            flex(1f)
                                            flexDirection(FlexDirection.COLUMN)
                                            justifyContentCenter()
                                        }
                                        Text {
                                            attr {
                                                text(item.nickname)
                                                fontSize(16f)
                                                fontWeightBold()
                                                color(Color(0xFF333333L))
                                                lines(1)
                                            }
                                        }
                                        Text {
                                            attr {
                                                text(item.lastMessage)
                                                fontSize(14f)
                                                color(Color(0xFF666666L))
                                                lines(1)
                                                marginTop(4f)
                                            }
                                        }
                                        Text {
                                            attr {
                                                text(item.lastTime)
                                                fontSize(12f)
                                                color(Color(0xFF999999L))
                                                lines(1)
                                                marginTop(2f)
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

    private fun fetchChatList(page: Int, size: Int): List<ChatItem> {
        return listOf(
            ChatItem("1", "avatar1.png", "张三", "你好，明天见", "10:30"),
            ChatItem("2", "avatar2.png", "李四", "收到，谢谢", "10:25"),
            ChatItem("3", "avatar3.png", "王五", "周末一起打球？", "昨天")
        )
    }
}