package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.views.*

@Page("DataDashboardPage")
internal class DataDashboardPage : Pager() {

    private var indicator1 by observable(0)
    private var indicator2 by observable(0)
    private var indicator3 by observable(0)
    private var progressPercent by observable(0f)
    private var listData by observableList<String>()

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                // 顶部：三个指标卡片行
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        flexDirectionRow()
                        paddingTop(16f)
                        paddingBottom(16f)
                        paddingLeft(12f)
                        paddingRight(12f)
                    }
                    // 卡片1
                    View {
                        attr {
                            flex(1f)
                            height(80f)
                            marginLeft(6f)
                            marginRight(6f)
                            borderRadius(12f)
                            backgroundColor(Color(0xFFF0F4FF))
                        }
                        Center {
                            Text {
                                attr {
                                    text("总用户")
                                    fontSize(14f)
                                    color(Color(0xFF666666))
                                }
                            }
                            Text {
                                attr {
                                    text("1,234")
                                    fontSize(24f)
                                    fontWeightBold()
                                    color(Color(0xFF333333))
                                    marginTop(4f)
                                }
                            }
                        }
                    }
                    // 卡片2
                    View {
                        attr {
                            flex(1f)
                            height(80f)
                            marginLeft(6f)
                            marginRight(6f)
                            borderRadius(12f)
                            backgroundColor(Color(0xFFF0FFF4))
                        }
                        Center {
                            Text {
                                attr {
                                    text("活跃用户")
                                    fontSize(14f)
                                    color(Color(0xFF666666))
                                }
                            }
                            Text {
                                attr {
                                    text("856")
                                    fontSize(24f)
                                    fontWeightBold()
                                    color(Color(0xFF333333))
                                    marginTop(4f)
                                }
                            }
                        }
                    }
                    // 卡片3
                    View {
                        attr {
                            flex(1f)
                            height(80f)
                            marginLeft(6f)
                            marginRight(6f)
                            borderRadius(12f)
                            backgroundColor(Color(0xFFFFF4F0))
                        }
                        Center {
                            Text {
                                attr {
                                    text("新增")
                                    fontSize(14f)
                                    color(Color(0xFF666666))
                                }
                            }
                            Text {
                                attr {
                                    text("+128")
                                    fontSize(24f)
                                    fontWeightBold()
                                    color(Color(0xFF333333))
                                    marginTop(4f)
                                }
                            }
                        }
                    }
                }
                // 中间：进度条
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        paddingLeft(16f)
                        paddingRight(16f)
                        paddingTop(8f)
                        paddingBottom(8f)
                    }
                    Text {
                        attr {
                            text("任务完成进度")
                            fontSize(16f)
                            fontWeightMedium()
                            color(Color(0xFF333333))
                            marginBottom(8f)
                        }
                    }
                    // 外层进度条背景
                    View {
                        attr {
                            width(pagerData.pageViewWidth - 32f)
                            height(12f)
                            borderRadius(6f)
                            backgroundColor(Color(0xFFE8E8E8))
                        }
                        // 内层进度条填充
                        View {
                            attr {
                                width((pagerData.pageViewWidth - 32f) * 0.72f)
                                height(12f)
                                borderRadius(6f)
                                backgroundColor(Color(0xFF4A90D9))
                            }
                        }
                    }
                    Text {
                        attr {
                            text("72%")
                            fontSize(12f)
                            color(Color(0xFF999999))
                            marginTop(4f)
                            alignSelfFlexEnd()
                        }
                    }
                }
                // 底部：列表
                List {
                    attr {
                        flex(1f)
                        width(pagerData.pageViewWidth)
                    }
                    vfor({ ctx.listData }) { item: String ->
                        View {
                            attr {
                                width(pagerData.pageViewWidth)
                                height(56f)
                                flexDirectionRow()
                                alignItemsCenter()
                                paddingLeft(16f)
                                paddingRight(16f)
                                border(Border(0.5f / pagerData.density, BorderStyle.SOLID, Color(0xFFEEEEEE)))
                            }
                            Text {
                                attr {
                                    text(item)
                                    fontSize(16f)
                                    color(Color(0xFF333333))
                                    flex(1f)
                                }
                            }
                            Text {
                                attr {
                                    text(item)
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