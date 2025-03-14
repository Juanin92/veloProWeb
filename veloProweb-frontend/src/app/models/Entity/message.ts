import { UserDTO } from "../DTO/user-dto";

export interface Message {
    id: number,
    context: string,
    created: string;
    read: boolean,
    delete: boolean,
    senderUser: UserDTO | null,
    receiverUser: UserDTO | null,
    senderName: string,
}
    
