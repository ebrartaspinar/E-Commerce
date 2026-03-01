import api from '@/api/api'
import type {
  ApiResponse,
  PagedResponse,
  Product,
  Category,
  Review,
  ReviewRequest,
  ProductFilter,
} from '@/types'

export const productService = {
  async getProducts(filter: ProductFilter): Promise<ApiResponse<PagedResponse<Product>>> {
    const response = await api.get<ApiResponse<PagedResponse<Product>>>('/products', {
      params: filter,
    })
    return response.data
  },

  async getProduct(id: string): Promise<ApiResponse<Product>> {
    const response = await api.get<ApiResponse<Product>>(`/products/${id}`)
    return response.data
  },

  async getProductBySlug(slug: string): Promise<ApiResponse<Product>> {
    const response = await api.get<ApiResponse<Product>>(`/products/slug/${slug}`)
    return response.data
  },

  async searchProducts(
    query: string,
    page: number = 0,
    size: number = 20
  ): Promise<ApiResponse<PagedResponse<Product>>> {
    const response = await api.get<ApiResponse<PagedResponse<Product>>>('/products/search', {
      params: { q: query, page, size },
    })
    return response.data
  },

  async autocomplete(query: string): Promise<ApiResponse<string[]>> {
    const response = await api.get<ApiResponse<string[]>>('/products/autocomplete', {
      params: { q: query },
    })
    return response.data
  },

  async getCategories(): Promise<ApiResponse<Category[]>> {
    const response = await api.get<ApiResponse<Category[]>>('/categories')
    return response.data
  },

  async getCategory(id: string): Promise<ApiResponse<Category>> {
    const response = await api.get<ApiResponse<Category>>(`/categories/${id}`)
    return response.data
  },

  async getProductReviews(
    productId: string,
    page: number = 0,
    size: number = 10
  ): Promise<ApiResponse<PagedResponse<Review>>> {
    const response = await api.get<ApiResponse<PagedResponse<Review>>>(
      `/products/${productId}/reviews`,
      { params: { page, size } }
    )
    return response.data
  },

  async createReview(productId: string, req: ReviewRequest): Promise<ApiResponse<Review>> {
    const response = await api.post<ApiResponse<Review>>(`/products/${productId}/reviews`, req)
    return response.data
  },
}
