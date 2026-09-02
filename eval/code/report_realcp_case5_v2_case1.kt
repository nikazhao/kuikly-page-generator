package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.layout.FlexAlign
import com.tencent.kuikly.core.layout.FlexJustifyContent
import com.tencent.kuikly.core.module.NetworkModule
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
    val avatar: String,
    val nickname: String,
    val lastMessage: String,
    val time: String
)

@Page("ChatListPage")
internal class ChatListPage : Pager() {

    private var chatList by observableList<ChatItemData>()
    private var isRefreshing by observable(false)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
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
                        vfor({ ctx.chatList }) { item ->
                            View {
                                attr {
                                    flexDirectionRow()
                                    width(pagerData.pageViewWidth)
                                    padding(16f, 12f)
                                    backgroundColor(Color(0xFFFFFFFF))
                                }
                                Image {
                                    attr {
                                        size(48f, 48f)
                                        borderRadius(24f)
                                        backgroundColor(Color(0xFFE0E0E0))
                                        src(item.avatar)
                                    }
                                }
                                View {
                                    attr {
                                        flex(1f)
                                        marginLeft(12f)
                                        justifyContent(FlexJustifyContent.CENTER)
                                    }
                                    Text {
                                        attr {
                                            text(item.nickname)
                                            fontSize(16f)
                                            fontWeightBold()
                                            color(Color(0xFF333333))
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
                                        color(Color(0xFFBBBBBB))
                                        textAlignRight()
                                        alignSelf(FlexAlign.FLEX_END)
                                        marginLeft(8f)
                                    }
                                }
                            }
                        }
                        event {
                            refreshStateDidChange { state ->
                                if (state == RefreshViewState.REFRESHING) {
                                    ctx.isRefreshing = true
                                    val networkModule = ctx.acquireModule<NetworkModule>(NetworkModule.MODULE_NAME)
                                    networkModule?.requestGet(
                                        "https://api.example.com/chat/list",
                                        JSONObject()
                                    ) { response, success, msg ->
                                        if (success && response is JSONArray) {
                                            val newList = ctx.parseChatList(response)
                                            ctx.chatList.clear()
                                            ctx.chatList.addAll(newList)
                                        }
                                        ctx.isRefreshing = false
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun parseChatList(jsonArray: JSONArray): List<ChatItemData> {
        val list = mutableListOf<ChatItemData>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.optJSONObject(i)
            list.add(
                ChatItemData(
                    avatar = obj?.optString("avatar", "") ?: "",
                    nickname = obj?.optString("nickname", "") ?: "",
                    lastMessage = obj?.optString("lastMessage", "") ?: "",
                    time = obj?.optString("time", "") ?: ""
                )
            )
        }
        return list
    }
}