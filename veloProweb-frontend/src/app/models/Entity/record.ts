import { User } from "./user";

export interface Record {
    id: number;
    entryDate: string;
    endDate: string;
    actionDate: string;
    action: string;
    comment: string;
    user: User;
}
