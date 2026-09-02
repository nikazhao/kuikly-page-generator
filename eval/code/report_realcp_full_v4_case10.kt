package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.module.NotifyModule
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

@Page("EmptyStatePage")
internal class EmptyStatePage : Pager() {

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                }
                Center {
                    View {
                        attr {
                            flexDirectionColumn()
                            alignItemsCenter()
                        }
                        Image {
                            attr {
                                size(120f, 120f)
                                src(ImageUri.pageAssets("empty_icon"))
                            }
                        }
                        Text {
                            attr {
                                marginTop(24f)
                                text("暂无数据")
                                fontSize(16f)
                                color(Color(0xFF999999))
                            }
                        }
                        Button {
                            attr {
                                marginTop(32f)
                                size(160f, 44f)
                                borderRadius(22f)
                                backgroundColor(Color(0xFF4A90D9))
                                titleAttr {
                                    text("重试")
                                    fontSize(16f)
                                    color(Color.WHITE)
                                }
                            }
                            event {
                                touchUp {
                                    val notifyModule = ctx.acquireModule<NotifyModule>(NotifyModule.MODULE_NAME)
                                    notifyModule?.postNotify("retry", JSONObject())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}