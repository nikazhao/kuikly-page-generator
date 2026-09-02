package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.module.SharedPreferencesModule
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
    val lastMessage: String = "",
    val time: String = ""
)

@Page("ChatListPage")
internal class ChatListPage : Pager() {

    private var chatList by observable(observableList<ChatItemData>())
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
                                    val networkModule = ctx.getModule<NetworkModule>(NetworkModule.MODULE_NAME)
                                    if (networkModule != null) {
                                        networkModule.requestGet(
                                            "https://api.example.com/chat/list",
                                            JSONObject(),
                                            { response, success, msg ->
                                                if (success) {
                                                    val jsonArray = response.optJSONArray("data")
                                                    if (jsonArray != null) {
                                                        val newList = mutableListOf<ChatItemData>()
                                                        for (i in 0 until jsonArray.length()) {
                                                            val item = jsonArray.getJSONObject(i)
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
                                                        val sp = ctx.getModule<SharedPreferencesModule>(SharedPreferencesModule.MODULE_NAME)
                                                        sp?.setObject("chatListCache", JSONObject().apply {
                                                            put("list", jsonArray)
                                                        })
                                                    }
                                                }
                                                ctx.isRefreshing = false
                                            }
                                        )
                                    } else {
                                        val mockData = mutableListOf(
                                            ChatItemData("avatar1.png", "张三", "你好，在吗？", "10:30"),
                                            ChatItemData("avatar2.png", "李四", "明天见", "09:15"),
                                            ChatItemData("avatar3.png", "王五", "收到", "昨天")
                                        )
                                        ctx.chatList.clear()
                                        ctx.chatList.addAll(mockData)
                                        ctx.isRefreshing = false
                                    }
                                }
                            }
                        }
                        vforLazy({ ctx.chatList }) { item, index, count ->
                            View {
                                attr {
                                    flexDirectionRow()
                                    alignItemsCenter()
                                    paddingHorizontal(16f)
                                    paddingVertical(12f)
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
                                    }
                                    View {
                                        attr {
                                            flexDirectionRow()
                                            alignItemsCenter()
                                            justifyContentSpaceBetween()
                                        }
                                        Text {
                                            attr {
                                                text(item.nickname)
                                                fontSize(16f)
                                                fontWeightBold()
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
                                            marginTop(4f)
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