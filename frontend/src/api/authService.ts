import api from '@/api/api'
import type { ApiResponse, LoginRequest, RegisterRequest, AuthResponse } from '@/types'

export const authService = {
  async login(req: LoginRequest): Promise<ApiResponse<AuthResponse>> {
    const response = await api.post<ApiResponse<AuthResponse>>('/auth/login', req)
    return response.data
  },

  async register(req: RegisterRequest): Promise<ApiResponse<AuthResponse>> {
    const response = await api.post<ApiResponse<AuthResponse>>('/auth/register', req)
    return response.data
  },

  async refreshToken(refreshToken: string): Promise<ApiResponse<AuthResponse>> {
    const response = await api.post<ApiResponse<AuthResponse>>('/auth/refresh', { refreshToken })
    return response.data
  },

  async logout(): Promise<ApiResponse<void>> {
    const response = await api.post<ApiResponse<void>>('/auth/logout')
    return response.data
  },
}
