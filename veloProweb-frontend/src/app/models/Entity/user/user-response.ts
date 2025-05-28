import { Role } from "../../enum/role"

export interface UserResponse {
    date: string,
    name: string,
    surname: string,
    username: string,
    rut: string,
    email: string,
    status: boolean,
    role: Role
}
