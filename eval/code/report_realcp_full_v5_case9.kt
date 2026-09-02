package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.FlexJustifyContent
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*

data class ListItemData(
    val title: String,
    val value: String
)

object DataDashboardStyles {
    val mainBackground = Color(0xFFF5F5F5)
    val cardBackground = Color(0xFFFFFFFF)
    val cardTitleColor = Color(0xFF666666)
    val cardValueColor = Color(0xFF333333)
    val progressBackground = Color(0xFFE0E0E0)
    val progressFillColor = Color(0xFF07C160)
    val listItemTextColor = Color(0xFF333333)
    
    val cardRadius = 12f
    val cardSpacing = 10f
    val cardPadding = 16f
    val progressHeight = 8f
    val listItemHeight = 50f
    
    val cardTitleSize = 14f
    val cardValueSize = 24f
    val listItemTextSize = 16f
}

@Page("DataDashboardPage")
internal class DataDashboardPage : Pager() {

    private var card1Value by observable("0")
    private var card2Value by observable("0")
    private var card3Value by observable("0")
    private var progressValue by observable(0f)
    private var listItems by observableList<ListItemData>()

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirection(FlexDirection.COLUMN)
                    backgroundColor(DataDashboardStyles.mainBackground)
                }
                View {
                    attr {
                        flexDirection(FlexDirection.ROW)
                        padding(12f, 12f, 12f, 12f)
                        justifyContent(FlexJustifyContent.SPACE_BETWEEN)
                    }
                    View {
                        attr {
                            flex(1f)
                            marginRight(8f)
                            borderRadius(DataDashboardStyles.cardRadius)
                            backgroundColor(DataDashboardStyles.cardBackground)
                            padding(16f, 12f, 16f, 12f)
                        }
                        Text {
                            attr {
                                text("总用户数")
                                fontSize(DataDashboardStyles.cardTitleSize)
                                color(DataDashboardStyles.cardTitleColor)
                            }
                        }
                        Text {
                            attr {
                                text(ctx.card1Value)
                                fontSize(DataDashboardStyles.cardValueSize)
                                fontWeightBold()
                                color(DataDashboardStyles.cardValueColor)
                                marginTop(8f)
                            }
                        }
                    }
                    View {
                        attr {
                            flex(1f)
                            marginRight(8f)
                            borderRadius(DataDashboardStyles.cardRadius)
                            backgroundColor(DataDashboardStyles.cardBackground)
                            padding(16f, 12f, 16f, 12f)
                        }
                        Text {
                            attr {
                                text("活跃用户")
                                fontSize(DataDashboardStyles.cardTitleSize)
                                color(DataDashboardStyles.cardTitleColor)
                            }
                        }
                        Text {
                            attr {
                                text(ctx.card2Value)
                                fontSize(DataDashboardStyles.cardValueSize)
                                fontWeightBold()
                                color(DataDashboardStyles.cardValueColor)
                                marginTop(8f)
                            }
                        }
                    }
                    View {
                        attr {
                            flex(1f)
                            borderRadius(DataDashboardStyles.cardRadius)
                            backgroundColor(DataDashboardStyles.cardBackground)
                            padding(16f, 12f, 16f, 12f)
                        }
                        Text {
                            attr {
                                text("转化率")
                                fontSize(DataDashboardStyles.cardTitleSize)
                                color(DataDashboardStyles.cardTitleColor)
                            }
                        }
                        Text {
                            attr {
                                text(ctx.card3Value)
                                fontSize(DataDashboardStyles.cardValueSize)
                                fontWeightBold()
                                color(DataDashboardStyles.cardValueColor)
                                marginTop(8f)
                            }
                        }
                    }
                }
                View {
                    attr {
                        marginLeft(12f)
                        marginRight(12f)
                        marginTop(8f)
                        marginBottom(8f)
                    }
                    Text {
                        attr {
                            text("任务完成进度")
                            fontSize(14f)
                            color(Color(0xFF666666))
                            marginBottom(8f)
                        }
                    }
                    View {
                        attr {
                            height(DataDashboardStyles.progressHeight)
                            borderRadius(DataDashboardStyles.progressHeight / 2f)
                            backgroundColor(DataDashboardStyles.progressBackground)
                        }
                        View {
                            attr {
                                width(ctx.progressValue * pagerData.pageViewWidth)
                                height(DataDashboardStyles.progressHeight)
                                borderRadius(DataDashboardStyles.progressHeight / 2f)
                                backgroundColor(DataDashboardStyles.progressFillColor)
                            }
                        }
                    }
                }
                List {
                    attr {
                        flex(1f)
                        marginLeft(12f)
                        marginRight(12f)
                        marginTop(8f)
                    }
                    vforLazy({ ctx.listItems }) { item: ListItemData, index: Int, count: Int ->
                        View {
                            attr {
                                flexDirection(FlexDirection.ROW)
                                padding(16f, 12f, 16f, 12f)
                                backgroundColor(DataDashboardStyles.cardBackground)
                                borderRadius(8f)
                                marginBottom(8f)
                            }
                            Text {
                                attr {
                                    text(item.title)
                                    fontSize(DataDashboardStyles.listItemTextSize)
                                    color(DataDashboardStyles.listItemTextColor)
                                    flex(1f)
                                }
                            }
                            Text {
                                attr {
                                    text(item.value)
                                    fontSize(14f)
                                    color(Color(0xFF999999))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}