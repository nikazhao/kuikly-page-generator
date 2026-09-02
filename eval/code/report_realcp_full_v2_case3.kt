package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.*

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

@Page("ProductDetailPage")
internal class ProductDetailPage : Pager() {

    private var productTitle by observable("")
    private var productPrice by observable("")
    private var productDescription by observable("")
    private lateinit var spModule: SharedPreferencesModule
    private lateinit var audioModule: AudioHapticsModule

    override fun createExternalModules(): Map<String, Module>? {
        return mapOf(AudioHapticsModule.MODULE_NAME to AudioHapticsModule())
    }

    override fun created() {
        super.created()
        spModule = acquireModule(SharedPreferencesModule.MODULE_NAME)
        audioModule = acquireModule(AudioHapticsModule.MODULE_NAME)
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flex(1f)
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Scroller {
                    attr {
                        flex(1f)
                        size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    }
                    Image {
                        attr {
                            width(pagerData.pageViewWidth)
                            height(pagerData.pageViewWidth * 0.75f)
                            src("https://example.com/product_image.jpg")
                        }
                    }
                    Text {
                        attr {
                            marginTop(16f)
                            marginLeft(16f)
                            marginRight(16f)
                            fontSize(24f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                            text("商品标题")
                        }
                    }
                    Text {
                        attr {
                            marginTop(8f)
                            marginLeft(16f)
                            marginRight(16f)
                            fontSize(20f)
                            fontWeightBold()
                            color(Color(0xFFFF6B35))
                            text("¥99.00")
                        }
                    }
                    Text {
                        attr {
                            marginTop(12f)
                            marginLeft(16f)
                            marginRight(16f)
                            fontSize(14f)
                            color(Color(0xFF666666))
                            text("这是一段商品描述，展示商品的主要特点和功能信息。")
                        }
                    }
                    Button {
                        attr {
                            marginTop(24f)
                            marginLeft(16f)
                            marginRight(16f)
                            height(48f)
                            width(pagerData.pageViewWidth - 32f)
                            backgroundColor(Color(0xFF07C160))
                            borderRadius(8f)
                            titleAttr {
                                text("加入购物车")
                                fontSize(16f)
                                fontWeightMedium()
                                color(Color.WHITE)
                            }
                        }
                        event {
                            click {
                                ctx.spModule.setInt("cart_has_product", 1)
                                ctx.audioModule.playSound("add_to_cart")
                                ctx.audioModule.vibrate(50)
                            }
                        }
                    }
                }
            }
        }
    }
}