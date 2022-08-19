package hntech.hntechserver.admin

const val ADMIN_PASSWORD_VALID_FAIL = "현재 비밀번호가 틀립니다."

class AdminException(message: String) : RuntimeException(message)