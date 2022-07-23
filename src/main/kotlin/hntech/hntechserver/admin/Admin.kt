package hntech.hntechserver.admin

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Admin(
    @Id @GeneratedValue
    @Column(name = "admin_id")
    var id: Long? = null,

    var password: String = "",
    var info: String = "",
)