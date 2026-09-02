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

    private var title by observable("")
    private var price by observable("")
    private var description by observable("")
    private var imageUrl by observable("")

    private lateinit var audioModule: AudioHapticsModule
    private lateinit var sp: SharedPreferencesModule

    override fun createExternalModules(): Map<String, Module>? {
        return mapOf(AudioHapticsModule.MODULE_NAME to AudioHapticsModule())
    }

    override fun created() {
        super.created()
        audioModule = acquireModule(AudioHapticsModule.MODULE_NAME)
        sp = acquireModule(SharedPreferencesModule.MODULE_NAME)
        title = sp.getString("product_title")
        price = sp.getString("product_price")
        description = sp.getString("product_description")
        imageUrl = sp.getString("product_image_url")
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            Scroller {
                attr {
                    flex(1f)
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                }
                View {
                    attr {
                        flexDirectionColumn()
                        width(pagerData.pageViewWidth)
                    }
                    Image {
                        attr {
                            width(pagerData.pageViewWidth)
                            height(300f)
                            src(ctx.imageUrl.ifEmpty { "https://example.com/product_image.jpg" })
                        }
                    }
                    Text {
                        attr {
                            text(ctx.title.ifEmpty { "商品标题" })
                            fontSize(20f)
                            fontWeightBold()
                            marginTop(16f)
                            marginLeft(16f)
                            marginRight(16f)
                        }
                    }
                    Text {
                        attr {
                            text(ctx.price.ifEmpty { "¥99.99" })
                            fontSize(24f)
                            color(Color(0xFFFF5722))
                            fontWeightBold()
                            marginTop(8f)
                            marginLeft(16f)
                            marginRight(16f)
                        }
                    }
                    Text {
                        attr {
                            text(ctx.description.ifEmpty { "这是一段商品描述，详细介绍了该商品的特点和功能。" })
                            fontSize(16f)
                            color(Color(0xFF666666))
                            marginTop(12f)
                            marginLeft(16f)
                            marginRight(16f)
                            lines(3)
                        }
                    }
                    Button {
                        attr {
                            marginTop(24f)
                            marginLeft(16f)
                            marginRight(16f)
                            height(48f)
                            backgroundColor(Color(0xFFFF5722))
                            borderRadius(24f)
                            titleAttr {
                                text("加入购物车")
                                fontSize(18f)
                                color(Color.WHITE)
                            }
                        }
                        event {
                            touchDown {
                                ctx.sp.setString("cart_item", ctx.title)
                                ctx.audioModule.playSound("add_to_cart")
                                ctx.audioModule.vibrate(30)
                            }
                        }
                    }
                }
            }
        }
    }
}