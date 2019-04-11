import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
import ReactTable from 'react-table';
import {ApiBase} from '../../api';
import 'react-table/react-table.css';
import './ParticipantTable.css';

class ParticipantTable extends Components.ContextComponent {

    state = {
        loading: false,
        participants: [],
    };

    constructor(props, context) {
        super(props);
        this.api = new ApiBase();
    }

    async loadParticipants() {
        this.setState({loading: true});

        try {
            const response = await this.api.getParticipants();
            this.setState({participants: response});
        }
        catch (e) {
            this.context.showNotification(e.message, 'error', 10);
        }
        finally {
            this.setState({loading: false});
        }
    }

    render() {
        const {loading, participants} = this.state;

        return (
            <div>
                <ReactTable
                    className="participants-table"
                    loading={loading}
                    data={participants}
                    filterable={true}
                    onFetchData={() => this.loadParticipants()}
                    minRows={10}
                    defaultPageSize={10}
                    columns={[
                        {
                            Header: 'ID',
                            accessor: 'id',
                        },
                        {
                            Header: 'Name',
                            accessor: 'name',
                        },
                        {
                            id: 'emailList',
                            accessor: 'emailList',
                            Header: 'Email Addresses',
                        },
                        {
                            accessor: 'contactPerson',
                            Header: 'Contact Person',
                        },
                    ]}
                />
            </div>
        );
    }
}

export default ParticipantTable;
