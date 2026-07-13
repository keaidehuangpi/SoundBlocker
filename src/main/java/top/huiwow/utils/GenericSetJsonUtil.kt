package top.huiwow.utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


object GenericSetJsonUtil {
    val jsonConfig = Json { prettyPrint = true }

    // 使用 inline 和 reified 关键字，让 Kotlin 在运行时能获取到泛型 T 的实际类型
    inline fun <reified T> toJson(set: HashSet<T>): String {
        return jsonConfig.encodeToString(set)
    }

    inline fun <reified T> fromJson(jsonStr: String): HashSet<T> {
        if (jsonStr.isBlank()) return hashSetOf()
        return try {
            jsonConfig.decodeFromString(jsonStr)
        } catch (e: Exception) {
            e.printStackTrace()
            hashSetOf()
        }
    }
}
