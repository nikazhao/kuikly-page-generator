package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.base.attr.ImageUri

@Page("WeatherPage")
internal class WeatherPage : Pager() {

    private var cityName by observable("北京")
    private var temperature by observable("26°C")
    private var weatherIcon by observable("☀️")
    private var windPower by observable("3级")
    private var humidity by observable("60%")
    private var isLoading by observable(false)
    private var errorMsg by observable("")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flex(1f)
                    flexDirectionColumn()
                }
                // 顶部区域：城市名 + 温度
                Center {
                    attr {
                        flex(1f)
                    }
                    Text {
                        attr {
                            text(ctx.cityName)
                            fontSize(24f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                        }
                        event {
                            click {
                                ctx.cityName = "广州"
                                ctx.temperature = "30°C"
                                ctx.weatherIcon = "🌧️"
                                ctx.windPower = "4级"
                                ctx.humidity = "80%"
                            }
                        }
                    }
                    Text {
                        attr {
                            text(ctx.temperature)
                            fontSize(48f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                            marginTop(8f)
                        }
                    }
                }
                // 中间区域：天气图标
                Center {
                    attr {
                        flex(1f)
                    }
                    Image {
                        attr {
                            size(120f, 120f)
                            src(ImageUri.commonAssets("weather_sunny.png"))
                        }
                    }
                }
                // 底部区域：风力信息 + 湿度信息
                View {
                    attr {
                        flexDirectionRow()
                        padding(16f, 0f, 16f, 24f)
                    }
                    // 风力信息卡片
                    View {
                        attr {
                            flex(1f)
                            marginRight(8f)
                            borderRadius(12f)
                            backgroundColor(Color(0xFFF5F5F5))
                            padding(16f, 12f, 16f, 12f)
                        }
                        Text {
                            attr {
                                text("风力")
                                fontSize(14f)
                                color(Color(0xFF999999))
                            }
                        }
                        Text {
                            attr {
                                text(ctx.windPower)
                                fontSize(20f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                                marginTop(4f)
                            }
                        }
                    }
                    // 湿度信息卡片
                    View {
                        attr {
                            flex(1f)
                            marginLeft(8f)
                            borderRadius(12f)
                            backgroundColor(Color(0xFFF5F5F5))
                            padding(16f, 12f, 16f, 12f)
                        }
                        Text {
                            attr {
                                text("湿度")
                                fontSize(14f)
                                color(Color(0xFF999999))
                            }
                        }
                        Text {
                            attr {
                                text(ctx.humidity)
                                fontSize(20f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                                marginTop(4f)
                            }
                        }
                    }
                }
                // 刷新按钮
                View {
                    attr {
                        size(48f, 48f)
                        backgroundColor(Color(0xFF2196F3))
                        borderRadius(24f)
                        allCenter()
                        marginTop(16f)
                        marginBottom(24f)
                    }
                    event {
                        click {
                            ctx.isLoading = true
                            ctx.cityName = "上海"
                            ctx.temperature = "28°C"
                            ctx.weatherIcon = "⛅"
                            ctx.windPower = "2级"
                            ctx.humidity = "65%"
                            ctx.isLoading = false
                        }
                    }
                    Text {
                        attr {
                            text("刷新")
                            fontSize(14f)
                            color(Color(0xFFFFFFFF))
                        }
                    }
                }
            }
        }
    }

    override fun created() {
        super.created()
        isLoading = true
        cityName = "北京"
        temperature = "26°C"
        weatherIcon = "☀️"
        windPower = "3级"
        humidity = "60%"
        isLoading = false
    }
}