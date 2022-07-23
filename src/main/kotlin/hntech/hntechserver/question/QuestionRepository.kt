package hntech.hntechserver.question

import hntech.hntechserver.domain.Comment
import hntech.hntechserver.domain.Question
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface QuestionRepository: JpaRepository<Question, Long> {

    fun findByIdAndPassword(id: Long, password: String): Question?
}

interface CommentRepository: JpaRepository<Comment, Long> {

}