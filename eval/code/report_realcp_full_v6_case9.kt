package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*

@Page("DataDashboardPage")
internal class DataDashboardPage : Pager() {

    private var card1Value by observable("0")
    private var card2Value by observable("0")
    private var card3Value by observable("0")
    private var progressValue by observable(0f)
    private var listData by observableList<String>()
    private var isLoading by observable(false)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirection(FlexDirection.COLUMN)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                // 顶部三个数字指标卡片
                View {
                    attr {
                        flexDirection(FlexDirection.ROW)
                        padding(16f, 16f, 16f, 0f)
                    }
                    // 卡片1
                    View {
                        attr {
                            flex(1f)
                            marginRight(8f)
                            backgroundColor(Color.WHITE)
                            borderRadius(12f)
                            padding(16f, 12f, 16f, 12f)
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                fontSize(28f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                                text(ctx.card1Value)
                            }
                        }
                        Text {
                            attr {
                                fontSize(12f)
                                color(Color(0xFF999999))
                                marginTop(4f)
                                text("总用户数")
                            }
                        }
                    }
                    // 卡片2
                    View {
                        attr {
                            flex(1f)
                            marginLeft(8f)
                            marginRight(8f)
                            backgroundColor(Color.WHITE)
                            borderRadius(12f)
                            padding(16f, 12f, 16f, 12f)
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                fontSize(28f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                                text(ctx.card2Value)
                            }
                        }
                        Text {
                            attr {
                                fontSize(12f)
                                color(Color(0xFF999999))
                                marginTop(4f)
                                text("活跃用户")
                            }
                        }
                    }
                    // 卡片3
                    View {
                        attr {
                            flex(1f)
                            marginLeft(8f)
                            backgroundColor(Color.WHITE)
                            borderRadius(12f)
                            padding(16f, 12f, 16f, 12f)
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                fontSize(28f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                                text(ctx.card3Value)
                            }
                        }
                        Text {
                            attr {
                                fontSize(12f)
                                color(Color(0xFF999999))
                                marginTop(4f)
                                text("完成率")
                            }
                        }
                    }
                }
                // 进度条
                View {
                    attr {
                        margin(16f, 16f, 16f, 0f)
                        padding(16f, 12f, 16f, 12f)
                        backgroundColor(Color.WHITE)
                        borderRadius(12f)
                    }
                    Text {
                        attr {
                            fontSize(14f)
                            fontWeightMedium()
                            color(Color(0xFF333333))
                            text("项目进度")
                        }
                    }
                    View {
                        attr {
                            marginTop(12f)
                            size(pagerData.pageViewWidth - 64f, 8f)
                            borderRadius(4f)
                            backgroundColor(Color(0xFFE0E0E0))
                        }
                        View {
                            attr {
                                size((pagerData.pageViewWidth - 64f) * ctx.progressValue, 8f)
                                borderRadius(4f)
                                backgroundColor(Color(0xFF4A90D9))
                            }
                        }
                    }
                    Text {
                        attr {
                            marginTop(8f)
                            fontSize(12f)
                            color(Color(0xFF999999))
                            text("${(ctx.progressValue * 100).toInt()}% 已完成")
                        }
                    }
                }
                // 列表
                List {
                    attr {
                        flex(1f)
                        marginTop(16f)
                        marginLeft(16f)
                        marginRight(16f)
                        marginBottom(16f)
                        backgroundColor(Color.WHITE)
                        borderRadius(12f)
                    }
                    vforLazy({ ctx.listData }) { item: String, index: Int, _: Int ->
                        View {
                            attr {
                                padding(16f, 12f, 16f, 12f)
                                if (index < ctx.listData.size - 1) {
                                    border(Border(0.5f / pagerData.density, BorderStyle.SOLID, Color(0xFFEEEEEE)))
                                }
                            }
                            Text {
                                attr {
                                    fontSize(14f)
                                    color(Color(0xFF333333))
                                    text(item)
                                }
                            }
                        }
                    }
                }
            }
            // 点击刷新数据
            event {
                click { params ->
                    ctx.isLoading = true
                    ctx.card1Value = "1,234"
                    ctx.card2Value = "567"
                    ctx.card3Value = "89%"
                    ctx.progressValue = 0.65f
                    ctx.listData.clear()
                    ctx.listData.addAll(listOf("项目A", "项目B", "项目C", "项目D"))
                    ctx.isLoading = false
                }
            }
        }
    }
}