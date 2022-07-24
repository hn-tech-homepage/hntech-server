package hntech.hntechserver.question

import org.springframework.data.jpa.repository.JpaRepository
import java.io.Writer

interface QuestionRepository: JpaRepository<Question, Long> {

    fun findByIdAndPassword(id: Long, password: String): Question?
    fun findByWriter(writer: String): Question?
}

interface CommentRepository: JpaRepository<Comment, Long> {

}