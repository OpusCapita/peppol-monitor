import request from 'superagent';
import ApiError from './ApiError';

class ApiBase {

    ajax = request;

    getTransmissionList(pagination, filter) {
        return this.ajax.post(`/peppol-monitor/api/get-transmissions`).send({pagination, filter}).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    getTransmissionById(id) {
        return this.ajax.get(`/peppol-monitor/api/get-transmission-by-id/${id}`).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    getTransmissionByTransmissionId(transmissionId) {
        return this.ajax.get(`/peppol-monitor/api/get-transmission-by-transmissionId/${transmissionId}`).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    uploadFile(transmissionId, userId, data) {
        return this.ajax.post(`/peppol-monitor/api/upload-file/${userId}/${transmissionId}`).send(data).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    downloadFile(transmissionId) {
        return this.ajax.get(`/peppol-monitor/api/download-file/${transmissionId}`).responseType('blob').catch(ApiError.getErrorFromResponse);
    }

    downloadMlr(transmissionId) {
        return this.ajax.get(`/peppol-monitor/api/download-mlr/${transmissionId}`).responseType('blob').catch(ApiError.getErrorFromResponse);
    }

    sendMlr(transmissionId, userId) {
        return this.ajax.get(`/peppol-monitor/api/send-mlr/${userId}/${transmissionId}`).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    sendMlrs(transmissionIds, userId) {
        return this.ajax.get(`/peppol-monitor/api/send-mlrs/${userId}/${transmissionIds}`).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    sendMlrsAdvanced(transmissionList, userId) {
        return this.ajax.post(`/peppol-monitor/api/send-mlrs-advanced/${userId}`).send(transmissionList).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    validateFile(data) {
        return this.ajax.post(`/peppol-validator/api/validate-file`).send(data).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    markAsFixedMessage(transmissionId, userId) {
        return this.ajax.get(`/peppol-monitor/api/mark-fixed-message/${userId}/${transmissionId}`).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    markAsFixedMessages(transmissionIds, userId) {
        return this.ajax.get(`/peppol-monitor/api/mark-fixed-messages/${userId}/${transmissionIds}`).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    markAsFixedMessagesAdvanced(transmissionList, userId) {
        return this.ajax.post(`/peppol-monitor/api/mark-fixed-messages-advanced/${userId}`).send(transmissionList).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    reprocessMessage(transmissionId, userId) {
        return this.ajax.get(`/peppol-monitor/api/reprocess-message/${userId}/${transmissionId}`).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    reprocessMessages(transmissionIds, userId) {
        return this.ajax.get(`/peppol-monitor/api/reprocess-messages/${userId}/${transmissionIds}`).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    reprocessMessagesAdvanced(transmissionList, userId) {
        return this.ajax.post(`/peppol-monitor/api/reprocess-messages-advanced/${userId}`).send(transmissionList).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    getMessageHistory(messageId) {
        return this.ajax.get(`/peppol-monitor/api/get-history/${messageId}`).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    getAccessPoints() {
        return this.ajax.get(`/peppol-monitor/api/get-access-points`).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    getParticipants() {
        return this.ajax.get(`/peppol-monitor/api/get-participants`).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    updateAccessPoint(accessPoint) {
        return this.ajax.post(`/peppol-monitor/api/update-access-point`).send(accessPoint).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    getDocumentTypes() {
        return this.ajax.get(`/peppol-validator/api/get-document-types`).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    getStatus(serviceName) {
        return this.ajax.get(`/${serviceName}/api/health/check`).catch(ApiError.getErrorFromResponse);
    }

    getStatistics(from, to) {
        return this.ajax.get(`/peppol-monitor/api/get-statistics/${from}/${to}`).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }
}

export default ApiBase;
