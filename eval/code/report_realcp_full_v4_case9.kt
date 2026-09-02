package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
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

@Page("DataDashboardPage")
internal class DataDashboardPage : Pager() {

    private var cardDataList by observableList<CardData>()
    private var progressValue by observable(0f)
    private var listData by observableList<ListItemData>()

    data class CardData(
        val title: String,
        val value: Int,
        val change: String
    )

    data class ListItemData(
        val title: String,
        val status: String,
        val progress: Int
    )

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flex(1f)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                // 顶部指标卡片区域
                View {
                    attr {
                        flexDirectionRow()
                        padding(16f, 12f, 16f, 12f)
                        alignItemsCenter()
                    }
                    // 卡片1
                    View {
                        attr {
                            flex(1f)
                            backgroundColor(Color.WHITE)
                            borderRadius(12f)
                            padding(12f, 16f, 12f, 16f)
                            alignItemsCenter()
                            marginRight(8f)
                        }
                        Text {
                            attr {
                                text("1,286")
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
                                marginTop(4f)
                            }
                        }
                    }
                    // 卡片2
                    View {
                        attr {
                            flex(1f)
                            backgroundColor(Color.WHITE)
                            borderRadius(12f)
                            padding(12f, 16f, 12f, 16f)
                            alignItemsCenter()
                            marginRight(8f)
                        }
                        Text {
                            attr {
                                text("85.6%")
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
                                marginTop(4f)
                            }
                        }
                    }
                    // 卡片3
                    View {
                        attr {
                            flex(1f)
                            backgroundColor(Color.WHITE)
                            borderRadius(12f)
                            padding(12f, 16f, 12f, 16f)
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                text("¥3,520")
                                fontSize(28f)
                                fontWeightBold()
                                color(Color(0xFFFF9800))
                            }
                        }
                        Text {
                            attr {
                                text("月收入")
                                fontSize(14f)
                                color(Color(0xFF999999))
                                marginTop(4f)
                            }
                        }
                    }
                }
                // 进度条区域
                View {
                    attr {
                        marginLeft(16f)
                        marginRight(16f)
                        backgroundColor(Color.WHITE)
                        borderRadius(12f)
                        padding(16f, 16f, 16f, 16f)
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
                    // 自定义进度条
                    View {
                        attr {
                            height(8f)
                            borderRadius(4f)
                            backgroundColor(Color(0xFFE0E0E0))
                        }
                        View {
                            attr {
                                width(65f)
                                height(8f)
                                borderRadius(4f)
                                backgroundColor(Color(0xFF4CAF50))
                            }
                        }
                    }
                    Text {
                        attr {
                            text("65% 已完成")
                            fontSize(12f)
                            color(Color(0xFF999999))
                            marginTop(6f)
                            textAlignRight()
                        }
                    }
                }
                // 列表区域
                List {
                    attr {
                        flex(1f)
                        marginTop(12f)
                        marginLeft(16f)
                        marginRight(16f)
                        marginBottom(16f)
                    }
                    vforLazy({ ctx.listData }) { item: ListItemData, index: Int, count: Int ->
                        View {
                            attr {
                                flexDirectionRow()
                                alignItemsCenter()
                                backgroundColor(Color.WHITE)
                                borderRadius(10f)
                                padding(14f, 16f, 14f, 16f)
                                marginBottom(8f)
                            }
                            Text {
                                attr {
                                    text(item.title)
                                    fontSize(15f)
                                    fontWeightMedium()
                                    color(Color(0xFF333333))
                                    flex(1f)
                                }
                            }
                            Text {
                                attr {
                                    text(item.progress.toString() + "%")
                                    fontSize(14f)
                                    color(Color(0xFF666666))
                                    textAlignRight()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun created() {
        super.created()
        // 模拟数据加载
        cardDataList.clear()
        cardDataList.add(CardData("总用户", 12846, "+12.5%"))
        cardDataList.add(CardData("活跃用户", 3842, "+8.3%"))
        cardDataList.add(CardData("转化率", 67, "+2.1%"))

        progressValue = 72.5f

        listData.clear()
        listData.add(ListItemData("项目A", "进行中", 85))
        listData.add(ListItemData("项目B", "已完成", 100))
        listData.add(ListItemData("项目C", "待开始", 0))
        listData.add(ListItemData("项目D", "进行中", 45))
    }
}