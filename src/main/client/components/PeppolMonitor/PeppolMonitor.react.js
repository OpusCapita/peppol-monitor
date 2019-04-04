import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
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
        const {i18n} = this.context;
        const {loading, messages, searchValues, showSearch} = this.state;

        return (
            <div>
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
                                                   onChange={e => this.handleSearchFormChange('id', e.target.value)}/>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <div className="col-sm-3">
                                            <label
                                                className="control-label">File Name</label>
                                        </div>
                                        <div className="offset-md-1 col-md-8">
                                            <input type="text" className="form-control" value={searchValues.filename}
                                                   onChange={e => this.handleSearchFormChange('filename', e.target.value)}/>
                                        </div>
                                    </div>
                                    <div className="form-group">
                                        <div className="col-sm-3">
                                            <label
                                                className="control-label">Participant</label>
                                        </div>
                                        <div className="offset-md-1 col-md-8">
                                            <input type="text" className="form-control" value={searchValues.participant}
                                                   onChange={e => this.handleSearchFormChange('participant', e.target.value)}/>
                                        </div>
                                    </div>
                                </div>
                                <div className="col-md-6">
                                    <div className="form-group">
                                        <div className="col-sm-3">
                                            <label
                                                className="control-label">Access Point</label>
                                        </div>
                                        <div className="offset-md-1 col-md-8">
                                            <input type="text" className="form-control" value={searchValues.accessPoint}
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
                                                placeholder=""
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
                                                placeholder=""
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
                            Header: 'ID'
                        },
                        {
                            accessor: 'filename',
                            Header: 'File Name'
                        },
                        {
                            accessor: 'status',
                            Header: 'Status',
                            Cell: ({val}) => <span className={`label label-default label-${val}`}>{val}</span>
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
                            Header: 'Source',
                            Cell: ({val}) => <span className={`label label-default label-${val}`}>{val}</span>
                        },
                        {
                            accessor: 'arrivedAt',
                            Header: 'Arrived At',
                            Cell: ({val}) => i18n.formatDate(val)
                        }
                        // {
                        //     id: 'actions',
                        //     accessor: user => user,
                        //     width: 200,
                        //     Cell: ({value}) =>
                        //         <nobr>
                        //             {
                        //                 this.shouldShowEdit(value) &&
                        //                 <button type="button" className="btn btn-sm btn-default"
                        //                         onClick={() => this.props.onEdit(value.id)}>
                        //                     <span
                        //                         className="icon glyphicon glyphicon-pencil"/>&nbsp;{i18n.getMessage('UserList.table.column.actions.edit')}
                        //                 </button>
                        //             }
                        //         </nobr>
                        // }
                    ]}
                />
            </div>
        );
    }
}

export default PeppolMonitor;
