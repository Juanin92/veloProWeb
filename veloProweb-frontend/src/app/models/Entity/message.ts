export interface Message {
    id: number,
    context: string,
    isRead: boolean,
    isDelete: boolean,
    senderUser: number,
    receiverUser: number,
}
