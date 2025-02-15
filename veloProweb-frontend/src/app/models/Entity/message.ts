export interface Message {
    id: number,
    context: string,
    created: string;
    read: boolean,
    delete: boolean,
    senderUser: number,
    senderName: string,
    receiverUser: number,
}
