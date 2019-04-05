import React from 'react';
import PropTypes from 'prop-types';
import { Components } from '@opuscapita/service-base-ui';
import ReactTable from 'react-table';
import {Message} from '../../api';
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
        messages: [],
        pageNumber: 0,
        searchValues: {},
        availableRoles: [],
        showSearch: true
    };

    static propTypes = {
        goMessageDetail: PropTypes.func.isRequired
    };

    static defaultProps = {
        goMessageDetail: () => null
    };

    constructor(props, context) {
        super(props);

        this.messageApi = new Message();
    }

    async loadMessages() {
        this.setState({loading: true});

        try {
            const messages = await this.messageApi.getMessages(this.state.pageNumber);
            this.setState({messages});
        }
        catch (e) {
            this.context.showNotification(e.message, 'error', 10);
        }
        finally {
            this.setState({loading: false});
        }
    }

    showMessageDetail(id) {
        this.props.goMessageDetail(id);
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

    handleSearchFormChange(field, value) {
        const {searchValues} = this.state;

        if (Array.isArray(value))
            searchValues[field] = value.map(val => val.value);
        else
            searchValues[field] = value;

        this.setState({searchValues});
    }

    resetSearch(e) {
        e.preventDefault();

        const searchValues = {
            id: '',
            filename: '',
            participant: '',
            accessPoint: '',
            sources: [],
            statuses: []
        };

        this.setState({searchValues}, () => this.handleSearch());
    }

    handleSearch(e) {
        e && e.preventDefault();

        const {searchValues} = this.state;
        let searchObj = {};

        if (searchValues.id) {
            searchObj = {
                $or: {
                    id: {$like: `%${searchValues.id}%`},
                    '$UserProfile.email$': {$like: `%${searchValues.id}%`}
                }
            }
        }

        if (searchValues.name) {
            const names = searchValues.name.split(' ');

            searchObj = {
                $or: {
                    '$UserProfile.firstName$': {$like: `%${names[0]}%`},
                    '$UserProfile.lastName$': {$like: `%${names[1] || names[0]}%`}
                }
            }
        }

        if (searchValues.roles && searchValues.roles.length > 0)
            searchObj['$UserRoles.id$'] = {$in: searchValues.roles};

        if (searchValues.statuses && searchValues.statuses.length > 0)
            searchObj.status = {$in: searchValues.statuses};

        return this.messageApi.filterMessages(searchObj).then(messages => this.setState({messages}))
            .catch(e => this.context.showNotification(e.message, 'error', 10));
    }

    render() {
        const { i18n } = this.context;
        const {loading, messages, searchValues, showSearch} = this.state;

        return (
            <div>
                <h2>PEPPOL Access Point Monitoring</h2>
                {
                    showSearch &&
                    <div>
                        <div className="form-horizontal message-search">
                            <div className="row">
                                <div className="col-md-6">
                                    <div className="form-group">
                                        <div className="col-sm-3">
                                            <label className="control-label">ID</label>
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
                            <button className="btn btn-link" onClick={e => this.resetSearch(e)}>Reset</button>
                            <button className="btn btn-primary" onClick={e => this.handleSearch(e)}>Filter</button>
                        </div>
                        <hr/>
                    </div>
                }

                <ReactTable
                    className="message-list-table"

                    data={messages}
                    onFetchData={() => this.loadMessages()}
                    loading={loading}

                    defaultSorted={[{id: 'arrivedAt', desc: true}]}
                    minRows={0}

                    getTrProps={(state, rowInfo, instance) => {
                        if (rowInfo && rowInfo.original.status === 'failed')
                            return {style: {'background-color': '#f2dedf'}};
                        return {}
                    }}

                    columns={[
                        {
                            accessor: 'messageId',
                            Header: 'ID',
                            Cell: ({value}) => <a className="btn btn-link" onClick={this.showMessageDetail.bind(this, value)}>value</a>
                        },
                        {
                            accessor: 'filename',
                            Header: 'File Name'
                        },
                        {
                            accessor: 'status',
                            Header: 'Status'
                        },
                        {
                            accessor: 'sender',
                            Header: 'Sender'
                        },
                        {
                            accessor: 'receiver',
                            Header: 'Receiver'
                        },
                        {
                            accessor: 'accessPoint',
                            Header: 'Access Point'
                        },
                        {
                            accessor: 'source',
                            Header: 'Source'
                        },
                        {
                            accessor: 'arrivedAt',
                            Header: 'Arrived At'
                        }
                    ]}
                />
            </div>
        );
    }
}

export default PeppolMonitor;
