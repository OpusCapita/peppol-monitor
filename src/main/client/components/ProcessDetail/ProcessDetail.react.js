import React from 'react';
import PropTypes from 'prop-types';
import {Components} from '@opuscapita/service-base-ui';
import ReactTable from 'react-table';
import {ApiBase} from '../../api';
import 'react-table/react-table.css';
import './ProcessDetail.css';

class ProcessDetail extends Components.ContextComponent {

    state = {
        loading: false,
        process: {},
        history: [],
        showHistory: false,
        showInfos: true,
        showErrors: true,
        showWarnings: true,
    };

    static propTypes = {
        processId: PropTypes.string.isRequired,
    };

    constructor(props, context) {
        super(props);
        this.api = new ApiBase();
    }

    componentDidMount() {
        this.setState({loading: true});

        this.api.getProcessById(this.props.processId).then(process => {
            this.setState({loading: false, process: process});

        }).catch(e => {
            this.context.showNotification(e.message, 'error', 10);
            this.setState({loading: false});
        });
    }

    uploadFile(event) {
        const file = event.target.files[0];
        if (file.type !== 'text/xml') {
            this.context.showNotification('Please select an XML file', 'error', 10);
            return;
        }

        this.setState({loading: true});
        let data = new FormData();
        data.append('file', file);

        this.api.uploadFile(this.props.processId, data).then(() => {
            this.setState({loading: false});
            this.context.showNotification('Successfully updated the file', 'success', 10);
        }).catch(e => {
            this.setState({loading: false});
            this.context.showNotification(e.message, 'error', 10);
        });
    }

    downloadFile(event) {
        this.setState({loading: true});
        this.api.downloadFile(this.props.processId).then((response) => {
            this.setState({loading: false});

            const filename = response.headers['content-disposition'].split('filename=')[1];
            let url = window.URL.createObjectURL(response.body);
            let a = document.createElement('a');
            a.href = url;
            a.download = filename;
            a.click();
        }).catch(e => {
            this.setState({loading: false});
            this.context.showNotification(e.message, 'error', 10);
        });
    }

    reprocessMessage(event) {
        event.preventDefault();

        this.api.reprocessMessage(this.props.processId).then(() => {
            this.setState({loading: false});
            this.context.showNotification('The file is sent for reprocessing', 'info', 10);
        }).catch(e => {
            this.setState({loading: false});
            this.context.showNotification(e.message, 'error', 10);
        });
    }

    loadHistory(e) {
        this.setState({loading: true});

        this.api.getMessageHistory(this.state.process.messageId).then(history => {
            this.setState({loading: false, history: history, showHistory: true});

        }).catch(e => {
            this.context.showNotification(e.message, 'error', 10);
            this.setState({loading: false});
        });
    }

    hideHistory(e) {
        this.setState({history: [], showHistory: false});
    }

    getHistory() {
        const {history, showInfos, showErrors, showWarnings} = this.state;

        return history.filter(log => {
            if (!showInfos && log.level === 'INFO') {
                return false;
            }
            if (!showErrors && log.level === 'ERROR') {
                return false;
            }
            if (!showWarnings && log.level === 'WARNING') {
                return false;
            }
            return true;
        });
    }

    getHistoryTypeLabelClass(level) {
        switch (level) {
            case 'INFO':
                return 'info';
            case 'WARNING':
                return 'warning';
            case 'ERROR':
                return 'danger';
            default:
                return 'default';
        }
    }

    render() {
        const {loading, process, showHistory, showInfos, showErrors, showWarnings} = this.state;

        return (
            <div>
                <div className="form-horizontal process-detail">
                    <div className="row">
                        <div className="col-md-12">
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Message ID</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{process.messageId}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Transmission ID</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{process.transmissionId}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">File Name</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{process.filename}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Message Status</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{process.messageStatus}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Process Status</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{process.status}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Source / Direction</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{process.source} / {process.direction}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Sender / Receiver</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{process.sender} / {process.receiver}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Arrived At</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{process.arrivedAt}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Profile ID</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{process.profileId}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Document Type</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{process.documentType}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Document Type ID</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{process.documentTypeId}</label>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="form-submit text-right process-detail-actions">
                    <label className="btn btn-default">
                        Upload<input type="file" hidden onChange={e => this.uploadFile(e)}/>
                    </label>
                    <button className="btn btn-default" onClick={e => this.downloadFile(e)}>Download</button>
                    <button className="btn btn-danger" onClick={e => this.reprocessMessage(e)}>Reprocess</button>
                    { showHistory
                        ? <button className="btn btn-primary" onClick={e => this.hideHistory(e)}>Hide History</button>
                        : <button className="btn btn-primary" onClick={e => this.loadHistory(e)}>Show History</button>
                    }
                </div>
                {
                    showHistory &&
                    <div>
                        <h3>Message History</h3>
                        <div className="form-horizontal process-detail">
                            <div className="row">
                                <div className="col-md-12">
                                    <ReactTable
                                        className="message-detail-history-table"
                                        data={this.getHistory()}
                                        loading={loading}
                                        columns={[
                                            {
                                                id: 'type',
                                                width: 200,
                                                Header: 'Type from Source',
                                                accessor: log => log,
                                                Cell: ({value}) =>
                                                    <span>
                                                        <span className={`label label-${this.getHistoryTypeLabelClass(value.level)}`}>
                                                            {(value.level === 'ERROR') ? value.errorType : value.level}
                                                        </span> from
                                                        <span className="label label-default">{value.source}</span>
                                                    </span>
                                            },
                                            {
                                                id: 'message',
                                                Header: 'Message',
                                                accessor: 'message',
                                            }
                                        ]}
                                        sorted={[{
                                            id: 'time',
                                            desc: false
                                        }]}
                                        minRows={5}
                                        showPagination={false}
                                    />
                                </div>
                            </div>
                        </div>
                        <div className="form-submit text-right process-detail-actions">
                            { showInfos
                                ? <button className="btn btn-info" onClick={() => this.setState({showInfos: false})}>Hide Infos</button>
                                : <button className="btn btn-info btn-passive" onClick={() => this.setState({showInfos: true})}>Show Infos</button>
                            }
                            { showWarnings
                                ? <button className="btn btn-warning" onClick={() => this.setState({showWarnings: false})}>Hide Warnings</button>
                                : <button className="btn btn-warning btn-passive" onClick={() => this.setState({showWarnings: true})}>Show Warnings</button>
                            }
                            { showErrors
                                ? <button className="btn btn-danger" onClick={() => this.setState({showErrors: false})}>Hide Errors</button>
                                : <button className="btn btn-danger btn-passive" onClick={() => this.setState({showErrors: true})}>Show Errors</button>
                            }
                        </div>
                    </div>
                }
            </div>
        );
    }
}

export default ProcessDetail;
