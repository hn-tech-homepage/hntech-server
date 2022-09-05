package hntech.hntechserver.domain.question.model

import hntech.hntechserver.common.BaseTimeEntity
import javax.persistence.*

@Entity
class Comment (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    var question: Question,

    var writer: String = "",
    var sequence: Int = 0,
    var content: String = "",

    ) : BaseTimeEntity() {

    fun update(content: String) {
        this.content = content
    }
}