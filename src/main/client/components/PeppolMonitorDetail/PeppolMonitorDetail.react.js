import React from 'react';
import PropTypes from 'prop-types';
import {Components} from '@opuscapita/service-base-ui';
import ReactTable from 'react-table';
import {ApiBase} from '../../api';
import 'react-table/react-table.css';
import './PeppolMonitorDetail.css';

class PeppolMonitorDetail extends Components.ContextComponent {

    state = {
        loading: false,
        process: {},
        history: [],
        showHistory: false
    };

    static propTypes = {
        messageId: PropTypes.string.isRequired, //rename to processId
    };

    constructor(props, context) {
        super(props);

        this.api = new ApiBase();
    }

    componentDidMount() {
        this.setState({loading: true});

        this.api.getProcessById(this.props.messageId).then(process => {
            this.setState({loading: false, process: process});

        }).catch(e => {
            this.context.showNotification(e.message, 'error', 10);
            this.setState({loading: false});
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

    render() {
        const {i18n} = this.context;
        const {loading, process, history, showHistory} = this.state;

        return (
            <div>
                <h3>Process Details</h3>
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
                                    <label className="control-label btn-link">Source / Status</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="control-label">{process.source} / {process.status}</label>
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
                <div className="form-submit text-right">
                    <button className="btn btn-link">Upload</button>
                    <button className="btn btn-link">Download</button>
                    <button className="btn btn-danger">Reprocess</button>
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
                                        data={history}
                                        loading={loading}
                                        columns={[
                                            {
                                                id: 'type',
                                                width: 150,
                                                Header: 'Type from Source',
                                                accessor: log => ((log.level === 'ERROR' ? log.errorType : log.level) + ' from ' + log.source),
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
                    </div>
                }
            </div>
        );
    }
}

export default PeppolMonitorDetail;
