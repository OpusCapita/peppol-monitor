import request from 'superagent';
import ApiError from './ApiError';

class ApiBase {
    ajax = request;

    getErrorFromResponse(e) {
        if (e)
            throw new ApiError((e.response && e.response.body && e.response.body.message) || e.body || e.message, e.response.status);

        throw new Error('An unknown error occured.');
    }

    getProcesses(pageNumber = 0) {
        return this.ajax.get(`/peppol-monitor/api/get-processes/${pageNumber}`).then(res => res.body).catch(this.getErrorFromResponse);
    }

    filterProcesses(filterObj) {
        return this.ajax.post('/peppol-monitor/api/filter-processes').send(filterObj).then(res => res.body).catch(this.getErrorFromResponse);
    }

    getProcessById(id) {
        return this.ajax.get(`/peppol-monitor/api/get-process-by-id/${id}`).then(res => res.body).catch(this.getErrorFromResponse);
    }

    getProcessByTransmissionId(transmissionId) {
        return this.ajax.get(`/peppol-monitor/api/get-process-by-transmissionId/${transmissionId}`).then(res => res.body).catch(this.getErrorFromResponse);
    }

    getMessageHistory(messageId) {
        return this.ajax.get(`/peppol-monitor/api/get-history/${messageId}`).then(res => res.body).catch(this.getErrorFromResponse);
    }
}

export default ApiBase;
