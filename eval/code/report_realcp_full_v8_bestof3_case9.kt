package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*

data class DashboardItem(
    val title: String,
    val value: String
)

@Page("DataDashboardPage")
internal class DataDashboardPage : Pager() {

    private var totalUsers by observable(0)
    private var activeUsers by observable(0)
    private var newUsers by observable(0)
    private var progressValue by observable(0.0f)
    private var listItems by observableList<DashboardItem>()

    override fun created() {
        super.created()
        totalUsers = 1234
        activeUsers = 567
        newUsers = 89
        progressValue = 0.65f
        listItems.addAll(
            listOf(
                DashboardItem("任务A", "已完成"),
                DashboardItem("任务B", "进行中"),
                DashboardItem("任务C", "未开始"),
                DashboardItem("任务D", "已完成"),
                DashboardItem("任务E", "进行中")
            )
        )
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
                // 顶部三个数字指标卡片
                View {
                    attr {
                        flexDirectionRow()
                        padding(16f, 12f, 16f, 8f)
                        alignItemsCenter()
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
                                text(ctx.totalUsers.toString())
                                fontSize(28f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                            }
                        }
                        Text {
                            attr {
                                text("今日访问")
                                fontSize(13f)
                                color(Color(0xFF999999))
                                marginTop(4f)
                            }
                        }
                    }
                    // 卡片2
                    View {
                        attr {
                            flex(1f)
                            marginLeft(4f)
                            marginRight(4f)
                            backgroundColor(Color.WHITE)
                            borderRadius(12f)
                            padding(16f, 12f, 16f, 12f)
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                text(ctx.activeUsers.toString())
                                fontSize(28f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                            }
                        }
                        Text {
                            attr {
                                text("新增用户")
                                fontSize(13f)
                                color(Color(0xFF999999))
                                marginTop(4f)
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
                                text("${ctx.newUsers}%")
                                fontSize(28f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                            }
                        }
                        Text {
                            attr {
                                text("转化率")
                                fontSize(13f)
                                color(Color(0xFF999999))
                                marginTop(4f)
                            }
                        }
                    }
                }
                // 进度条
                View {
                    attr {
                        marginLeft(16f)
                        marginRight(16f)
                        marginTop(8f)
                        marginBottom(8f)
                        padding(16f, 12f, 16f, 12f)
                        backgroundColor(Color.WHITE)
                        borderRadius(12f)
                    }
                    Text {
                        attr {
                            text("任务完成进度")
                            fontSize(15f)
                            fontWeightMedium()
                            color(Color(0xFF333333))
                            marginBottom(12f)
                        }
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 64f, 8f)
                            borderRadius(4f)
                            backgroundColor(Color(0xFFE8E8E8))
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
                            text("${(ctx.progressValue * 100).toInt()}%")
                            fontSize(12f)
                            color(Color(0xFF999999))
                            marginTop(6f)
                            textAlignRight()
                        }
                    }
                }
                // 列表
                List {
                    attr {
                        flex(1f)
                        marginLeft(16f)
                        marginRight(16f)
                        marginTop(8f)
                        marginBottom(16f)
                        backgroundColor(Color.WHITE)
                        borderRadius(12f)
                    }
                    vforLazy({ ctx.listItems }) { item: DashboardItem, index: Int, _: Int ->
                        View {
                            attr {
                                flexDirectionRow()
                                alignItemsCenter()
                                padding(16f, 12f, 16f, 12f)
                                if (index < ctx.listItems.size - 1) {
                                    border(Border(0.5f / pagerData.density, BorderStyle.SOLID, Color(0xFFEEEEEE)))
                                }
                            }
                            Text {
                                attr {
                                    text(item.title)
                                    fontSize(15f)
                                    color(Color(0xFF333333))
                                    flex(1f)
                                }
                            }
                            Text {
                                attr {
                                    text(item.value)
                                    fontSize(15f)
                                    color(Color(0xFF666666))
                                    marginRight(8f)
                                }
                            }
                            Text {
                                attr {
                                    text(">")
                                    fontSize(14f)
                                    color(Color(0xFFCCCCCC))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}