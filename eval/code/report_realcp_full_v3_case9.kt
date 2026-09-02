package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.layout.FlexAlign
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.FlexJustifyContent
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.directives.vforLazy

@Page("DataDashboardPage")
internal class DataDashboardPage : Pager() {

    private var listData by observableList<ListItemData>()

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirection(FlexDirection.COLUMN)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                // 顶部：三个数字指标卡片
                View {
                    attr {
                        flexDirection(FlexDirection.ROW)
                        padding(16f, 16f, 16f, 0f)
                        justifyContent(FlexJustifyContent.SPACE_BETWEEN)
                    }
                    // 卡片1
                    View {
                        attr {
                            flex(1f)
                            height(100f)
                            backgroundColor(Color.WHITE)
                            borderRadius(12f)
                            marginRight(8f)
                            alignItems(FlexAlign.CENTER)
                            justifyContent(FlexJustifyContent.CENTER)
                        }
                        Text {
                            attr {
                                text("1,234")
                                fontSize(28f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                            }
                        }
                        Text {
                            attr {
                                text("总用户数")
                                fontSize(14f)
                                color(Color(0xFF999999))
                                marginTop(8f)
                            }
                        }
                    }
                    // 卡片2
                    View {
                        attr {
                            flex(1f)
                            height(100f)
                            backgroundColor(Color.WHITE)
                            borderRadius(12f)
                            marginLeft(4f)
                            marginRight(4f)
                            alignItems(FlexAlign.CENTER)
                            justifyContent(FlexJustifyContent.CENTER)
                        }
                        Text {
                            attr {
                                text("85%")
                                fontSize(28f)
                                fontWeightBold()
                                color(Color(0xFF4CAF50))
                            }
                        }
                        Text {
                            attr {
                                text("活跃率")
                                fontSize(14f)
                                color(Color(0xFF999999))
                                marginTop(8f)
                            }
                        }
                    }
                    // 卡片3
                    View {
                        attr {
                            flex(1f)
                            height(100f)
                            backgroundColor(Color.WHITE)
                            borderRadius(12f)
                            marginLeft(8f)
                            alignItems(FlexAlign.CENTER)
                            justifyContent(FlexJustifyContent.CENTER)
                        }
                        Text {
                            attr {
                                text("¥56,789")
                                fontSize(28f)
                                fontWeightBold()
                                color(Color(0xFFFF9800))
                            }
                        }
                        Text {
                            attr {
                                text("总收入")
                                fontSize(14f)
                                color(Color(0xFF999999))
                                marginTop(8f)
                            }
                        }
                    }
                }
                // 中部：进度条
                View {
                    attr {
                        margin(16f, 16f, 16f, 0f)
                        padding(16f)
                        backgroundColor(Color.WHITE)
                        borderRadius(12f)
                    }
                    Text {
                        attr {
                            text("项目完成进度")
                            fontSize(16f)
                            fontWeightMedium()
                            color(Color(0xFF333333))
                            marginBottom(12f)
                        }
                    }
                    // 进度条轨道
                    View {
                        attr {
                            height(20f)
                            backgroundColor(Color(0xFFE0E0E0))
                            borderRadius(10f)
                        }
                        // 进度条填充
                        View {
                            attr {
                                width(75f) // 75% 进度
                                height(20f)
                                backgroundColor(Color(0xFF4CAF50))
                                borderRadius(10f)
                            }
                        }
                    }
                    Text {
                        attr {
                            text("75%")
                            fontSize(14f)
                            color(Color(0xFF666666))
                            marginTop(8f)
                            textAlignRight()
                        }
                    }
                }
                // 底部：列表
                View {
                    attr {
                        flex(1f)
                        margin(16f, 16f, 16f, 16f)
                        backgroundColor(Color.WHITE)
                        borderRadius(12f)
                    }
                    Text {
                        attr {
                            text("最近动态")
                            fontSize(16f)
                            fontWeightMedium()
                            color(Color(0xFF333333))
                            padding(16f, 16f, 16f, 0f)
                        }
                    }
                    List {
                        attr {
                            flex(1f)
                            padding(0f, 8f, 0f, 8f)
                        }
                        vforLazy({ ctx.listData }) { item: ListItemData, index: Int, count: Int ->
                            View {
                                attr {
                                    flexDirection(FlexDirection.ROW)
                                    alignItems(FlexAlign.CENTER)
                                    padding(12f, 12f, 12f, 12f)
                                    backgroundColor(Color(0xFFFAFAFA))
                                    borderRadius(8f)
                                    marginBottom(8f)
                                }
                                Text {
                                    attr {
                                        text("${index + 1}")
                                        fontSize(14f)
                                        fontWeightMedium()
                                        color(Color(0xFF999999))
                                        marginRight(12f)
                                    }
                                }
                                Text {
                                    attr {
                                        text(item.title)
                                        fontSize(14f)
                                        color(Color(0xFF333333))
                                        flex(1f)
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

data class CardData(
    val title: String,
    val value: String,
    val color: Color
)

data class ListItemData(
    val title: String
)