package hntech.hntechserver.utils.scheduler

import hntech.hntechserver.question.EMAIL_SEND_ERROR
import hntech.hntechserver.question.EmailException
import hntech.hntechserver.question.QuestionService
import hntech.hntechserver.question.dto.QuestionSimpleResponse
import hntech.hntechserver.question.dto.convertDto
import hntech.hntechserver.utils.config.EMAIL_ADDR
import hntech.hntechserver.utils.logger
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class MailService(
    private val mailSender: JavaMailSender,
    private val questionService: QuestionService
) {
    private val log = logger()
    fun testLog() = log.info("테스트")

    fun sendMail() {
        try {
            val mail = mailSender.createMimeMessage()
            val mailHelper = MimeMessageHelper(mail, false, "UTF-8")
            val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
            val questions = questionService.findAllQuestions().map {
                convertDto(it, false) as QuestionSimpleResponse
            }
            var text: String = ""

            mailHelper.setFrom(EMAIL_ADDR, "HNTECH 웹페이지")
            mailHelper.setTo(EMAIL_ADDR)
            mailHelper.setSubject("HNTECH 웹페이지: $now 신규 등록된 문의사항")
            text += "<html>" +
                    "<body>" +
                    "<h3>HNTECH 웹페이지에 금일 등록된 문의사항 목록입니다.</h3>"
            questions.forEach {
                text += it.writer + "님 : " + it.title + "<br/>"
            }
            text += "</body>" +
                    "</html>"

            mailHelper.setText(text, true)

            mailSender.send(mail)
        } catch (e: Exception) {
            log.debug(e.message)
            throw EmailException(EMAIL_SEND_ERROR)
        }
    }
}