import { Role } from "../../enum/role";

export interface UserForm {
    name: string,
    surname: string,
    username: string,
    rut: string,
    email: string,
    role: Role
}
