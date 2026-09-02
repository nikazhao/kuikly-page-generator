package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.*

@Page("ProductDetailPage")
internal class ProductDetailPage : Pager() {

    private var title by observable("商品标题")
    private var price by observable("¥99.00")
    private var description by observable("这是一段商品描述文字，用于展示商品详细信息。")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flex(1f)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Scroller {
                    attr {
                        flex(1f)
                        flexDirectionColumn()
                    }
                    Image {
                        attr {
                            width(pagerData.pageViewWidth)
                            height(300f)
                        }
                    }
                    Text {
                        attr {
                            marginTop(12f)
                            marginLeft(16f)
                            marginRight(16f)
                            fontSize(20f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                            text(ctx.title)
                        }
                    }
                    Text {
                        attr {
                            marginTop(8f)
                            marginLeft(16f)
                            marginRight(16f)
                            fontSize(24f)
                            fontWeightBold()
                            color(Color(0xFFFF4759))
                            text(ctx.price)
                        }
                    }
                    Text {
                        attr {
                            marginTop(8f)
                            marginLeft(16f)
                            marginRight(16f)
                            fontSize(14f)
                            color(Color(0xFF666666))
                            text(ctx.description)
                        }
                    }
                    Button {
                        attr {
                            marginTop(16f)
                            marginLeft(16f)
                            marginRight(16f)
                            height(48f)
                            borderRadius(8f)
                            backgroundColor(Color(0xFF07C160))
                            titleAttr {
                                text("加入购物车")
                                fontSize(16f)
                                color(Color.WHITE)
                            }
                        }
                        event {
                            touchDown {
                                // 加入购物车逻辑
                            }
                        }
                    }
                }
            }
        }
    }
}