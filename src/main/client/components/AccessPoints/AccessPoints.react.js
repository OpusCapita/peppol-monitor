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

        this.renderEditable = this.renderEditable.bind(this);
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

    updateAccessPoint(accessPoint) {
        this.setState({loading: true});

        this.api.updateAccessPoint(accessPoint).then(() => {
            this.setState({loading: false});

        }).catch(e => {
            this.context.showNotification(e.message, 'error', 10);
            this.setState({loading: false});
        });
    }

    renderEditable(cellInfo) {
        return (
            <div
                className="editable-cell"
                contentEditable
                suppressContentEditableWarning
                onBlur={e => {
                    const {accessPoints} = this.state;
                    accessPoints[cellInfo.index][cellInfo.column.id] = e.target.innerHTML;
                    this.setState({accessPoints});

                    this.updateAccessPoint(accessPoints[cellInfo.index]);
                }}
                dangerouslySetInnerHTML={{
                    __html: this.state.accessPoints[cellInfo.index][cellInfo.column.id]
                }}
            />
        );
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
                            Header: 'Subject',
                            accessor: 'subject',
                        },
                        {
                            accessor: 'emailList',
                            Header: 'Email Addresses',
                            Cell: this.renderEditable,
                        },
                        {
                            accessor: 'contactPerson',
                            Header: 'Contact Person',
                            Cell: this.renderEditable,
                        },
                    ]}
                />
            </div>
        );
    }
}

export default AccessPoints;
