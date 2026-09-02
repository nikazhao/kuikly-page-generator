package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

@Page("ProductDetailPage")
internal class ProductDetailPage : Pager() {

    private var productTitle by observable("")
    private var productPrice by observable("")
    private var productDescription by observable("")

    private lateinit var sp: SharedPreferencesModule

    override fun created() {
        super.created()
        sp = acquireModule(SharedPreferencesModule.MODULE_NAME)
        productTitle = sp.getString("productTitle") ?: ""
        productPrice = sp.getString("productPrice") ?: ""
        productDescription = sp.getString("productDescription") ?: ""
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Scroller {
                    attr {
                        size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                        flex(1f)
                    }
                    View {
                        attr {
                            flexDirectionColumn()
                        }
                        Image {
                            attr {
                                width(pagerData.pageViewWidth)
                                height(pagerData.pageViewWidth / 1.5f)
                            }
                        }
                        Text {
                            attr {
                                text(ctx.productTitle.ifEmpty { "商品标题" })
                                fontSize(20f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                                marginTop(12f)
                                marginLeft(16f)
                                marginRight(16f)
                            }
                        }
                        Text {
                            attr {
                                text(ctx.productPrice.ifEmpty { "¥99.00" })
                                fontSize(24f)
                                color(Color(0xFFFF0000))
                                fontWeightBold()
                                marginTop(12f)
                                marginLeft(16f)
                                marginRight(16f)
                            }
                        }
                        Text {
                            attr {
                                text(ctx.productDescription.ifEmpty { "这是一段商品描述信息，用于展示商品的详细说明。" })
                                fontSize(14f)
                                color(Color(0xFF666666))
                                marginTop(12f)
                                marginLeft(16f)
                                marginRight(16f)
                                lineHeight(20f)
                            }
                        }
                        Button {
                            attr {
                                size(pagerData.pageViewWidth - 32f, 48f)
                                marginTop(24f)
                                marginLeft(16f)
                                marginRight(16f)
                                borderRadius(24f)
                                backgroundColor(Color(0xFFFF6B35))
                                titleAttr {
                                    text("加入购物车")
                                    fontSize(16f)
                                    color(Color.WHITE)
                                }
                            }
                            event {
                                touchDown {
                                    ctx.sp.setString("cart", ctx.productTitle)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}