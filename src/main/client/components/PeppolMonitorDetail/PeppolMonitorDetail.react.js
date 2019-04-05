import React from 'react';
import PropTypes from 'prop-types';
import {Components} from '@opuscapita/service-base-ui';
import ReactTable from 'react-table';
import {Message} from '../../api';
import 'react-table/react-table.css';

class PeppolMonitorDetail extends Components.ContextComponent {

    state = {
        loading: false,
        message: {},
        showHistory: true
    };

    static propTypes = {
        messageId: PropTypes.string.isRequired,
    };

    constructor(props, context) {
        super(props);

        this.messageApi = new Message();
    }

    componentDidMount() {
        this.setState({loading: true});

        this.messageApi.getMessageByMessageId(this.props.messageId).then(message => {
            this.setState({loading: false, message: message});

        }).catch(e => {
            this.context.showNotification(e.message, 'error', 10);
            this.setState({loading: false});
        });
    }

    render() {
        const {i18n} = this.context;
        const {loading, message, showHistory} = this.state;
        console.log(message);

        return (
            <div>
                <h3>Message Details</h3>
                <div className="form-horizontal message-detail">
                    <div className="row">
                        <div className="col-md-12">
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label">ID</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="value-label">{message.messageId}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label">File Name</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="value-label">{message.filename}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label">Source / Status</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="value-label">{message.source} / {message.status}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label">Sender / Receiver</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="value-label">{message.sender} / {message.receiver}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label">Document Type</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="value-label">{message.documentType}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label">Document Type ID</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="value-label">{message.documentTypeId}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label">Profile ID</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="value-label">{message.profileId}</label>
                                </div>
                            </div>
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label">Arrived At</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <label className="value-label">{i18n.formatDate(message.arrivedAt)}</label>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="form-submit text-right">
                    <button className="btn btn-link">Download</button>
                    <button className="btn btn-primary">Reprocess</button>
                </div>
                {
                    showHistory &&
                    <div>
                        <h3>Message History</h3>
                        <div className="form-horizontal message-detail">
                            <div className="row">
                                <div className="col-md-12">
                                    <ReactTable
                                        className="message-detail-history-table"
                                        data={message.logs}
                                        loading={loading}
                                        columns={[
                                            {
                                                id: 'type',
                                                accessor: log => ((log.level === 'ERROR' ? log.errorType : log.level) + ' from ' + log.source),
                                            },
                                            {
                                                id: 'message',
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
