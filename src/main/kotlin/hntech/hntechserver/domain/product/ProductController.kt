package hntech.hntechserver.domain.product

import hntech.hntechserver.auth.Auth
import hntech.hntechserver.exception.ValidationException
import hntech.hntechserver.utils.BoolResponse
import io.swagger.annotations.ApiOperation
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/product")
class ProductController(private val productService: ProductService) {
    /**
     * 사용자 모드
     */
    @GetMapping
    fun getAllProducts(
        @RequestParam(name = "category", required = false) categoryName: String?
    ) = ProductListResponse(
            productService.getAllProducts(categoryName).map { ProductSimpleResponse(it) }
        )

    @GetMapping("/{productId}")
    fun getProduct(@PathVariable("productId") id: Long) =
        ProductDetailResponse(productService.getProduct(id))

    /**
     * 관리자 모드
     */
    @Auth
    @PostMapping
    fun createProduct(@Valid @ModelAttribute form: ProductRequestForm,
                      br: BindingResult
    ): ProductDetailResponse {
        if (br.hasErrors()) throw ValidationException(br)
        return ProductDetailResponse(productService.createProduct(form))
    }

    @ApiOperation(
        value = "제품에 등록된 문서 파일의 버튼명 수정",
        notes = "문서 파일들은 파일명과 파일 쌍으로 요청을 보내야 하기 때문에 FormData 로는 파라미터명/버튼명/파일을 동시에 받을 수 없음\n " +
                "그래서 문서 파일들은 제품 생성 후 따로 요청하여 파일 이름을 수정해야 함\n " +
                "productId: 등록된 제품 id / fileId: 해당 제품의 문서 파일 id / form의 filename: 해당 문서 파일의 버튼 이름"
    )
    @Auth
    @PutMapping("/{productId}/file/{fileId}")
    fun updateProductDocFiles(@PathVariable("productId") productId: Long,
                              @PathVariable("fileId") fileId: Long,
                              @RequestBody form: ProductDocFileForm,
    ): ProductDetailResponse =
        ProductDetailResponse(productService.updateProductDocFile(productId, fileId, form))

    @Auth
    @PutMapping("/{productId}")
    fun updateProduct(@PathVariable("productId") id: Long,
                      @Valid @ModelAttribute form: ProductRequestForm,
                      br: BindingResult
    ): ProductDetailResponse {
        if (br.hasErrors()) throw ValidationException(br)
        return ProductDetailResponse(productService.updateProduct(id, form))
    }

    // 제품 순서 변경
    // 맨 뒤로 옮길 때에는 targetProductId를 0으로 요청
    @Auth
    @PatchMapping
    fun updateCategorySequence(@RequestParam("productId") productId: Long,
                               @RequestParam("targetProductId") targetProductId: Long,
    ): ProductListResponse {
        return ProductListResponse(
            productService.updateProductSequence(productId, targetProductId).map { ProductSimpleResponse(it) }
        )
    }

    @Auth
    @DeleteMapping("/{productId}")
    fun deleteProduct(@PathVariable("productId") id: Long) =
        BoolResponse(productService.deleteProduct(id))

    @ApiOperation(
        value = "제품 수정 시 기존 등록된 첨부 파일 삭제",
        notes = "productId: 등록된 제품 id / fileId: 삭제할 파일 id"
    )
    @Auth
    @DeleteMapping("/{productId}/file/{fileId}")
    fun deleteAttachedFile(@PathVariable("productId") productId: Long,
                           @PathVariable("fileId") fileId: Long
    ) = BoolResponse(productService.deleteAttachedFile(productId, fileId))
}