import ApiBase from './ApiBase';

class Message extends ApiBase {

    getMessages(pageNumber = 0) {
        return this.ajax.get(`/peppol-monitor/api/messages/${pageNumber}`).then(res => res.body).catch(this.getErrorFromResponse);
    }

    filterMessages(filterObj) {
        return this.ajax.post('/peppol-monitor/api/messages/filter').send(filterObj).then(res => res.body).catch(this.getErrorFromResponse);
    }

    getMessageByMessageId(messageId) {
        return this.ajax.get(`/peppol-monitor/api/message-by-messageId/${messageId}`).then(res => res.body).catch(this.getErrorFromResponse);
    }

    getMessageHistory(messageId) {
        return this.ajax.get(`/peppol-monitor/api/message-history/${messageId}`).then(res => res.body).catch(this.getErrorFromResponse);
    }
}

export default Message;
