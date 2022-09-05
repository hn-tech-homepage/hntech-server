package hntech.hntechserver.common


data class BoolResponse(val result: Boolean = true)

data class ErrorResponse(val cause: String = "", val message: String = "")

data class ErrorListResponse(val errors: List<ErrorResponse>)

