export interface UpdateUserDTO {
    username: string,
    email: string,
    currentPassword: string,
    newPassword: string,
    confirmPassword: string
}
