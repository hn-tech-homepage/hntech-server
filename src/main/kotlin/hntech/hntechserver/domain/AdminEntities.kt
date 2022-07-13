package hntech.hntechserver.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*

@MappedSuperclass
abstract class BaseTimeEntity {
    @CreatedDate
    lateinit var createTime: LocalDateTime
    @LastModifiedDate
    lateinit var updateTime: LocalDateTime
}

@Entity
class Admin(
    @Id @GeneratedValue
    @Column(name = "admin_id")
    var id: Long? = null,

    var password: String = "",
    var info: String = "",
)