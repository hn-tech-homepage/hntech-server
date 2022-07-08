package hntech.hntechserver

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Board {

    @Id @GeneratedValue
    var id: Long? = null
}