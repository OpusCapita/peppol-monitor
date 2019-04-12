import request from 'superagent';
import ApiError from './ApiError';

class ApiBase {

    ajax = request;

    getProcesses(pagination, filter) {
        return this.ajax.post(`/peppol-monitor/api/get-processes`).send({pagination, filter}).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    getProcessById(processId) {
        return this.ajax.get(`/peppol-monitor/api/get-process-by-id/${processId}`).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    getProcessByTransmissionId(transmissionId) {
        return this.ajax.get(`/peppol-monitor/api/get-process-by-transmissionId/${transmissionId}`).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    uploadFile(processId, data) {
        return this.ajax.post(`/peppol-monitor/api/upload-file/${processId}`).send(data).then(res => res.body).catch(ApiError.getErrorFromResponse);
    }

    downloadFile(processId) {
        return this.ajax.get(`/peppol-monitor/api/download-file/${processId}`).responseType('blob').catch(ApiError.getErrorFromResponse);
    }

    reprocessMessage(processId) {
        return this.ajax.get(`/peppol-monitor/api/reprocess-message/${processId}`).then(res => res.body).catch(ApiError.getErrorFromResponse);
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
}

export default ApiBase;
