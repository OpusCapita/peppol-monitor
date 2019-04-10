import React from 'react';
import PropTypes from 'prop-types';
import {Components} from '@opuscapita/service-base-ui';
import ReactTable from 'react-table';
import ReactTooltip from 'react-tooltip';
import {ApiBase} from '../../api';
import Select from '@opuscapita/react-select';
import 'react-table/react-table.css';
import './PeppolMonitor.css';

class PeppolMonitor extends Components.ContextComponent {

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
        'delivered'
    ];

    state = {
        loading: false,
        processes: [],
        searchValues: {},
        showSearch: true,
        pageCount: -1,
        pagination: {},
    };

    static propTypes = {
        goMessageDetail: PropTypes.func.isRequired
    };

    static defaultProps = {
        goMessageDetail: () => null //rename to goProcessDetail
    };

    constructor(props, context) {
        super(props);

        this.api = new ApiBase();
    }

    async loadProcesses(tableState) {
        this.setState({loading: true});
        const {pagination, searchValues} = this.state;

        try {
            if (tableState) {
                pagination.page = tableState.page;
                pagination.pageSize = tableState.pageSize;
                pagination.sorted = tableState.sorted;
            }

            const response = await this.api.getProcesses(pagination, searchValues);
            this.setState({processes: response.data, pageCount: response.pages});
        }
        catch (e) {
            this.context.showNotification(e.message, 'error', 10);
        }
        finally {
            this.setState({loading: false});
        }
    }

    showProcessDetail(id) {
        this.props.goMessageDetail(id);
    }

    showParticipantLookup(participant) {
        const parts = participant.split(":");
        window.open(`https://my.galaxygw.com/participantlookup#/${parts[0]}/${parts[1]}`,'_blank');
    }

    mapSourcesSelect() {
        return PeppolMonitor.sources.map(value => {
            return {value: value, label: value};
        });
    }

    mapStatusesSelect() {
        return PeppolMonitor.statuses.map(value => {
            return {value: value, label: value};
        });
    }

    getStatusLabelClass(status) {
        switch (status) {
            case 'delivered':
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
            participant: '',
            accessPoint: '',
            sources: [],
            statuses: []
        };

        this.setState({searchValues}, () => this.loadProcesses());
    }

    render() {
        const {i18n} = this.context;
        const {loading, processes, pageCount, searchValues, showSearch} = this.state;

        return (
            <div>
                <h2>PEPPOL Access Point Monitoring</h2>
                {
                    showSearch &&
                    <div>
                        <div className="form-horizontal process-search">
                            <div className="row">
                                <div className="col-md-6">
                                    <div className="form-group">
                                        <div className="col-sm-3">
                                            <label className="control-label">Message ID</label>
                                        </div>
                                        <div className="offset-md-1 col-md-8">
                                            <input type="text" className="form-control" value={searchValues.id}
                                                   placeholder={"e7a85712-21ae-4d8b-a2de-c012a39bbb12"}
                                                   onChange={e => this.handleSearchFormChange('id', e.target.value)}/>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <div className="col-sm-3">
                                            <label className="control-label">File Name</label>
                                        </div>
                                        <div className="offset-md-1 col-md-8">
                                            <input type="text" className="form-control" value={searchValues.filename}
                                                   placeholder={"logger_5723_0000000001703094.xml"}
                                                   onChange={e => this.handleSearchFormChange('filename', e.target.value)}/>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <div className="col-sm-3">
                                            <label className="control-label">Participant</label>
                                        </div>
                                        <div className="offset-md-1 col-md-8">
                                            <input type="text" className="form-control" value={searchValues.participant}
                                                   placeholder={"9908:919779446"}
                                                   onChange={e => this.handleSearchFormChange('participant', e.target.value)}/>
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
                                                   placeholder={"PNO000104"}
                                                   onChange={e => this.handleSearchFormChange('accessPoint', e.target.value)}/>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <div className="col-sm-3">
                                            <label className="control-label">Source</label>
                                        </div>
                                        <div className="offset-md-1 col-md-8">
                                            <Select
                                                className="react-select"
                                                placeholder="NETWORK"
                                                value={searchValues.sources && searchValues.sources.map(src => ({
                                                    label: src,
                                                    value: src
                                                }))}
                                                onChange={value => this.handleSearchFormChange('sources', value)}
                                                isMulti={true}
                                                noOptionsMessage={() => 'No Results'}
                                                options={this.mapSourcesSelect()}/>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <div className="col-sm-3">
                                            <label className="control-label">Status</label>
                                        </div>
                                        <div className="offset-md-1 col-md-8">
                                            <Select
                                                className="react-select"
                                                placeholder="failed"
                                                value={searchValues.statuses && searchValues.statuses.map(sts => ({
                                                    label: sts,
                                                    value: sts
                                                }))}
                                                onChange={value => this.handleSearchFormChange('statuses', value)}
                                                isMulti={true}
                                                noOptionsMessage={() => 'No Results'}
                                                options={this.mapStatusesSelect()}/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className="form-submit text-right">
                            <button className="btn btn-link" onClick={() => this.resetSearch()}>Reset</button>
                            <button className="btn btn-primary" onClick={() => this.loadProcesses()}>Filter</button>
                        </div>
                        <hr/>
                    </div>
                }

                <ReactTable
                    className="process-list-table"
                    loading={loading}
                    data={processes}
                    onFetchData={(state) => this.loadProcesses(state)}

                    manual
                    minRows={10}
                    pages={pageCount}
                    defaultPageSize={20}
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
                            Header: 'Transmission ID',
                            accessor: row => row,
                            Cell: ({value}) =>
                                <span>
                                    <a className="btn btn-link" data-tip data-for={`id-tooltip-${value.transmissionId}`}
                                       onClick={this.showProcessDetail.bind(this, value.id)}>
                                        {value.transmissionId}
                                    </a>
                                    <ReactTooltip id={`id-tooltip-${value.transmissionId}`} delayHide={500}>
                                        <p>Message ID: {value.messageId}</p>
                                        <p>Transmission ID: {value.transmissionId}</p>
                                    </ReactTooltip>
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
                                    <ReactTooltip id={`name-tooltip-${value.transmissionId}`} delayHide={500}>
                                        {value.filename}
                                    </ReactTooltip>
                                </span>
                        },
                        {
                            id: 'status',
                            accessor: 'status',
                            Header: 'Status',
                            Cell: ({value}) =>
                                <span className={`label label-${this.getStatusLabelClass(value)}`}>{value}</span>
                        },
                        {
                            id: 'sender',
                            accessor: 'sender',
                            Header: 'Sender',
                            Cell: ({value}) =>
                                <a className="btn btn-link" onClick={this.showParticipantLookup.bind(this, value)}>
                                    {value}
                                </a>
                        },
                        {
                            id: 'receiver',
                            accessor: 'receiver',
                            Header: 'Receiver',
                            Cell: ({value}) =>
                                <a className="btn btn-link" onClick={this.showParticipantLookup.bind(this, value)}>
                                    {value}
                                </a>
                        },
                        {
                            accessor: 'accessPoint',
                            Header: 'Access Point'
                        },
                        {
                            id: 'source',
                            accessor: 'source',
                            Header: 'Source',
                            Cell: ({value}) => <span className="well">{value}</span>
                        },
                        {
                            accessor: 'arrivedAt',
                            Header: 'Arrived At',
                            Cell: props => <span>{i18n.formatDate(props.value)}</span>
                        }
                    ]}
                />
            </div>
        );
    }
}

export default PeppolMonitor;
