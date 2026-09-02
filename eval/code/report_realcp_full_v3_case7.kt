package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.base.attr.ImageUri

@Page("WeatherPage")
internal class WeatherPage : Pager() {

    private var cityName by observable("北京")
    private var temperature by observable("25°C")
    private var weatherIcon by observable("weather_sunny")
    private var windForce by observable("3级")
    private var humidity by observable("65%")

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
                // 顶部区域：城市名和温度
                Center {
                    attr {
                        flex(1f)
                    }
                    View {
                        attr {
                            flexDirectionColumn()
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                text(ctx.cityName)
                                fontSize(24f)
                                color(Color(0xFF333333))
                            }
                        }
                        Text {
                            attr {
                                text(ctx.temperature)
                                fontSize(48f)
                                color(Color(0xFF07C160))
                                marginTop(8f)
                            }
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
                            src(ImageUri.pageAssets(ctx.weatherIcon))
                        }
                    }
                }
                // 底部区域：两个卡片
                View {
                    attr {
                        flexDirectionRow()
                        padding(16f, 0f, 16f, 32f)
                    }
                    // 卡片1：湿度
                    View {
                        attr {
                            flex(1f)
                            marginRight(8f)
                            backgroundColor(Color(0xFFFFFFFF))
                            borderRadius(12f)
                            padding(16f)
                            alignItemsCenter()
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
                                fontSize(18f)
                                color(Color(0xFF333333))
                                marginTop(4f)
                            }
                        }
                    }
                    // 卡片2：风速
                    View {
                        attr {
                            flex(1f)
                            marginLeft(8f)
                            backgroundColor(Color(0xFFFFFFFF))
                            borderRadius(12f)
                            padding(16f)
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                text("风速")
                                fontSize(14f)
                                color(Color(0xFF999999))
                            }
                        }
                        Text {
                            attr {
                                text(ctx.windForce)
                                fontSize(18f)
                                color(Color(0xFF333333))
                                marginTop(4f)
                            }
                        }
                    }
                }
            }
        }
    }
}