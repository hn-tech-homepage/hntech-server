package hntech.hntechserver.question

import hntech.hntechserver.question.entity.Comment
import hntech.hntechserver.question.entity.Question
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionRepository: JpaRepository<Question, Long> {

    fun findByIdAndPassword(id: Long, password: String): Question?
}

interface CommentRepository: JpaRepository<Comment, Long> {

}