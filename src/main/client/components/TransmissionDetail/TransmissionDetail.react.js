import React from 'react';
import PropTypes from 'prop-types';
import {Components} from '@opuscapita/service-base-ui';
import ReactTable from 'react-table';
import {ApiBase} from '../../api';
import 'react-table/react-table.css';
import './TransmissionDetail.css';

class TransmissionDetail extends Components.ContextComponent {

    state = {
        loading: false,
        transmission: {},
        history: [],
        showHistory: false,
        showInfos: true,
        showErrors: true,
        showWarnings: true,
    };

    static propTypes = {
        transmissionId: PropTypes.string.isRequired,
    };

    constructor(props, context) {
        super(props);
        this.api = new ApiBase();
    }

    componentDidMount() {
        this.setState({loading: true});

        this.api.getTransmissionById(this.props.transmissionId).then(transmission => {
            this.setState({loading: false, transmission: transmission});

        }).catch(e => {
            this.context.showNotification(e.message, 'error', 10);
            this.setState({loading: false});
        });
    }

    uploadFile(event) {
        const file = event.target.files[0];
        if (file.type !== 'text/xml') {
            this.context.showNotification('Please select an XML file', 'error', 3);
            return;
        }


        let data = new FormData();
        data.append('file', file);
        this.context.showSpinner();
        this.api.uploadFile(this.props.transmissionId, data).then(() => {
            this.context.hideSpinner();
            this.context.showNotification('Successfully updated the file', 'success', 3);
        }).catch(e => {
            this.context.hideSpinner();
            this.context.showNotification(e.message, 'error', 10);
        });
    }

    downloadFile(event) {
        this.setState({loading: true});
        this.api.downloadFile(this.props.transmissionId).then((response) => {
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

    downloadMlr(event) {
        this.setState({loading: true});
        this.api.downloadMlr(this.props.transmissionId).then((response) => {
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

        this.context.showSpinner();
        this.api.reprocessMessage(this.props.transmissionId).then(() => {
            this.context.hideSpinner();
            this.context.showNotification('The file is sent for reprocessing', 'info', 3);
        }).catch(e => {
            this.context.hideSpinner();
            this.context.showNotification(e.message, 'error', 10);
        });
    }

    loadHistory(e) {
        this.setState({loading: true});

        this.api.getMessageHistory(this.state.transmission.messageId).then(history => {
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
        const {i18n} = this.context;
        const {loading, transmission, showHistory, showInfos, showErrors, showWarnings} = this.state;

        return (
            <div>
                <h3>Transmission Details</h3>
                <div className="form-horizontal transmission-detail">
                    <div className="row">
                        <div className="col-md-12">
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Message ID</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{transmission.messageId}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Transmission ID</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{transmission.transmissionId}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">File Name</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{transmission.filename}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Message Status</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{transmission.messageStatus}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Transmission Status</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{transmission.status}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Source / Direction</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{transmission.source} / {transmission.direction}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Sender / Receiver</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{transmission.sender} / {transmission.receiver}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Arrived At</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{transmission.arrivedAt}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Profile ID</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{transmission.profileId}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Validation Rule</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{transmission.documentType}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label btn-link">Document Type ID</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{transmission.documentTypeId}</label>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="form-submit text-right transmission-detail-actions">
                    <label className="btn btn-default">
                        Upload<input type="file" hidden onChange={e => this.uploadFile(e)}/>
                    </label>
                    <button className="btn btn-default" onClick={e => this.downloadFile(e)}>Download</button>
                    {
                        (transmission.direction === 'OUT' && transmission.status === 'failed') &&
                        <button className="btn btn-default" onClick={e => this.downloadMlr(e)}>MLR</button>
                    }
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
                        <div className="form-horizontal transmission-detail">
                            <div className="row">
                                <div className="col-md-12">
                                    <ReactTable
                                        className="message-detail-history-table"
                                        data={this.getHistory()}
                                        loading={loading}
                                        columns={[
                                            {
                                                id: 'type',
                                                width: 185,
                                                Header: 'Type from Source',
                                                accessor: log => log,
                                                Cell: ({value}) =>
                                                    <span>
                                                        <span className={`label label-${this.getHistoryTypeLabelClass(value.level)}`}>
                                                            {(value.level === 'ERROR') ? value.errorType : value.level}
                                                        </span> from <span className="label label-default">{value.source}</span>
                                                    </span>
                                            },
                                            {
                                                id: 'time',
                                                width: 125,
                                                accessor: log => log,
                                                Header: 'Time',
                                                Cell: ({value}) => <span className="label label-none">{i18n.formatDateTime(value.time)}</span>
                                            },
                                            {
                                                id: 'message',
                                                Header: 'Message',
                                                accessor: 'message',
                                            }
                                        ]}
                                        sorted={[{
                                            id: 'time',
                                            desc: true
                                        }]}
                                        minRows={5}
                                        defaultPageSize={100}
                                    />
                                </div>
                            </div>
                        </div>
                        <div className="form-submit text-right transmission-detail-actions">
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

export default TransmissionDetail;
