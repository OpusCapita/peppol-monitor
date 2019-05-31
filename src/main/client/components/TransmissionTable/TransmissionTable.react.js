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

    state = {
        loading: false,
        transmissionList: [],
        searchValues: {},
        showSearch: true,
        totalCount: -1,
        pagination: {},
    };

    static propTypes = {
        goTransmissionDetail: PropTypes.func
    };

    static defaultProps = {
        goTransmissionDetail: () => null
    };

    constructor(props, context) {
        super(props);

        this.api = new ApiBase();
    }

    // componentDidMount() {
    //     this.loadStateFromLocalStorage();
    //     window.addEventListener("beforeunload", this.saveStateToLocalStorage.bind(this));
    // }
    //
    // componentWillUnmount() {
    //     window.removeEventListener("beforeunload", this.saveStateToLocalStorage.bind(this));
    //     this.saveStateToLocalStorage();
    // }
    //
    // saveStateToLocalStorage() {
    //     localStorage.setItem("transmissionTable_searchValues", JSON.stringify(this.state.searchValues));
    // }
    //
    // loadStateFromLocalStorage() {
    //     let searchValues = localStorage.getItem("transmissionTable_searchValues");
    //     try {
    //         searchValues = JSON.parse(searchValues);
    //     } catch (e) {
    //     }
    //
    //     if (searchValues) {
    //         this.setState({searchValues}, () => this.loadTransmissionList());
    //     }
    // }

    async loadTransmissionList(tableState) {
        this.setState({loading: true});
        const {pagination, searchValues} = this.state;

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

    async bulkReprocess() {
        const {loading, transmissionList} = this.state;
        const {showNotification, showModalDialog, hideModalDialog} = this.context;

        const onConfirmationClick = (btn) => {
            hideModalDialog();

            if (btn === 'yes') {
                this.setState({loading: true});

                setTimeout(() => {
                    const transmissionIds = transmissionList.map(t => t.id).join("-");
                    this.api.reprocessMessages(transmissionIds).then(() => {
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
        const modalText = `${transmissionList.length} messages will be reprocessed in the background.${(warnCount > 0) ? ` Note that ${warnCount} of them did NOT failed.` : ' '}\n\nDo you want to continue?`;
        const modalButtons = {no: 'No', yes: 'Yes'};

        showModalDialog(modalTitle, modalText, onConfirmationClick, modalButtons);
    }

    showTransmissionDetail(e, id) {
        e && e.preventDefault();
        this.context.router.push(`/peppol-monitor/messageDetail/${id}`);
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

        if (Array.isArray(value))
            searchValues[field] = value.map(val => val.value);
        else
            searchValues[field] = value;

        this.setState({searchValues});
    }

    resetSearch() {
        const searchValues = {
            id: '',
            filename: '',
            sender: '',
            receiver: '',
            accessPoint: '',
            history: '',
            sources: [],
            statuses: [],
            startDate: '',
            endDate: ''
        };

        this.setState({searchValues}, () => this.loadTransmissionList());
    }

    render() {
        const {i18n} = this.context;
        const {loading, transmissionList, pagination, totalCount, searchValues, showSearch} = this.state;

        return (
            <div>
                <h3>Transmission List</h3>
                {
                    showSearch &&
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
                            <button className="btn btn-primary" onClick={() => this.loadTransmissionList()}>Filter
                            </button>
                            <button className="btn btn-danger float-left"
                                    onClick={() => this.bulkReprocess()}>Reprocess
                            </button>
                        </div>
                        <hr/>
                    </div>
                }

                <ReactTable
                    className="transmission-list-table"
                    loading={loading}
                    data={transmissionList}
                    onFetchData={(state) => this.loadTransmissionList(state)}

                    manual
                    minRows={10}
                    pages={Math.ceil(totalCount / (pagination.pageSize || 10))}
                    defaultPageSize={10}
                    pageSizeOptions={[5, 10, 20, 50]}
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
                            width: 100,
                            accessor: 'source',
                            Header: 'Source',
                            Cell: ({value}) => <span className="well">{value}</span>
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
