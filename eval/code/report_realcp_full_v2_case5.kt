package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.Refresh
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.*

data class ChatItem(
    val id: String,
    val nickname: String,
    val avatar: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int = 0
)

@Page("ChatListPage")
internal class ChatListPage : Pager() {

    private var chatList by observableList<ChatItem>()
    private var isRefreshing by observable(false)
    private var page by observable(1)
    private var hasMore by observable(true)

    private fun loadChatList(page: Int): List<ChatItem> {
        return listOf(
            ChatItem("1", "张三", "avatar1.png", "你好，最近怎么样？", "10:30", 2),
            ChatItem("2", "李四", "avatar2.png", "明天一起吃饭吗？", "09:15", 0),
            ChatItem("3", "王五", "avatar3.png", "项目进度怎么样了？", "昨天", 1),
            ChatItem("4", "赵六", "avatar4.png", "收到，谢谢！", "昨天", 0),
            ChatItem("5", "孙七", "avatar5.png", "周末有空吗？", "周三", 3)
        )
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flex(1f)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Scroller {
                    attr {
                        flex(1f)
                    }
                    Refresh {
                        attr {
                            refreshEnable = true
                        }
                        event {
                            refreshStateDidChange { state ->
                                if (state == RefreshViewState.REFRESHING) {
                                    ctx.page = 1
                                    ctx.hasMore = true
                                    ctx.isRefreshing = true
                                    ctx.chatList.clear()
                                    ctx.chatList.addAll(ctx.loadChatList(ctx.page))
                                    ctx.isRefreshing = false
                                }
                            }
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
                                        padding(12f)
                                        backgroundColor(Color(0xFFFFFFFF))
                                        marginTop(8f)
                                        marginLeft(16f)
                                        marginRight(16f)
                                        borderRadius(8f)
                                    }
                                    event {
                                        click {
                                            val router = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                            router?.openPage("ChatDetailPage", JSONObject().apply {
                                                put("chatId", item.id)
                                                put("nickname", item.nickname)
                                            })
                                        }
                                    }
                                    Image {
                                        attr {
                                            size(48f, 48f)
                                            borderRadius(24f)
                                            marginRight(12f)
                                            src(item.avatar)
                                            backgroundColor(Color(0xFFE0E0E0))
                                        }
                                    }
                                    View {
                                        attr {
                                            flex(1f)
                                        }
                                        View {
                                            attr {
                                                flexDirectionRow()
                                                alignItemsCenter()
                                                marginBottom(4f)
                                            }
                                            Text {
                                                attr {
                                                    flex(1f)
                                                    fontSize(16f)
                                                    fontWeightMedium()
                                                    color(Color(0xFF333333))
                                                    text(item.nickname)
                                                }
                                            }
                                            Text {
                                                attr {
                                                    fontSize(12f)
                                                    color(Color(0xFF999999))
                                                    text(item.time)
                                                }
                                            }
                                        }
                                        Text {
                                            attr {
                                                fontSize(14f)
                                                color(Color(0xFF666666))
                                                maxLines(1)
                                                lineBreakMode(LineBreakMode.TAIL)
                                                text(item.lastMessage)
                                            }
                                        }
                                    }
                                    vif({ item.unreadCount > 0 }) {
                                        View {
                                            attr {
                                                size(20f, 20f)
                                                borderRadius(10f)
                                                backgroundColor(Color(0xFFFF4444))
                                                allCenter()
                                                marginLeft(8f)
                                            }
                                            Text {
                                                attr {
                                                    fontSize(12f)
                                                    color(Color(0xFFFFFFFF))
                                                    fontWeightBold()
                                                    text(item.unreadCount.toString())
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
}