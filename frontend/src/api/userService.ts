import api from '@/api/api'
import type {
  ApiResponse,
  UserResponse,
  UpdateUserRequest,
  Address,
  AddressRequest,
} from '@/types'

export const userService = {
  async getProfile(): Promise<ApiResponse<UserResponse>> {
    const response = await api.get<ApiResponse<UserResponse>>('/users/me')
    return response.data
  },

  async updateProfile(req: UpdateUserRequest): Promise<ApiResponse<UserResponse>> {
    const response = await api.put<ApiResponse<UserResponse>>('/users/me', req)
    return response.data
  },

  async getAddresses(): Promise<ApiResponse<Address[]>> {
    const response = await api.get<ApiResponse<Address[]>>('/users/me/addresses')
    return response.data
  },

  async addAddress(req: AddressRequest): Promise<ApiResponse<Address>> {
    const response = await api.post<ApiResponse<Address>>('/users/me/addresses', req)
    return response.data
  },

  async updateAddress(id: string, req: AddressRequest): Promise<ApiResponse<Address>> {
    const response = await api.put<ApiResponse<Address>>(`/users/me/addresses/${id}`, req)
    return response.data
  },

  async deleteAddress(id: string): Promise<ApiResponse<void>> {
    const response = await api.delete<ApiResponse<void>>(`/users/me/addresses/${id}`)
    return response.data
  },

  async setDefaultAddress(id: string): Promise<ApiResponse<Address>> {
    const response = await api.patch<ApiResponse<Address>>(`/users/me/addresses/${id}/default`)
    return response.data
  },
}
