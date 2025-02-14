import { User } from "./user";

export interface Task {
    id: number;
    description: string;
    status: boolean;
    created: string;
    user: User | null;
}
