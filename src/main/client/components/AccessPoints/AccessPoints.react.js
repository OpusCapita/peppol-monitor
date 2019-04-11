import React from 'react';
import PropTypes from 'prop-types';
import {Components} from '@opuscapita/service-base-ui';
import ReactTable from 'react-table';
import {ApiBase} from '../../api';
import 'react-table/react-table.css';
import './AccessPoints.css';

class AccessPoints extends Components.ContextComponent {

    state = {
        loading: false,
        accessPoints: [],
    };

    static propTypes = {
        accessPointId: PropTypes.string
    };

    constructor(props, context) {
        super(props);
        this.api = new ApiBase();
    }

    async loadAccessPoints() {
        this.setState({loading: true});

        try {
            const response = await this.api.getAccessPoints();
            this.setState({accessPoints: response});
        }
        catch (e) {
            this.context.showNotification(e.message, 'error', 10);
        }
        finally {
            this.setState({loading: false});
        }
    }

    render() {
        const {loading, accessPoints} = this.state;

        return (
            <div>
                <ReactTable
                    className="access-points-table"
                    loading={loading}
                    data={accessPoints}
                    filterable={true}
                    onFetchData={() => this.loadAccessPoints()}
                    minRows={10}
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
                            Header: 'subject',
                            accessor: 'subject',
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

export default AccessPoints;
