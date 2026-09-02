package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Slider
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*

@Page("DataDashboardPage")
internal class DataDashboardPage : Pager() {

    private var card1Value by observable("0")
    private var card2Value by observable("0")
    private var card3Value by observable("0")
    private var progressValue by observable(0f)
    private var listData by observable(mutableListOf<String>())
    private var isLoading by observable(false)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirectionColumn()
                }
                // 顶部三个数字指标卡片横向行
                View {
                    attr {
                        flexDirectionRow()
                        width(pagerData.pageViewWidth)
                        padding(12f)
                    }
                    // 卡片1
                    View {
                        attr {
                            flex(1f)
                            marginLeft(6f)
                            marginRight(6f)
                            backgroundColor(Color(0xFFF0F4FF))
                            borderRadius(12f)
                            padding(16f)
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                text("总用户")
                                fontSize(14f)
                                color(Color(0xFF666666))
                                marginBottom(8f)
                            }
                        }
                        Text {
                            attr {
                                text(ctx.card1Value)
                                fontSize(28f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                            }
                        }
                    }
                    // 卡片2
                    View {
                        attr {
                            flex(1f)
                            marginLeft(6f)
                            marginRight(6f)
                            backgroundColor(Color(0xFFF0FFF4))
                            borderRadius(12f)
                            padding(16f)
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                text("活跃用户")
                                fontSize(14f)
                                color(Color(0xFF666666))
                                marginBottom(8f)
                            }
                        }
                        Text {
                            attr {
                                text(ctx.card2Value)
                                fontSize(28f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                            }
                        }
                    }
                    // 卡片3
                    View {
                        attr {
                            flex(1f)
                            marginLeft(6f)
                            marginRight(6f)
                            backgroundColor(Color(0xFFFFF4F0))
                            borderRadius(12f)
                            padding(16f)
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                text("新增用户")
                                fontSize(14f)
                                color(Color(0xFF666666))
                                marginBottom(8f)
                            }
                        }
                        Text {
                            attr {
                                text(ctx.card3Value)
                                fontSize(28f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                            }
                        }
                    }
                }
                // 进度条
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        paddingLeft(16f)
                        paddingRight(16f)
                        marginTop(16f)
                    }
                    Text {
                        attr {
                            text("任务完成进度")
                            fontSize(16f)
                            fontWeightMedium()
                            color(Color(0xFF333333))
                            marginBottom(12f)
                        }
                    }
                    Slider {
                        attr {
                            width(pagerData.pageViewWidth - 32f)
                            height(6f)
                            progress(ctx.progressValue)
                            backgroundColor(Color(0xFFE0E0E0))
                            activeColor(Color(0xFF4A90D9))
                            borderRadius(3f)
                        }
                    }
                }
                // 列表
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        flex(1f)
                        marginTop(20f)
                        paddingLeft(16f)
                        paddingRight(16f)
                    }
                    Text {
                        attr {
                            text("最新动态")
                            fontSize(16f)
                            fontWeightMedium()
                            color(Color(0xFF333333))
                            marginBottom(12f)
                        }
                    }
                    List {
                        attr {
                            width(pagerData.pageViewWidth - 32f)
                            flex(1f)
                        }
                        // 列表项1
                        View {
                            attr {
                                width(pagerData.pageViewWidth - 32f)
                                paddingTop(12f)
                                paddingBottom(12f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFEEEEEE)))
                                borderRadius(8f)
                                marginBottom(8f)
                                paddingLeft(12f)
                                paddingRight(12f)
                            }
                            Text {
                                attr {
                                    text("系统升级通知")
                                    fontSize(15f)
                                    fontWeightMedium()
                                    color(Color(0xFF333333))
                                    marginBottom(4f)
                                }
                            }
                            Text {
                                attr {
                                    text("系统将于今晚 23:00 进行升级维护，预计持续 2 小时")
                                    fontSize(13f)
                                    color(Color(0xFF999999))
                                }
                            }
                        }
                        // 列表项2
                        View {
                            attr {
                                width(pagerData.pageViewWidth - 32f)
                                paddingTop(12f)
                                paddingBottom(12f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFEEEEEE)))
                                borderRadius(8f)
                                marginBottom(8f)
                                paddingLeft(12f)
                                paddingRight(12f)
                            }
                            Text {
                                attr {
                                    text("新功能上线")
                                    fontSize(15f)
                                    fontWeightMedium()
                                    color(Color(0xFF333333))
                                    marginBottom(4f)
                                }
                            }
                            Text {
                                attr {
                                    text("数据看板新增导出功能，支持导出为 Excel 格式")
                                    fontSize(13f)
                                    color(Color(0xFF999999))
                                }
                            }
                        }
                        // 列表项3
                        View {
                            attr {
                                width(pagerData.pageViewWidth - 32f)
                                paddingTop(12f)
                                paddingBottom(12f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFEEEEEE)))
                                borderRadius(8f)
                                marginBottom(8f)
                                paddingLeft(12f)
                                paddingRight(12f)
                            }
                            Text {
                                attr {
                                    text("数据更新")
                                    fontSize(15f)
                                    fontWeightMedium()
                                    color(Color(0xFF333333))
                                    marginBottom(4f)
                                }
                            }
                            Text {
                                attr {
                                    text("昨日新增用户 128 人，环比增长 15%")
                                    fontSize(13f)
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