package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.module.RouterModule
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

data class ChatItem(
    val id: String,
    val avatar: String,
    val nickname: String,
    val lastMessage: String,
    val time: String
)

class AudioHapticsModule : Module() {
    override fun moduleName(): String = "KRAudioHapticsModule"

    companion object {
        const val MODULE_NAME = "KRAudioHapticsModule"
    }

    fun playSound(soundName: String) {
        asyncToNativeMethod(
            "playSound",
            JSONObject().apply { put("soundName", soundName) },
            null
        )
    }

    fun vibrate(durationMs: Int) {
        asyncToNativeMethod(
            "vibrate",
            JSONObject().apply { put("durationMs", durationMs) },
            null
        )
    }
}

@Page("ChatListPage")
internal class ChatListPage : Pager() {

    private var chatList by observableList<ChatItem>()
    private var isRefreshing by observable(false)
    private var page by observable(1)
    private var hasMore by observable(true)

    private lateinit var audioModule: AudioHapticsModule

    override fun createExternalModules(): Map<String, Module>? {
        return mapOf(AudioHapticsModule.MODULE_NAME to AudioHapticsModule())
    }

    override fun created() {
        super.created()
        audioModule = acquireModule<AudioHapticsModule>(AudioHapticsModule.MODULE_NAME)
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
                                    ctx.isRefreshing = true
                                    ctx.page = 1
                                    ctx.chatList.clear()
                                    ctx.isRefreshing = false
                                }
                            }
                        }
                        View {
                            attr {
                                flexDirection(FlexDirection.COLUMN)
                            }
                            vfor({ ctx.chatList }) { item: ChatItem ->
                                View {
                                    attr {
                                        flexDirection(FlexDirection.ROW)
                                        alignItemsCenter()
                                        padding(12f, 16f, 12f, 16f)
                                        backgroundColor(Color.WHITE)
                                    }
                                    event {
                                        click {
                                            ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME).openPage("ChatDetailPage", JSONObject().apply {
                                                put("chatId", item.id)
                                                put("nickname", item.nickname)
                                            })
                                        }
                                        longClick {
                                            ctx.audioModule?.vibrate(20)
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
                                            color(Color(0xFFCCCCCC))
                                            textAlignRight()
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