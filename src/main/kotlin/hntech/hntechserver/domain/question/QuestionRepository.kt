package hntech.hntechserver.domain.question

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface QuestionRepository: JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.FAQ = 'false'")
    override fun findAll(pageable: Pageable): Page<Question>

    @Query("SELECT q FROM Question q WHERE q.FAQ = 'true'")
    fun findAllFAQ(pageable: Pageable): Page<Question>

    fun findByIdAndPassword(id: Long, password: String): Question?
    fun findByWriter(writer: String): Question?
}

