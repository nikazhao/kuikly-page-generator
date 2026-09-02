package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View

/**
 * 音频 + 震动自定义 Module 示例（电子木鱼核心能力）。
 *
 * 关键点：
 * - Kuikly 无内置音频/震动 API，须自定义 Module（继承 Module，实现 moduleName()）；
 * - 用 asyncToNativeMethod 把方法名 + JSONObject 参数发给 Native 端真正执行；
 * - 必须在 createExternalModules() 注册，在 created() 里 acquireModule 获取。
 */
class AudioHapticsModule : Module() {
    override fun moduleName(): String = "KRAudioHapticsModule"

    companion object {
        const val MODULE_NAME = "KRAudioHapticsModule"
    }

    fun playSound(soundName: String) {
        asyncToNativeMethod(
            "playSound",
            JSONObject().apply { put("soundName", soundName) },
            null
        )
    }

    fun vibrate(durationMs: Int) {
        asyncToNativeMethod(
            "vibrate",
            JSONObject().apply { put("durationMs", durationMs) },
            null
        )
    }
}

@Page("MuyuPage")
internal class MuyuPage : Pager() {

    private var meritCount by observable(0)

    private lateinit var audioModule: AudioHapticsModule

    override fun createExternalModules(): Map<String, Module>? {
        return mapOf(AudioHapticsModule.MODULE_NAME to AudioHapticsModule())
    }

    override fun created() {
        super.created()
        audioModule = acquireModule(AudioHapticsModule.MODULE_NAME)
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    backgroundColor(Color(0xFFF5E6C8))
                    allCenter()
                }
                Text {
                    attr {
                        text("功德 ${ctx.meritCount}")
                        fontSize(28f)
                        color(Color(0xFF5C3A21))
                    }
                }
                View {
                    attr {
                        size(120f, 120f)
                        borderRadius(60f)
                        backgroundColor(Color(0xFF8B5A2B))
                        marginTop(40f)
                        allCenter()
                    }
                    event {
                        click {
                            ctx.meritCount += 1
                            ctx.audioModule.playSound("muyu_knock")
                            ctx.audioModule.vibrate(30)
                        }
                    }
                    Text {
                        attr {
                            text("敲")
                            fontSize(40f)
                            color(Color.WHITE)
                        }
                    }
                }
            }
        }
    }
}
