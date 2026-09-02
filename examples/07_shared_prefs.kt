package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View

/**
 * SharedPreferencesModule 持久化示例（计数跨启动保留）。
 *
 * 关键点：
 * - SharedPreferencesModule 是 Kuikly 内置模块，直接 acquireModule 获取；
 * - 在 created() 里读上次保存的值初始化（getInt 返回 Int?，需 ?: 兜底）；
 * - 在事件里写回（setInt）。
 */
@Page("CountPage")
internal class CountPage : Pager() {

    private var count by observable(0)

    private lateinit var spModule: SharedPreferencesModule

    override fun created() {
        super.created()
        spModule = acquireModule(SharedPreferencesModule.MODULE_NAME)
        count = spModule.getInt("count") ?: 0
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    allCenter()
                }
                Text {
                    attr {
                        text("计数 ${ctx.count}")
                        fontSize(24f)
                        color(Color.BLACK)
                    }
                }
                View {
                    attr {
                        size(160f, 48f)
                        marginTop(32f)
                        borderRadius(8f)
                        backgroundColor(Color(0xFF07C160))
                        allCenter()
                    }
                    event {
                        click {
                            ctx.count += 1
                            ctx.spModule.setInt("count", ctx.count)
                        }
                    }
                    Text {
                        attr {
                            text("+1 并保存")
                            fontSize(16f)
                            color(Color.WHITE)
                        }
                    }
                }
            }
        }
    }
}
