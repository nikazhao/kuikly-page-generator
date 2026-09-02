package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.views.*

@Page("WeatherPage")
internal class WeatherPage : Pager() {

    private var cityName by observable("北京")
    private var temperature by observable("26°C")
    private var weatherIcon by observable("☀️")
    private var windSpeed by observable("3级")
    private var humidity by observable("60%")
    private var windDirection by observable("东南风")
    private var airQuality by observable("良")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flex(1f)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                // 顶部区域：城市和温度
                Center {
                    View {
                        attr {
                            flexDirectionColumn()
                            alignItemsCenter()
                            paddingTop(60f)
                            paddingBottom(20f)
                        }
                        Text {
                            attr {
                                text(ctx.cityName)
                                fontSize(24f)
                                color(Color(0xFF333333))
                                fontWeightBold()
                            }
                        }
                        Text {
                            attr {
                                text(ctx.temperature)
                                fontSize(48f)
                                color(Color(0xFF333333))
                                fontWeightBold()
                                marginTop(8f)
                            }
                        }
                    }
                }
                // 中间区域：天气图标
                Center {
                    Image {
                        attr {
                            size(120f, 120f)
                            src(ImageUri.pageAssets("weather_sunny"))
                            marginTop(20f)
                            marginBottom(20f)
                        }
                    }
                }
                // 底部区域：风力、湿度等信息卡片
                View {
                    attr {
                        flex(1f)
                        flexDirectionColumn()
                        paddingLeft(16f)
                        paddingRight(16f)
                        paddingTop(10f)
                    }
                    // 风力卡片
                    View {
                        attr {
                            flexDirectionRow()
                            alignItemsCenter()
                            backgroundColor(Color.WHITE)
                            borderRadius(12f)
                            padding(16f)
                            marginBottom(12f)
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFE8E8E8)))
                        }
                        Text {
                            attr {
                                text("风力")
                                fontSize(16f)
                                color(Color(0xFF666666))
                                flex(1f)
                            }
                        }
                        Text {
                            attr {
                                text(ctx.windSpeed)
                                fontSize(16f)
                                color(Color(0xFF333333))
                                fontWeightMedium()
                            }
                        }
                    }
                    // 湿度卡片
                    View {
                        attr {
                            flexDirectionRow()
                            alignItemsCenter()
                            backgroundColor(Color.WHITE)
                            borderRadius(12f)
                            padding(16f)
                            marginBottom(12f)
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFE8E8E8)))
                        }
                        Text {
                            attr {
                                text("湿度")
                                fontSize(16f)
                                color(Color(0xFF666666))
                                flex(1f)
                            }
                        }
                        Text {
                            attr {
                                text(ctx.humidity)
                                fontSize(16f)
                                color(Color(0xFF333333))
                                fontWeightMedium()
                            }
                        }
                    }
                    // 空气质量卡片
                    View {
                        attr {
                            flexDirectionRow()
                            alignItemsCenter()
                            backgroundColor(Color.WHITE)
                            borderRadius(12f)
                            padding(16f)
                            marginBottom(12f)
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFE8E8E8)))
                        }
                        Text {
                            attr {
                                text("空气质量")
                                fontSize(16f)
                                color(Color(0xFF666666))
                                flex(1f)
                            }
                        }
                        Text {
                            attr {
                                text(ctx.airQuality)
                                fontSize(16f)
                                color(Color(0xFF4CAF50))
                                fontWeightMedium()
                            }
                        }
                    }
                    // 紫外线卡片
                    View {
                        attr {
                            flexDirectionRow()
                            alignItemsCenter()
                            backgroundColor(Color.WHITE)
                            borderRadius(12f)
                            padding(16f)
                            marginBottom(12f)
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFE8E8E8)))
                        }
                        Text {
                            attr {
                                text("紫外线")
                                fontSize(16f)
                                color(Color(0xFF666666))
                                flex(1f)
                            }
                        }
                        Text {
                            attr {
                                text("中等")
                                fontSize(16f)
                                color(Color(0xFFFF9800))
                                fontWeightMedium()
                            }
                        }
                    }
                }
            }
            // 点击刷新天气数据
            event {
                click { params ->
                    ctx.cityName = "上海"
                    ctx.temperature = "28°C"
                    ctx.weatherIcon = "⛅"
                    ctx.windSpeed = "4级"
                    ctx.humidity = "55%"
                    ctx.windDirection = "南风"
                    ctx.airQuality = "优"
                }
            }
        }
    }
}