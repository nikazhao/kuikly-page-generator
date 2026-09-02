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
    private var weatherIcon by observable("sunny")
    private var windSpeed by observable("3级")
    private var humidity by observable("60%")

    override fun body(): ViewBuilder {
        val pagerData = this.pagerData
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                    paddingTop(20f)
                    paddingBottom(20f)
                }
                // 顶部区域：城市名和温度水平排列
                Center {
                    attr {
                        width(pagerData.pageViewWidth)
                        height(80f)
                    }
                    View {
                        attr {
                            flexDirectionRow()
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                text(this@WeatherPage.cityName)
                                fontSize(18f)
                                color(Color(0xFF333333))
                                fontWeightSemiBold()
                                marginRight(12f)
                            }
                        }
                        Text {
                            attr {
                                text(this@WeatherPage.temperature)
                                fontSize(48f)
                                color(Color(0xFF07C160))
                                fontWeightBold()
                                marginTop(8f)
                            }
                        }
                    }
                }
                // 中间区域：天气图标
                Center {
                    attr {
                        width(pagerData.pageViewWidth)
                        flex(1f)
                    }
                    Image {
                        attr {
                            size(120f, 120f)
                            src(ImageUri.pageAssets("weather_sunny"))
                        }
                    }
                }
                // 底部区域：多个卡片水平排列
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        height(120f)
                        flexDirectionRow()
                        alignItemsCenter()
                        justifyContentSpaceEvenly()
                        paddingBottom(pagerData.safeAreaInsets.bottom)
                    }
                    // 卡片1：湿度
                    View {
                        attr {
                            width(100f)
                            height(90f)
                            backgroundColor(Color(0xFFFFFFFF))
                            borderRadius(12f)
                            alignItemsCenter()
                            justifyContentCenter()
                            padding(16f)
                            marginTop(12f)
                        }
                        Text {
                            attr {
                                text("湿度")
                                fontSize(14f)
                                color(Color(0xFF999999))
                                marginBottom(8f)
                            }
                        }
                        Text {
                            attr {
                                text(this@WeatherPage.humidity)
                                fontSize(20f)
                                color(Color(0xFF333333))
                                fontWeightSemiBold()
                                marginTop(4f)
                            }
                        }
                    }
                    // 卡片2：风速
                    View {
                        attr {
                            width(100f)
                            height(90f)
                            backgroundColor(Color(0xFFFFFFFF))
                            borderRadius(12f)
                            alignItemsCenter()
                            justifyContentCenter()
                            padding(16f)
                            marginTop(12f)
                        }
                        Text {
                            attr {
                                text("风速")
                                fontSize(14f)
                                color(Color(0xFF999999))
                                marginBottom(8f)
                            }
                        }
                        Text {
                            attr {
                                text(this@WeatherPage.windSpeed)
                                fontSize(20f)
                                color(Color(0xFF333333))
                                fontWeightSemiBold()
                                marginTop(4f)
                            }
                        }
                    }
                    // 卡片3：空气质量
                    View {
                        attr {
                            width(100f)
                            height(90f)
                            backgroundColor(Color(0xFFFFFFFF))
                            borderRadius(12f)
                            alignItemsCenter()
                            justifyContentCenter()
                            padding(16f)
                            marginTop(12f)
                        }
                        Text {
                            attr {
                                text("空气质量")
                                fontSize(14f)
                                color(Color(0xFF999999))
                                marginBottom(8f)
                            }
                        }
                        Text {
                            attr {
                                text("良")
                                fontSize(20f)
                                color(Color(0xFF333333))
                                fontWeightSemiBold()
                                marginTop(4f)
                            }
                        }
                    }
                }
            }
        }
    }
}