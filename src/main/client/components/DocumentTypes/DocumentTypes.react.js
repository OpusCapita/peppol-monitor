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

    copyToClipboard(txt) {
        const el = document.createElement('textarea');
        el.value = txt;
        el.setAttribute('readonly', '');
        el.style.position = 'absolute';
        el.style.left = '-9999px';
        document.body.appendChild(el);
        el.select();
        document.execCommand('copy');
        document.body.removeChild(el);
        this.context.showNotification('Value copied to clipboard', 'info', 1);
    }

    render() {
        const {loading, documentTypes} = this.state;

        return (
            <div>
                <h3>Supported Document Types</h3>
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
                            id: 'id',
                            width: 50,
                            Header: 'ID',
                            accessor: 'id',
                        },
                        {
                            Header: 'Description',
                            accessor: 'description',
                        },
                        {
                            id: 'archetype',
                            width: 100,
                            Header: 'Archetype',
                            accessor: 'archetype',
                        },
                        {
                            id: 'localName',
                            width: 120,
                            Header: 'Local Name',
                            accessor: 'localName',
                        },
                        {
                            id: 'documentId',
                            Header: 'Document Identifier',
                            accessor: 'documentId',
                            Cell: ({value}) =>
                                <span className="copy-link" onClick={this.copyToClipboard.bind(this, value)}>
                                    {value}
                                </span>
                        },
                        {
                            id: 'processId',
                            Header: 'Profile Identifier',
                            accessor: 'processId',
                            Cell: ({value}) =>
                                <span className="copy-link" onClick={this.copyToClipboard.bind(this, value)}>
                                    {value}
                                </span>
                        },
                    ]}
                />
            </div>
        );
    }
}

export default DocumentTypes;
