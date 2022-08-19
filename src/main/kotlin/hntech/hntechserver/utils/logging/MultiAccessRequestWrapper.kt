package hntech.hntechserver.utils.logging

import org.apache.tomcat.util.http.fileupload.IOUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

/**
 * 서블릿 request 의 컨텐츠를 두번 액세스 할 수 있도록 래핑하는 클래스
 */
class MultiAccessRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private var contents = ByteArrayOutputStream() // request content 를 여기다 저장한다. (캐싱)

    // 이 메소드를 통해서 request body 의 내용을 inputStream 으로 읽는다.
    override fun getInputStream(): ServletInputStream {
        IOUtils.copy(super.getInputStream(), contents) // request content 를 복사

        // read 를 호출하면 buffer (저장된 내용)을 보내주는 커스텀 ServletInputStream 객체를 생성해서 반환
        return object : ServletInputStream() {
            private var buffer = ByteArrayInputStream(contents.toByteArray())
            override fun read(): Int = buffer.read()
            override fun isFinished(): Boolean = buffer.available() == 0
            override fun isReady(): Boolean = true
            override fun setReadListener(listener: ReadListener?) =
                throw java.lang.RuntimeException("Not implemented")
        }
    }

    // contents 를 byteArray 로 반환
    fun getContents(): ByteArray = this.inputStream.readAllBytes()
}