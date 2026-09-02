package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONArray
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Refresh
import com.tencent.kuikly.core.views.RefreshViewState
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

data class ChatItemData(
    val avatar: String = "",
    val nickname: String = "",
    val lastMsgContent: String = "",
    val lastMsgTime: String = "",
    val userId: String = ""
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
                                        "https://api.example.com/chat/list?page=2",
                                        JSONObject()
                                    ) { response, success, _ ->
                                        if (success) {
                                            val jsonArray = response.optJSONArray("data") ?: JSONArray()
                                            val newList = mutableListOf<ChatItemData>()
                                            for (i in 0 until jsonArray.length()) {
                                                val item = jsonArray.optJSONObject(i) ?: continue
                                                newList.add(
                                                    ChatItemData(
                                                        avatar = item.optString("avatar"),
                                                        nickname = item.optString("nickname"),
                                                        lastMsgContent = item.optString("lastMessage"),
                                                        lastMsgTime = item.optString("time"),
                                                        userId = item.optString("userId")
                                                    )
                                                )
                                            }
                                            ctx.chatList.clear()
                                            ctx.chatList.addAll(newList)
                                        }
                                        ctx.refreshing = false
                                    }
                                }
                            }
                        }
                        vforLazy({ ctx.chatList }) { item: ChatItemData, index: Int, _: Int ->
                            View {
                                attr {
                                    flexDirectionRow()
                                    padding(12f, 12f, 12f, 12f)
                                    alignItemsCenter()
                                    backgroundColor(Color.WHITE)
                                }
                                Image {
                                    attr {
                                        size(48f, 48f)
                                        borderRadius(24f)
                                        marginRight(12f)
                                        src(item.avatar)
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
                                        }
                                        Text {
                                            attr {
                                                flex(1f)
                                                text(item.nickname)
                                                fontWeightBold()
                                                fontSize(16f)
                                                color(Color(0xFF333333))
                                            }
                                        }
                                        Text {
                                            attr {
                                                text(item.lastMsgTime)
                                                fontSize(12f)
                                                color(Color(0xFF999999))
                                                textAlignRight()
                                            }
                                        }
                                    }
                                    Text {
                                        attr {
                                            text(item.lastMsgContent)
                                            fontSize(14f)
                                            color(Color(0xFF666666))
                                            lines(1)
                                            marginTop(4f)
                                        }
                                    }
                                }
                                event {
                                    touchDown { _ ->
                                        val itemData = ctx.chatList.getOrNull(index) ?: return@touchDown
                                        val routerModule = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                        routerModule?.openPage(
                                            "ChatDetailPage",
                                            JSONObject().apply {
                                                put("userId", itemData.userId)
                                                put("nickname", itemData.nickname)
                                            }
                                        )
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