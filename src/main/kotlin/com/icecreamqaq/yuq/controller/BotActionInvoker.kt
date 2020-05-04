package com.icecreamqaq.yuq.controller

import com.IceCreamQAQ.Yu.controller.ActionContext
import com.IceCreamQAQ.Yu.controller.router.DefaultActionInvoker
import com.IceCreamQAQ.Yu.controller.router.DefaultRouter
import com.IceCreamQAQ.Yu.controller.router.MethodInvoker
import com.IceCreamQAQ.Yu.entity.DoNone
import com.IceCreamQAQ.Yu.entity.Result
import com.icecreamqaq.yuq.message.Message
import java.lang.Exception

class BotActionInvoker(level: Int) : DefaultActionInvoker(level) {

    override fun invoke(path: String, context: ActionContext): Boolean {
        if (context !is BotActionContext) return false
//        if (super.invoke(path, context)) return true
        var reMessage: Message? = null
        try {
            for (before in befores) {
                val o = before.invoke(context)
                if (o != null) context[toLowerCaseFirstOne(o::class.java.simpleName)] = o
            }
            reMessage = context.buildResult(invoker.invoke(context) ?: return true)
        } catch (e: DoNone) {
        } catch (e: Result) {
            reMessage = context.buildResult(e)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (reMessage != null)
            if (reMessage.qq == null && reMessage.group == null) {
                val message = context.message!!

                reMessage.qq = message.qq
                reMessage.group = message.group
            }
        context.result = reMessage
        return true
    }

    private fun toLowerCaseFirstOne(s: String): String {
        return if (Character.isLowerCase(s[0])) s
        else (StringBuilder()).append(Character.toLowerCase(s[0])).append(s.substring(1)).toString();
    }
}