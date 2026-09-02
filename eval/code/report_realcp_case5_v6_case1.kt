package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.RefreshViewState
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.reactive.handler.observable

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
                                    ctx.refreshing = true
                                    val newList = mutableListOf<ChatItemData>()
                                    newList.add(ChatItemData("avatar1.png", "张三", "你好，最近怎么样？", "12:30"))
                                    newList.add(ChatItemData("avatar2.png", "李四", "明天一起吃饭吗？", "12:25"))
                                    newList.add(ChatItemData("avatar3.png", "王五", "收到，谢谢！", "12:20"))
                                    ctx.chatList.clear()
                                    ctx.chatList.addAll(newList)
                                    ctx.refreshing = false
                                }
                            }
                        }
                        List {
                            attr {
                                flex(1f)
                            }
                            vfor({ ctx.chatList }) { item ->
                                View {
                                    attr {
                                        flexDirectionRow()
                                        alignItemsCenter()
                                        padding(12f, 10f, 12f, 10f)
                                        backgroundColor(Color.WHITE)
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