import { Role } from "../enum/role";

export interface UserDTO {
    name: string;
    surname: string;
    username: string;
    rut: string;
    email: string;
    token: string;
    status: boolean;
    role: Role | null;
    date: string;
}
