package hntech.hntechserver.product

const val PRODUCT_NOT_FOUND = "해당 제품이 존재하지 않습니다."
const val DUPLICATE_PRODUCT_NAME = "해당 이름의 제품이 이미 존재합니다."

class ProductException(message: String): RuntimeException(message)