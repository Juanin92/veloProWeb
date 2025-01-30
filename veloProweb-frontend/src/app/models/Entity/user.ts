import { Role } from "../enum/role";

export interface User {
    id: number;
    date: string;
    name: string;
    surname: string;
    username: string;
    rut: string;
    email: string;
    password: string;
    token: string;
    status: boolean;
    role: Role;
}
