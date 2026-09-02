package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.layout.BorderStyle
import com.tencent.kuikly.core.module.NetworkModule
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
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
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
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Scroller {
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
                                            val jsonArray = response.optJSONArray("list") ?: JSONArray()
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
                                }
                            }
                        }
                        List {
                            attr {
                                flex(1f)
                            }
                            vforLazy({ ctx.chatList }) { item, index, _ ->
                                View {
                                    attr {
                                        flexDirectionRow()
                                        alignItemsCenter()
                                        padding(12f, 16f, 12f, 16f)
                                    }
                                    Image {
                                        attr {
                                            size(48f, 48f)
                                            borderRadius(24f)
                                            border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                                            src(item.avatar)
                                        }
                                    }
                                    View {
                                        attr {
                                            flexDirectionColumn()
                                            flex(1f)
                                            marginLeft(12f)
                                            marginRight(12f)
                                        }
                                        Text {
                                            attr {
                                                text(item.nickname)
                                                fontWeightBold()
                                                fontSize(16f)
                                                color(Color(0xFF333333))
                                            }
                                        }
                                        Text {
                                            attr {
                                                text(item.lastMessage)
                                                fontSize(14f)
                                                color(Color(0xFF666666))
                                                lines(1)
                                                marginTop(4f)
                                            }
                                        }
                                    }
                                    Text {
                                        attr {
                                            text(item.time)
                                            fontSize(12f)
                                            color(Color(0xFF999999))
                                            textAlignCenter()
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