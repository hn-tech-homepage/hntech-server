package hntech.hntechserver.comment

import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository: JpaRepository<Comment, Long> {
    fun findAllByQuestionId(questionId: Long): List<Comment>
}