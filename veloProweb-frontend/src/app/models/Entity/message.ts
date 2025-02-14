export interface Message {
    id: number,
    context: string,
    read: boolean,
    delete: boolean,
    senderUser: number,
    receiverUser: number,
}
