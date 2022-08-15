package hntech.hntechserver.utils.logging

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import org.springframework.stereotype.Component

/**
 * 의존성 주입을 위해 다형성을 활용한다.
 */
interface JsonViewConverters {
    fun convert(obj: ByteArray): String
}

/**
 * 인코딩만 해서 한 줄로 출력해준다.
 */
@Component
class NormalConverter : JsonViewConverters {
    override fun convert(obj: ByteArray): String {
        return String(obj, Charsets.UTF_8)
    }
}

/**
 * JSON 형태로 예쁘게 출력해준다.
 * GSON 의존성이 필요하다.
 */
@Component
class PrettyConverter : JsonViewConverters {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val jsonParser = JsonParser()

    override fun convert(obj: ByteArray): String {
        return gson.toJson(jsonParser.parse(String(obj, Charsets.UTF_8)))
    }

}