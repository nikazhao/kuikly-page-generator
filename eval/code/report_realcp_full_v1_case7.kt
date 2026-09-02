package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*

@Page("WeatherPage")
internal class WeatherPage : Pager() {

    private var cityName by observable("")
    private var temperature by observable("")
    private var weatherIcon by observable("")
    private var windPower by observable("")
    private var humidity by observable("")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                // 顶部导航栏
                View {
                    attr {
                        size(pagerData.pageViewWidth, 56f)
                        backgroundColor(Color.WHITE)
                        alignItemsCenter()
                        flexDirectionRow()
                        paddingTop(pagerData.safeAreaInsets.top)
                    }
                    Text {
                        attr {
                            text("Weather")
                            fontSize(20f)
                            fontWeightBold()
                            marginLeft(16f)
                            color(Color(0xFF333333))
                        }
                    }
                    Text {
                        attr {
                            text("Beijing")
                            fontSize(14f)
                            color(Color(0xFF666666))
                            marginLeft(12f)
                        }
                    }
                }
                // 主要天气信息
                View {
                    attr {
                        size(pagerData.pageViewWidth, 200f)
                        alignItemsCenter()
                        justifyContentCenter()
                        backgroundColor(Color(0xFF4A90D9))
                    }
                    Text {
                        attr {
                            text("25°C")
                            fontSize(64f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                    Text {
                        attr {
                            text("Sunny")
                            fontSize(20f)
                            color(Color.WHITE)
                            marginTop(8f)
                        }
                    }
                    Text {
                        attr {
                            text("Feels like 27°C | Humidity 45%")
                            fontSize(14f)
                            color(Color(0xCCFFFFFF))
                            marginTop(4f)
                        }
                    }
                }
                // 今日详情
                View {
                    attr {
                        size(pagerData.pageViewWidth, 120f)
                        flexDirectionRow()
                        backgroundColor(Color.WHITE)
                        paddingLeft(16f)
                        paddingRight(16f)
                        alignItemsCenter()
                        justifyContentSpaceAround()
                    }
                    View {
                        attr {
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                text("Sunrise")
                                fontSize(12f)
                                color(Color(0xFF999999))
                            }
                        }
                        Text {
                            attr {
                                text("06:15")
                                fontSize(16f)
                                fontWeightMedium()
                                color(Color(0xFF333333))
                                marginTop(4f)
                            }
                        }
                    }
                    View {
                        attr {
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                text("Sunset")
                                fontSize(12f)
                                color(Color(0xFF999999))
                            }
                        }
                        Text {
                            attr {
                                text("18:45")
                                fontSize(16f)
                                fontWeightMedium()
                                color(Color(0xFF333333))
                                marginTop(4f)
                            }
                        }
                    }
                    View {
                        attr {
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                text("Wind")
                                fontSize(12f)
                                color(Color(0xFF999999))
                            }
                        }
                        Text {
                            attr {
                                text("12 km/h")
                                fontSize(16f)
                                fontWeightMedium()
                                color(Color(0xFF333333))
                                marginTop(4f)
                            }
                        }
                    }
                    View {
                        attr {
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                text("UV Index")
                                fontSize(12f)
                                color(Color(0xFF999999))
                            }
                        }
                        Text {
                            attr {
                                text("7 High")
                                fontSize(16f)
                                fontWeightMedium()
                                color(Color(0xFF333333))
                                marginTop(4f)
                            }
                        }
                    }
                }
                // 每小时预报
                View {
                    attr {
                        size(pagerData.pageViewWidth, 160f)
                        backgroundColor(Color.WHITE)
                        marginTop(8f)
                    }
                    Text {
                        attr {
                            text("Hourly Forecast")
                            fontSize(16f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                            marginLeft(16f)
                            marginTop(12f)
                        }
                    }
                    Scroller {
                        attr {
                            size(pagerData.pageViewWidth, 120f)
                            flexDirectionRow()
                            paddingLeft(8f)
                            paddingRight(8f)
                            marginTop(8f)
                        }
                        // 每小时项
                        View {
                            attr {
                                size(70f, 100f)
                                alignItemsCenter()
                                justifyContentCenter()
                                marginLeft(4f)
                                marginRight(4f)
                                borderRadius(12f)
                                backgroundColor(Color(0xFFF0F4FF))
                            }
                            Text {
                                attr {
                                    text("Now")
                                    fontSize(12f)
                                    color(Color(0xFF666666))
                                }
                            }
                            Text {
                                attr {
                                    text("☀️")
                                    fontSize(24f)
                                    marginTop(4f)
                                }
                            }
                            Text {
                                attr {
                                    text("25°")
                                    fontSize(14f)
                                    fontWeightMedium()
                                    color(Color(0xFF333333))
                                    marginTop(4f)
                                }
                            }
                        }
                        View {
                            attr {
                                size(70f, 100f)
                                alignItemsCenter()
                                justifyContentCenter()
                                marginLeft(4f)
                                marginRight(4f)
                                borderRadius(12f)
                                backgroundColor(Color(0xFFF0F4FF))
                            }
                            Text {
                                attr {
                                    text("14:00")
                                    fontSize(12f)
                                    color(Color(0xFF666666))
                                }
                            }
                            Text {
                                attr {
                                    text("⛅")
                                    fontSize(24f)
                                    marginTop(4f)
                                }
                            }
                            Text {
                                attr {
                                    text("26°")
                                    fontSize(14f)
                                    fontWeightMedium()
                                    color(Color(0xFF333333))
                                    marginTop(4f)
                                }
                            }
                        }
                        View {
                            attr {
                                size(70f, 100f)
                                alignItemsCenter()
                                justifyContentCenter()
                                marginLeft(4f)
                                marginRight(4f)
                                borderRadius(12f)
                                backgroundColor(Color(0xFFF0F4FF))
                            }
                            Text {
                                attr {
                                    text("15:00")
                                    fontSize(12f)
                                    color(Color(0xFF666666))
                                }
                            }
                            Text {
                                attr {
                                    text("☁️")
                                    fontSize(24f)
                                    marginTop(4f)
                                }
                            }
                            Text {
                                attr {
                                    text("24°")
                                    fontSize(14f)
                                    fontWeightMedium()
                                    color(Color(0xFF333333))
                                    marginTop(4f)
                                }
                            }
                        }
                        View {
                            attr {
                                size(70f, 100f)
                                alignItemsCenter()
                                justifyContentCenter()
                                marginLeft(4f)
                                marginRight(4f)
                                borderRadius(12f)
                                backgroundColor(Color(0xFFF0F4FF))
                            }
                            Text {
                                attr {
                                    text("16:00")
                                    fontSize(12f)
                                    color(Color(0xFF666666))
                                }
                            }
                            Text {
                                attr {
                                    text("🌧️")
                                    fontSize(24f)
                                    marginTop(4f)
                                }
                            }
                            Text {
                                attr {
                                    text("22°")
                                    fontSize(14f)
                                    fontWeightMedium()
                                    color(Color(0xFF333333))
                                    marginTop(4f)
                                }
                            }
                        }
                        View {
                            attr {
                                size(70f, 100f)
                                alignItemsCenter()
                                justifyContentCenter()
                                marginLeft(4f)
                                marginRight(4f)
                                borderRadius(12f)
                                backgroundColor(Color(0xFFF0F4FF))
                            }
                            Text {
                                attr {
                                    text("17:00")
                                    fontSize(12f)
                                    color(Color(0xFF666666))
                                }
                            }
                            Text {
                                attr {
                                    text("🌧️")
                                    fontSize(24f)
                                    marginTop(4f)
                                }
                            }
                            Text {
                                attr {
                                    text("21°")
                                    fontSize(14f)
                                    fontWeightMedium()
                                    color(Color(0xFF333333))
                                    marginTop(4f)
                                }
                            }
                        }
                    }
                }
                // 7天预报
                View {
                    attr {
                        size(pagerData.pageViewWidth, 300f)
                        backgroundColor(Color.WHITE)
                        marginTop(8f)
                    }
                    Text {
                        attr {
                            text("7-Day Forecast")
                            fontSize(16f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                            marginLeft(16f)
                            marginTop(12f)
                        }
                    }
                    // 每日项
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 32f, 40f)
                            flexDirectionRow()
                            alignItemsCenter()
                            marginLeft(16f)
                            marginRight(16f)
                            marginTop(8f)
                        }
                        Text {
                            attr {
                                text("Today")
                                fontSize(14f)
                                color(Color(0xFF333333))
                                width(60f)
                            }
                        }
                        Text {
                            attr {
                                text("☀️")
                                fontSize(18f)
                                marginLeft(8f)
                            }
                        }
                        Text {
                            attr {
                                text("25°")
                                fontSize(14f)
                                fontWeightMedium()
                                color(Color(0xFF333333))
                                marginLeft(12f)
                            }
                        }
                        View {
                            attr {
                                flex(1f)
                                height(6f)
                                borderRadius(3f)
                                backgroundColor(Color(0xFFE0E0E0))
                                marginLeft(8f)
                                marginRight(8f)
                            }
                            View {
                                attr {
                                    width(70f)
                                    height(6f)
                                    borderRadius(3f)
                                    backgroundColor(Color(0xFF4A90D9))
                                }
                            }
                        }
                        Text {
                            attr {
                                text("18°")
                                fontSize(14f)
                                color(Color(0xFF999999))
                            }
                        }
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 32f, 40f)
                            flexDirectionRow()
                            alignItemsCenter()
                            marginLeft(16f)
                            marginRight(16f)
                            marginTop(4f)
                        }
                        Text {
                            attr {
                                text("Mon")
                                fontSize(14f)
                                color(Color(0xFF333333))
                                width(60f)
                            }
                        }
                        Text {
                            attr {
                                text("⛅")
                                fontSize(18f)
                                marginLeft(8f)
                            }
                        }
                        Text {
                            attr {
                                text("24°")
                                fontSize(14f)
                                fontWeightMedium()
                                color(Color(0xFF333333))
                                marginLeft(12f)
                            }
                        }
                        View {
                            attr {
                                flex(1f)
                                height(6f)
                                borderRadius(3f)
                                backgroundColor(Color(0xFFE0E0E0))
                                marginLeft(8f)
                                marginRight(8f)
                            }
                            View {
                                attr {
                                    width(50f)
                                    height(6f)
                                    borderRadius(3f)
                                    backgroundColor(Color(0xFF4A90D9))
                                }
                            }
                        }
                        Text {
                            attr {
                                text("17°")
                                fontSize(14f)
                                color(Color(0xFF999999))
                            }
                        }
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 32f, 40f)
                            flexDirectionRow()
                            alignItemsCenter()
                            marginLeft(16f)
                            marginRight(16f)
                            marginTop(4f)
                        }
                        Text {
                            attr {
                                text("Tue")
                                fontSize(14f)
                                color(Color(0xFF333333))
                                width(60f)
                            }
                        }
                        Text {
                            attr {
                                text("☁️")
                                fontSize(18f)
                                marginLeft(8f)
                            }
                        }
                        Text {
                            attr {
                                text("22°")
                                fontSize(14f)
                                fontWeightMedium()
                                color(Color(0xFF333333))
                                marginLeft(12f)
                            }
                        }
                        View {
                            attr {
                                flex(1f)
                                height(6f)
                                borderRadius(3f)
                                backgroundColor(Color(0xFFE0E0E0))
                                marginLeft(8f)
                                marginRight(8f)
                            }
                            View {
                                attr {
                                    width(40f)
                                    height(6f)
                                    borderRadius(3f)
                                    backgroundColor(Color(0xFF4A90D9))
                                }
                            }
                        }
                        Text {
                            attr {
                                text("16°")
                                fontSize(14f)
                                color(Color(0xFF999999))
                            }
                        }
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 32f, 40f)
                            flexDirectionRow()
                            alignItemsCenter()
                            marginLeft(16f)
                            marginRight(16f)
                            marginTop(4f)
                        }
                        Text {
                            attr {
                                text("Wed")
                                fontSize(14f)
                                color(Color(0xFF333333))
                                width(60f)
                            }
                        }
                        Text {
                            attr {
                                text("🌧️")
                                fontSize(18f)
                                marginLeft(8f)
                            }
                        }
                        Text {
                            attr {
                                text("20°")
                                fontSize(14f)
                                fontWeightMedium()
                                color(Color(0xFF333333))
                                marginLeft(12f)
                            }
                        }
                        View {
                            attr {
                                flex(1f)
                                height(6f)
                                borderRadius(3f)
                                backgroundColor(Color(0xFFE0E0E0))
                                marginLeft(8f)
                                marginRight(8f)
                            }
                            View {
                                attr {
                                    width(30f)
                                    height(6f)
                                    borderRadius(3f)
                                    backgroundColor(Color(0xFF4A90D9))
                                }
                            }
                        }
                        Text {
                            attr {
                                text("15°")
                                fontSize(14f)
                                color(Color(0xFF999999))
                            }
                        }
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 32f, 40f)
                            flexDirectionRow()
                            alignItemsCenter()
                            marginLeft(16f)
                            marginRight(16f)
                            marginTop(4f)
                        }
                        Text {
                            attr {
                                text("Thu")
                                fontSize(14f)
                                color(Color(0xFF333333))
                                width(60f)
                            }
                        }
                        Text {
                            attr {
                                text("☀️")
                                fontSize(18f)
                                marginLeft(8f)
                            }
                        }
                        Text {
                            attr {
                                text("23°")
                                fontSize(14f)
                                fontWeightMedium()
                                color(Color(0xFF333333))
                                marginLeft(12f)
                            }
                        }
                        View {
                            attr {
                                flex(1f)
                                height(6f)
                                borderRadius(3f)
                                backgroundColor(Color(0xFFE0E0E0))
                                marginLeft(8f)
                                marginRight(8f)
                            }
                            View {
                                attr {
                                    width(60f)
                                    height(6f)
                                    borderRadius(3f)
                                    backgroundColor(Color(0xFF4A90D9))
                                }
                            }
                        }
                        Text {
                            attr {
                                text("17°")
                                fontSize(14f)
                                color(Color(0xFF999999))
                            }
                        }
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 32f, 40f)
                            flexDirectionRow()
                            alignItemsCenter()
                            marginLeft(16f)
                            marginRight(16f)
                            marginTop(4f)
                        }
                        Text {
                            attr {
                                text("Fri")
                                fontSize(14f)
                                color(Color(0xFF333333))
                                width(60f)
                            }
                        }
                        Text {
                            attr {
                                text("☀️")
                                fontSize(18f)
                                marginLeft(8f)
                            }
                        }
                        Text {
                            attr {
                                text("26°")
                                fontSize(14f)
                                fontWeightMedium()
                                color(Color(0xFF333333))
                                marginLeft(12f)
                            }
                        }
                        View {
                            attr {
                                flex(1f)
                                height(6f)
                                borderRadius(3f)
                                backgroundColor(Color(0xFFE0E0E0))
                                marginLeft(8f)
                                marginRight(8f)
                            }
                            View {
                                attr {
                                    width(80f)
                                    height(6f)
                                    borderRadius(3f)
                                    backgroundColor(Color(0xFF4A90D9))
                                }
                            }
                        }
                        Text {
                            attr {
                                text("19°")
                                fontSize(14f)
                                color(Color(0xFF999999))
                            }
                        }
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 32f, 40f)
                            flexDirectionRow()
                            alignItemsCenter()
                            marginLeft(16f)
                            marginRight(16f)
                            marginTop(4f)
                        }
                        Text {
                            attr {
                                text("Sat")
                                fontSize(14f)
                                color(Color(0xFF333333))
                                width(60f)
                            }
                        }
                        Text {
                            attr {
                                text("⛅")
                                fontSize(18f)
                                marginLeft(8f)
                            }
                        }
                        Text {
                            attr {
                                text("24°")
                                fontSize(14f)
                                fontWeightMedium()
                                color(Color(0xFF333333))
                                marginLeft(12f)
                            }
                        }
                        View {
                            attr {
                                flex(1f)
                                height(6f)
                                borderRadius(3f)
                                backgroundColor(Color(0xFFE0E0E0))
                                marginLeft(8f)
                                marginRight(8f)
                            }
                            View {
                                attr {
                                    width(55f)
                                    height(6f)
                                    borderRadius(3f)
                                    backgroundColor(Color(0xFF4A90D9))
                                }
                            }
                        }
                        Text {
                            attr {
                                text("18°")
                                fontSize(14f)
                                color(Color(0xFF999999))
                            }
                        }
                    }
                }
                // 底部安全区域
                View {
                    attr {
                        size(pagerData.pageViewWidth, pagerData.safeAreaInsets.bottom)
                        backgroundColor(Color.WHITE)
                    }
                }
            }
        }
    }
}