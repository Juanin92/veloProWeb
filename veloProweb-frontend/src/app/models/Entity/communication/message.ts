import { UserDTO } from "../../DTO/user-dto";

export interface Message {
    id: number,
    context: string,
    created: string;
    read: boolean,
    delete: boolean,
    senderName: string,
}
    
