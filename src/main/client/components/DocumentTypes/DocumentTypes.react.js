import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
import ReactTable from 'react-table';
import {ApiBase} from '../../api';
import 'react-table/react-table.css';
import './DocumentTypes.css';

class DocumentTypes extends Components.ContextComponent {

    state = {
        loading: false,
        documentTypes: [],
    };

    constructor(props, context) {
        super(props);
        this.api = new ApiBase();
    }

    async loadDocumentTypes() {
        this.setState({loading: true});

        try {
            const response = await this.api.getDocumentTypes();
            this.setState({documentTypes: response});
        }
        catch (e) {
            this.context.showNotification(e.message, 'error', 10);
        }
        finally {
            this.setState({loading: false});
        }
    }

    render() {
        const {loading, documentTypes} = this.state;

        return (
            <div>
                <ReactTable
                    className="document-types-table"
                    loading={loading}
                    data={documentTypes}
                    filterable={true}
                    onFetchData={() => this.loadDocumentTypes()}
                    minRows={10}
                    defaultPageSize={10}
                    columns={[
                        {
                            Header: 'ID',
                            accessor: 'id',
                        },
                        {
                            Header: 'Description',
                            accessor: 'description',
                        },
                        {
                            Header: 'Archetype',
                            accessor: 'archetype',
                        },
                        {
                            Header: 'Local Name',
                            accessor: 'localName',
                        },
                        {
                            Header: 'Document Identifier',
                            accessor: 'documentId',
                        },
                        {
                            Header: 'Profile Identifier',
                            accessor: 'processId',
                        },
                    ]}
                />
            </div>
        );
    }
}

export default DocumentTypes;
