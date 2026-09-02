package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONArray
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
import com.tencent.kuikly.core.module.Module

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
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
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
                                    networkModule.requestGet(
                                        "https://api.example.com/chat/list",
                                        JSONObject(),
                                        { response, success, _ ->
                                            if (success) {
                                                val jsonArray = response.optJSONArray("data") ?: JSONArray()
                                                val newList = mutableListOf<ChatItemData>()
                                                for (i in 0 until jsonArray.length()) {
                                                    val item = jsonArray.optJSONObject(i) ?: continue
                                                    newList.add(
                                                        ChatItemData(
                                                            avatar = item.optString("avatar", ""),
                                                            nickname = item.optString("nickname", ""),
                                                            lastMessage = item.optString("lastMessage", ""),
                                                            time = item.optString("time", "")
                                                        )
                                                    )
                                                }
                                                ctx.chatList.clear()
                                                ctx.chatList.addAll(newList)
                                            }
                                            ctx.refreshing = false
                                        }
                                    )
                                }
                            }
                        }
                        List {
                            attr {
                                size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                                flex(1f)
                            }
                            vforLazy({ ctx.chatList }) { item: ChatItemData, index: Int, _: Int ->
                                View {
                                    attr {
                                        size(pagerData.pageViewWidth, 72f)
                                        flexDirection(FlexDirection.ROW)
                                        alignItemsCenter()
                                        backgroundColor(Color.WHITE)
                                        padding(12f, 8f, 12f, 8f)
                                    }
                                    event {
                                        click {
                                            val routerModule = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                            routerModule.openPage(
                                                "ChatDetailPage",
                                                JSONObject().apply {
                                                    put("chatId", item.nickname)
                                                }
                                            )
                                        }
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
                                                lines(1)
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
                                            alignSelfFlexStart()
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