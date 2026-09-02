package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONArray
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Refresh
import com.tencent.kuikly.core.views.RefreshViewState
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

data class ChatItemData(
    val avatar: String = "",
    val nickname: String = "",
    val lastMessage: String = "",
    val time: String = ""
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
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    padding(
                        pagerData.safeAreaInsets.top,
                        pagerData.safeAreaInsets.left,
                        pagerData.safeAreaInsets.bottom,
                        pagerData.safeAreaInsets.right
                    )
                    flex(1f)
                    backgroundColor(Color(0xFFF5F5F5))
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
                                    ctx.refreshing = true
                                    val networkModule = ctx.acquireModule<NetworkModule>(NetworkModule.MODULE_NAME)
                                    networkModule?.requestGet(
                                        "https://api.example.com/chat/list",
                                        JSONObject()
                                    ) { response, success, _ ->
                                        if (success) {
                                            val newList = mutableListOf<ChatItemData>()
                                            val result = response.optJSONArray("data")
                                            if (result != null) {
                                                for (i in 0 until result.length()) {
                                                    val item = result.optJSONObject(i) ?: continue
                                                    newList.add(
                                                        ChatItemData(
                                                            avatar = item.optString("avatar", ""),
                                                            nickname = item.optString("nickname", ""),
                                                            lastMessage = item.optString("lastMessage", ""),
                                                            time = item.optString("time", "")
                                                        )
                                                    )
                                                }
                                            }
                                            ctx.chatList.clear()
                                            ctx.chatList.addAll(newList)
                                        }
                                        ctx.refreshing = false
                                    }
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
                                    View {
                                        attr {
                                            size(48f, 48f)
                                            borderRadius(24f)
                                            backgroundColor(Color(0xFFE0E0E0))
                                            marginRight(12f)
                                        }
                                    }
                                    View {
                                        attr {
                                            flex(1f)
                                            flexDirection(FlexDirection.COLUMN)
                                        }
                                        View {
                                            attr {
                                                flexDirection(FlexDirection.ROW)
                                                alignItemsCenter()
                                                justifyContentSpaceBetween()
                                                marginBottom(4f)
                                            }
                                            Text {
                                                attr {
                                                    text(item.nickname)
                                                    fontSize(16f)
                                                    fontWeightMedium()
                                                    color(Color(0xFF333333))
                                                    flex(1f)
                                                }
                                            }
                                            Text {
                                                attr {
                                                    text(item.time)
                                                    fontSize(12f)
                                                    color(Color(0xFF999999))
                                                    marginLeft(8f)
                                                }
                                            }
                                        }
                                        Text {
                                            attr {
                                                text(item.lastMessage)
                                                fontSize(14f)
                                                color(Color(0xFF666666))
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