package hntech.hntechserver.question

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface QuestionRepository: JpaRepository<Question, Long> {
    fun findByIdAndPassword(id: Long, password: String): Optional<Question>
    fun findByWriter(writer: String): Question?
}

interface CommentRepository: JpaRepository<Comment, Long> {
    fun findAllByQuestionId(questionId: Long): List<Comment>
}