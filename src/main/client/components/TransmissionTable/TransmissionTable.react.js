import React from 'react';
import PropTypes from 'prop-types';
import {Components} from '@opuscapita/service-base-ui';
import ReactTable from 'react-table';
import ReactTooltip from 'react-tooltip';
import {ApiBase} from '../../api';
import Select from '@opuscapita/react-select';
import 'react-table/react-table.css';
import './TransmissionTable.css';

class TransmissionTable extends Components.ContextComponent {

    static sources = [
        'NETWORK',
        'A2A',
        'XIB',
        'SIRIUS'
    ];

    static statuses = [
        'failed',
        'received',
        'processing',
        'validating',
        'sending',
        'fixed',
        'delivered'
    ];

    static errorTypes = [
        {value: 'PROCESSING_ERROR', label: 'ALL_PROCESSING_ERRORS'},
        {value: 'VALIDATION_ERROR', label: 'VALIDATION_ERROR'},
        {value: 'Cannot find a validation artifact for file', label: 'UNKNOWN_ARTIFACT_ERROR'},
        {value: 'blob service', label: 'BLOB_SERVICE_ERROR'},
        {value: 'SENDING_ERROR', label: 'SENDING_ERRORS'},
        {value: 'UNKNOWN_RECIPIENT', label: 'UNKNOWN_RECIPIENT'},
        {value: 'UNSUPPORTED_DATA_FORMAT', label: 'UNSUPPORTED_DATA_FORMAT'},
        {value: 'CONNECTION_ERROR', label: 'CONNECTION_ERROR'},
        {value: 'RECEIVING_AP_ERROR', label: 'RECEIVING_AP_ERROR'},
        {value: 'SECURITY_ERROR', label: 'SECURITY_ERROR'},
        {value: 'BP_DELIVERY_ERROR', label: 'BP_DELIVERY_ERROR'},
        {value: 'STOPPED_DELIVERY_ERROR', label: 'STOPPED_DELIVERY_ERROR'},
    ];

    state = {
        loading: false,
        transmissionList: [],
        searchValues: {},
        documentTypeOptions: [],
        localNameOptions: [],
        totalCount: -1,
        pagination: {},
    };

    constructor(props, context) {
        super(props);

        this.api = new ApiBase();
    }

    async componentDidMount() {
        await this.fetchDocumentTypesFromValidator();
    }

    async loadTransmissionList(tableState) {
        this.setState({loading: true});
        let {pagination, searchValues} = this.state;

        try {
            if (tableState) {
                pagination.page = tableState.page;
                pagination.pageSize = tableState.pageSize;
                pagination.sorted = tableState.sorted;
            } else {
                pagination.page = 0;
            }

            const response = await this.api.getTransmissionList(pagination, searchValues);
            this.setState({transmissionList: response.data, totalCount: response.totalCount});
        }
        catch (e) {
            this.context.showNotification(e.message, 'error', 10);
        }
        finally {
            this.setState({loading: false});
        }
    }

    async exportTransmissionList(tableState) {
        this.setState({loading: true});
        let {pagination, searchValues} = this.state;

        try {
            if (tableState) {
                pagination.page = tableState.page;
                pagination.pageSize = tableState.pageSize;
                pagination.sorted = tableState.sorted;
            } else {
                pagination.page = 0;
            }

            this.api.exportTransmissionList(pagination, searchValues).then(response => {
                console.log(response);
            });
        }
        catch (e) {
            this.context.showNotification(e.message, 'error', 10);
        }
        finally {
            this.setState({loading: false});
        }
    }

    async bulkReprocess() {
        const {transmissionList} = this.state;
        const {userData, showModalDialog, hideModalDialog} = this.context;

        const onConfirmationClick = (btn) => {
            hideModalDialog();

            if (btn === 'yes') {
                this.setState({loading: true});

                setTimeout(() => {
                    const transmissionIds = transmissionList.map(t => t.id);
                    this.api.reprocessMessages(transmissionIds, userData.id).then(() => {
                        this.setState({loading: false});
                        this.context.showNotification('Reprocessing of the messages has been started', 'info', 3);
                    }).catch(e => {
                        this.setState({loading: false});
                        this.context.showNotification(e.message, 'error', 10);
                    });

                }, 500);
            }
        }

        const modalTitle = "Bulk Reprocess";
        const warnCount = transmissionList.filter(t => t.status !== 'failed').length;
        const modalText = `${transmissionList.length} transmissions will be reprocessed in the background.${(warnCount > 0) ? ` Note that ${warnCount} of them did NOT failed.` : ' '}\n\nDo you want to continue?`;
        const modalButtons = {no: 'No', yes: 'Yes'};

        showModalDialog(modalTitle, modalText, onConfirmationClick, modalButtons);
    }

    async bulkSendMlr() {
        const {transmissionList} = this.state;
        const {userData, showModalDialog, hideModalDialog} = this.context;

        const onConfirmationClick = (btn) => {
            hideModalDialog();

            if (btn === 'yes') {
                this.setState({loading: true});

                setTimeout(() => {
                    const transmissionIds = transmissionList.map(t => t.id);
                    this.api.sendMlrs(transmissionIds, userData.id).then(() => {
                        this.setState({loading: false});
                        this.context.showNotification('MLR sending operation of the messages has been started', 'info', 3);
                    }).catch(e => {
                        this.setState({loading: false});
                        this.context.showNotification(e.message, 'error', 10);
                    });

                }, 500);
            }
        }

        const modalTitle = "Bulk Send MLR";
        const modalText = `${transmissionList.length} transmissions will be processed in the background. New MLR report will be created for each one according to its status and will be send to the owner\n\nDo you want to continue?`;
        const modalButtons = {no: 'No', yes: 'Yes'};

        showModalDialog(modalTitle, modalText, onConfirmationClick, modalButtons);
    }

    async bulkMarkAsFixed() {
        const {userData} = this.context;
        const {transmissionList} = this.state;
        this.setState({loading: true});

        const transmissionIds = transmissionList.map(t => t.id);
        this.api.markAsFixedMessages(transmissionIds, userData.id).then(() => {
            this.setState({loading: false});
            this.context.showNotification('Marking operation of the messages as fixed has been started', 'info', 3);
        }).catch(e => {
            this.setState({loading: false});
            this.context.showNotification(e.message, 'error', 10);
        });
    }

    showTransmissionDetail(e, id) {
        e && e.preventDefault();
        this.context.router.push(`/peppol-monitor/messageDetail/${id}`);
    }

    goCustomPage() {
        this.context.router.push(`/peppol-monitor/advancedOperations`);
    }

    showParticipantLookup(participant) {
        const parts = participant.split(":");
        window.open(`https://my.galaxygw.com/participantlookup#/${parts[0]}/${parts[1]}`, '_blank');
    }

    mapSourcesSelect() {
        return TransmissionTable.sources.map(value => {
            return {value: value, label: value};
        });
    }

    mapStatusesSelect() {
        return TransmissionTable.statuses.map(value => {
            return {value: value, label: value};
        });
    }

    mapErrorTypesSelect() {
        return TransmissionTable.errorTypes;
    }

    mapErrorTypesSelectValue() {
        const errorType = this.state.searchValues.errorType;
        return TransmissionTable.errorTypes.find(e => e.value === errorType);
    }

    async fetchDocumentTypesFromValidator() {
        try {
            const documentTypes = await this.api.getDocumentTypes();
            documentTypes.forEach(d => {
                d.value = d.id;
                d.label = "[" + d.id + "] " + d.description;
            });

            const localNames = [...new Set(documentTypes.map(d => d.localName))].map(value => {
                return {value: value, label: value};
            });

            this.setState({documentTypeOptions: documentTypes, localNameOptions: localNames});
        } catch (e) {
            this.context.showNotification(e.message, 'error', 10);
            this.setState({documentTypeOptions: [], localNameOptions: []});
        }
    }

    mapDocumentTypeSelectedValue() {
        const {documentTypeOptions} = this.state;
        const documentTypeIds = this.state.searchValues.documentTypeIds;
        if (documentTypeOptions && documentTypeIds && documentTypeIds.length) {
            return documentTypeOptions.filter(d => documentTypeIds.includes(d.value));
        }
        return [];
    }

    mapLocalNameSelectedValue() {
        const {documentTypeOptions} = this.state;
        const localNameIds = this.state.searchValues.localNameIds;
        if (documentTypeOptions && localNameIds && localNameIds.length) {
            const selectedDocumentTypes = documentTypeOptions.filter(d => localNameIds.includes(d.value));
            return [...new Set(selectedDocumentTypes.map(d => d.localName))].map(value => {
                return {value: value, label: value};
            });
        }
        return [];
    }

    mapDocumentTypesByLocalNames(localNames) {
        const {documentTypeOptions} = this.state;
        if (documentTypeOptions && localNames && localNames.length) {
            return documentTypeOptions.filter(d => localNames.map(l => l.value).includes(d.localName));
        }
        return [];
    }

    getStatusLabelClass(status) {
        switch (status) {
            case 'delivered':
            case 'fixed':
                return 'success';
            case 'unknown':
                return 'warning';
            case 'failed':
                return 'danger';
            default:
                return 'info';
        }
    }

    handleSearchFormChange(field, value) {
        const {searchValues} = this.state;

        // multi-select
        if (Array.isArray(value))
            searchValues[field] = value.map(val => val.value);
        // date-picker
        else if (value instanceof Date)
            searchValues[field] = value;
        // single-select
        else if (typeof value === 'object')
            searchValues[field] = value !== null ? value.value : null;
        // text-input
        else
            searchValues[field] = value;

        this.setState({searchValues});
    }

    resetSearch() {
        const searchValues = {
            id: '',
            messageId: '',
            filename: '',
            sender: '',
            receiver: '',
            accessPoint: '',
            invoiceNumber: '',
            history: '',
            errorType: null,
            sources: [],
            destinations: [],
            statuses: [],
            localNameIds: [],
            documentTypeIds: [],
            startDate: '',
            endDate: ''
        };

        this.setState({searchValues}, () => this.loadTransmissionList());
    }

    render() {
        const {i18n} = this.context;
        const {loading, transmissionList, documentTypeOptions, localNameOptions, pagination, totalCount, searchValues} = this.state;

        return (
            <div>
                <h3>Transmission List</h3>
                <div>
                    <div className="form-horizontal transmission-search">
                        <div className="row">
                            <div className="col-md-6">
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">Transmission ID</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <input type="text" className="form-control" value={searchValues.id}
                                               onChange={e => this.handleSearchFormChange('id', e.target.value)}
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">File Name</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <input type="text" className="form-control" value={searchValues.filename}
                                               onChange={e => this.handleSearchFormChange('filename', e.target.value)}
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">Error Type</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <Select className="react-select" isMulti={false}
                                                options={this.mapErrorTypesSelect()}
                                                onChange={value => this.handleSearchFormChange('errorType', value)}
                                                value={this.mapErrorTypesSelectValue()}
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">Document Type</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <Select className="react-select" isMulti={true}
                                                options={documentTypeOptions}
                                                onChange={value => this.handleSearchFormChange('documentTypeIds', value)}
                                                value={this.mapDocumentTypeSelectedValue()}
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">Invoice Number</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <input type="text" className="form-control" value={searchValues.invoiceNumber}
                                               onChange={e => this.handleSearchFormChange('invoiceNumber', e.target.value)}
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">Source</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <Select className="react-select" isMulti={true}
                                                options={this.mapSourcesSelect()}
                                                onChange={value => this.handleSearchFormChange('sources', value)}
                                                value={searchValues.sources && searchValues.sources.map(src => ({
                                                    label: src,
                                                    value: src
                                                }))}
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">Sender</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <input type="text" className="form-control" value={searchValues.sender}
                                               onChange={e => this.handleSearchFormChange('sender', e.target.value)}
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">Start Date</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <Components.DatePicker
                                            showIcon={false}
                                            dateFormat={i18n.dateTimeFormat}
                                            onChange={e => this.handleSearchFormChange('startDate', e.date)}
                                            value={searchValues.startDate && new Date(searchValues.startDate)}
                                        />
                                    </div>
                                </div>
                            </div>
                            <div className="col-md-6">
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">Message ID</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <input type="text" className="form-control" value={searchValues.messageId}
                                               onChange={e => this.handleSearchFormChange('messageId', e.target.value)}
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">Access Point</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <input type="text" className="form-control" value={searchValues.accessPoint}
                                               onChange={e => this.handleSearchFormChange('accessPoint', e.target.value)}
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">History</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <input type="text" className="form-control" value={searchValues.history}
                                               onChange={e => this.handleSearchFormChange('history', e.target.value)}
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">Local Name</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <Select className="react-select" isMulti={true}
                                                options={localNameOptions}
                                                onChange={value => this.handleSearchFormChange('localNameIds', this.mapDocumentTypesByLocalNames(value))}
                                                value={this.mapLocalNameSelectedValue()}
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">Status</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <Select className="react-select" isMulti={true}
                                                options={this.mapStatusesSelect()}
                                                onChange={value => this.handleSearchFormChange('statuses', value)}
                                                value={searchValues.statuses && searchValues.statuses.map(sts => ({
                                                    label: sts,
                                                    value: sts
                                                }))}
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">Destination</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <Select className="react-select" isMulti={true}
                                                options={this.mapSourcesSelect()}
                                                onChange={value => this.handleSearchFormChange('destinations', value)}
                                                value={searchValues.destinations && searchValues.destinations.map(src => ({
                                                    label: src,
                                                    value: src
                                                }))}
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">Receiver</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <input type="text" className="form-control" value={searchValues.receiver}
                                               onChange={e => this.handleSearchFormChange('receiver', e.target.value)}
                                        />
                                    </div>
                                </div>
                                <div className="form-group">
                                    <div className="col-sm-3">
                                        <label className="control-label">End Date</label>
                                    </div>
                                    <div className="offset-md-1 col-md-8">
                                        <Components.DatePicker
                                            showIcon={false}
                                            dateFormat={i18n.dateTimeFormat}
                                            onChange={e => this.handleSearchFormChange('endDate', e.date)}
                                            value={searchValues.endDate && new Date(searchValues.endDate)}
                                        />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="form-submit text-right">
                        <button className="btn btn-link" onClick={() => this.resetSearch()}>Reset</button>
                        <button className="btn btn-primary" onClick={() => this.loadTransmissionList()}>Filter</button>
                        <button className="btn btn-default float-left" onClick={() => this.goCustomPage()}>
                            Custom Operations
                        </button>
                        <div className="btn-group float-left" role="group">
                            <button id="btnGroupDrop1" type="button" className="btn btn-secondary dropdown-toggle"
                                    data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                Bulk Operations
                            </button>
                            <div className="dropdown-menu" aria-labelledby="btnGroupDrop1">
                                <a className="dropdown-item" onClick={() => this.bulkReprocess()}>Reprocess</a>
                                <a className="dropdown-item" onClick={() => this.bulkMarkAsFixed()}>Mark as Fixed</a>
                                <a className="dropdown-item" onClick={() => this.bulkSendMlr()}>Send MLR</a></div>
                                <a className="dropdown-item" onClick={() => this.exportTransmissionList()}>Export CSV</a></div>
                        </div>
                    </div>
                    <hr/>
                </div>

                <ReactTable
                    className="transmission-list-table"
                    loading={loading}
                    data={transmissionList}
                    onFetchData={(state) => this.loadTransmissionList(state)}

                    manual
                    minRows={10}
                    pages={Math.ceil(totalCount / (pagination.pageSize || 10))}
                    defaultPageSize={10}
                    pageSizeOptions={[5, 10, 20, 50, 100, 1000]}
                    defaultSorted={[{id: 'arrivedAt', desc: true}]}

                    getTrProps={(state, rowInfo) => {
                        if (rowInfo && rowInfo.original.status === 'failed')
                            return {style: {'background-color': '#f2dedf'}};
                        return {}
                    }}

                    columns={[
                        {
                            id: 'transmissionId',
                            Header: ' ',
                            width: 50,
                            accessor: row => row,
                            Cell: ({value}) =>
                                <span>
                                    <a href={`/peppol-monitor?r=messageDetail/${value.id}`}
                                       onClick={(e) => this.showTransmissionDetail(e, value.id)}
                                       className="btn btn-link detail-link">
                                        <span className="glyphicon glyphicon-open-file"></span>
                                    </a>
                                </span>
                        },
                        {
                            id: 'filename',
                            accessor: row => row,
                            Header: 'File Name',
                            Cell: ({value}) =>
                                <span>
                                    <span data-tip data-for={`name-tooltip-${value.transmissionId}`}>
                                        {value.filename.split('/').pop()}
                                    </span>
                                    <ReactTooltip id={`name-tooltip-${value.transmissionId}`}
                                                  className="sticky" effect="solid" delayHide={100}>
                                        <p>Message ID: {value.messageId}</p>
                                        <p>Transmission ID: {value.transmissionId}</p>
                                        <p>Full Path: {value.filename}</p>
                                    </ReactTooltip>
                                </span>
                        },
                        {
                            id: 'status',
                            width: 75,
                            accessor: 'status',
                            Header: 'Status',
                            Cell: ({value}) =>
                                <span className={`label label-${this.getStatusLabelClass(value)}`}>{value}</span>
                        },
                        {
                            width: 150,
                            accessor: 'invoiceNumber',
                            Header: 'Invoice Number'
                        },
                        {
                            id: 'sender',
                            width: 150,
                            accessor: 'sender',
                            Header: 'Sender',
                            Cell: ({value}) =>
                                <a className="btn btn-link" onClick={this.showParticipantLookup.bind(this, value)}>
                                    {value}
                                </a>
                        },
                        {
                            id: 'receiver',
                            width: 150,
                            accessor: 'receiver',
                            Header: 'Receiver',
                            Cell: ({value}) =>
                                <a className="btn btn-link" onClick={this.showParticipantLookup.bind(this, value)}>
                                    {value}
                                </a>
                        },
                        {
                            width: 100,
                            accessor: 'accessPoint',
                            Header: 'Access Point'
                        },
                        {
                            id: 'source',
                            width: 155,
                            accessor: row => row,
                            Header: 'Direction',
                            Cell: ({value}) => <span className="well">{value.source} <span className="glyphicon glyphicon-arrow-right right-arrow"></span> {(value.direction) ? value.direction.toUpperCase() : '-'}</span>
                        },
                        {
                            id: 'arrivedAt',
                            width: 150,
                            accessor: 'arrivedAt',
                            Header: 'Arrived At',
                            Cell: props => <span>{i18n.formatDateTime(props.value)}</span>
                        }
                    ]}
                />
                <div className="text-center media">
                    <p>{`${pagination.page * pagination.pageSize} to ${Math.min((pagination.page * pagination.pageSize + pagination.pageSize), totalCount)} of ${totalCount} transmissions`}</p>
                </div>
            </div>
        );
    }
}

export default TransmissionTable;
