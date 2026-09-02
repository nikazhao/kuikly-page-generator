package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*

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
                    backgroundColor(Color(0xFFF5F5F5))
                }
                View {
                    attr {
                        size(pagerData.pageViewWidth, 56f)
                        backgroundColor(Color.WHITE)
                        flexDirection(FlexDirection.ROW)
                        alignItemsCenter()
                        paddingTop(pagerData.safeAreaInsets.top)
                    }
                    View {
                        attr {
                            size(44f, 44f)
                            marginLeft(8f)
                            alignItemsCenter()
                            justifyContentCenter()
                        }
                        Text {
                            attr {
                                text("←")
                                fontSize(20f)
                                color(Color(0xFF333333))
                            }
                        }
                    }
                    View {
                        attr {
                            flex(1f)
                            alignItemsCenter()
                            justifyContentCenter()
                        }
                        Text {
                            attr {
                                text("聊天")
                                fontSize(18f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                            }
                        }
                    }
                    View {
                        attr {
                            size(44f, 44f)
                            marginRight(8f)
                            alignItemsCenter()
                            justifyContentCenter()
                        }
                        Text {
                            attr {
                                text("···")
                                fontSize(20f)
                                color(Color(0xFF333333))
                            }
                        }
                    }
                }
                Scroller {
                    attr {
                        flex(1f)
                        width(pagerData.pageViewWidth)
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth)
                        }
                        for (item in ctx.chatList) {
                            View {
                                attr {
                                    width(pagerData.pageViewWidth)
                                    height(72f)
                                    backgroundColor(Color.WHITE)
                                    flexDirection(FlexDirection.ROW)
                                    alignItemsCenter()
                                    paddingLeft(16f)
                                    paddingRight(16f)
                                }
                                event {
                                    click {
                                        val router = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                        router.openPage("ChatDetailPage", JSONObject().apply {
                                            put("userId", item.userId)
                                            put("nickname", item.nickname)
                                        })
                                    }
                                }
                                View {
                                    attr {
                                        size(48f, 48f)
                                        borderRadius(24f)
                                        backgroundColor(Color(0xFF4A90D9))
                                        alignItemsCenter()
                                        justifyContentCenter()
                                    }
                                    Text {
                                        attr {
                                            text(item.nickname.substring(0, 1))
                                            fontSize(20f)
                                            color(Color.WHITE)
                                            fontWeightBold()
                                        }
                                    }
                                }
                                View {
                                    attr {
                                        flex(1f)
                                        marginLeft(12f)
                                    }
                                    View {
                                        attr {
                                            flexDirection(FlexDirection.ROW)
                                            alignItemsCenter()
                                            justifyContentSpaceBetween()
                                            width(pagerData.pageViewWidth - 76f)
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
                                                text(item.lastTime)
                                                fontSize(12f)
                                                color(Color(0xFF999999))
                                            }
                                        }
                                    }
                                    View {
                                        attr {
                                            flexDirection(FlexDirection.ROW)
                                            alignItemsCenter()
                                            marginTop(4f)
                                            width(pagerData.pageViewWidth - 76f)
                                        }
                                        Text {
                                            attr {
                                                text(item.lastMessage)
                                                fontSize(14f)
                                                color(Color(0xFF666666))
                                                lines(1)
                                                flex(1f)
                                            }
                                        }
                                    }
                                }
                            }
                            View {
                                attr {
                                    width(pagerData.pageViewWidth)
                                    height(0.5f)
                                    backgroundColor(Color(0xFFE0E0E0))
                                    marginLeft(76f)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}