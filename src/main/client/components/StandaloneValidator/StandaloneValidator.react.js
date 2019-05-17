import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
import {ApiBase} from '../../api';
import ReactTable from 'react-table';
import 'react-table/react-table.css';
import './StandaloneValidator.css';

class StandaloneValidator extends Components.ContextComponent {

    state = {
        loading: false,
        result: {},
        expanded: {},
        showInfos: true,
        showErrors: true,
        showWarnings: true,
    };

    constructor(props, context) {
        super(props);
        this.api = new ApiBase();
    }

    validateFile(event) {
        const file = event.target.files[0];
        if (file && file.type !== 'text/xml') {
            this.context.showNotification('Please select an XML file', 'error', 10);
            return;
        }

        this.setState({loading: true});
        let data = new FormData();
        data.append('file', file);

        this.api.validateFile(data).then((response) => {
            response.filename = file.name;
            if (response.messages && response.messages.filter(log => log.level === 'ERROR').length === 0) {
                response.messages.push({time: 1, level: "SUCCESS", message: "Validation SUCCESSFUL!"});
            }
            this.setState({loading: false, result: response});
        }).catch(e => {
            this.setState({loading: false});
            this.context.showNotification(e.message, 'error', 10);
        });
    }

    getMessages() {
        const {result, showInfos, showErrors, showWarnings} = this.state;

        if (!result || !result.messages) {
            return []
        }

        return result.messages.filter(log => {
            if (!showInfos && log.level === 'INFO') {
                return false;
            }
            if (!showErrors && log.level === 'ERROR') {
                return false;
            }
            return !(!showWarnings && log.level === 'WARNING');
        });
    }

    getTypeLabelClass(level) {
        switch (level) {
            case 'INFO':
                return 'info';
            case 'WARNING':
                return 'warning';
            case 'ERROR':
                return 'danger';
            case 'SUCCESS':
                return 'success';
            default:
                return 'default';
        }
    }

    render() {
        const {loading, result, showInfos, showErrors, showWarnings} = this.state;

        return (
            <div>
                <h2>Standalone Validator</h2>
                <label className="btn btn-default upload-btn">
                    Select the document for validation!
                    <input type="file" hidden onChange={e => this.validateFile(e)}/>
                </label>
                <div>
                    <h3 className="text-center">{(result && result.filename) ? result.filename : 'File Name'}</h3>
                    <div className="form-horizontal transmission-detail">
                        <div className="row">
                            <div className="col-md-10 col-md-offset-1">
                                <div className="doc-type-label">
                                    {(result && result.rule) ? '(' + result.rule.id + ') ' + result.rule.description : 'Doc Type'}
                                </div>
                            </div>
                        </div>
                        <div className="row">
                            <div className="col-md-12">
                                <ReactTable
                                    className="message-detail-history-table"
                                    data={this.getMessages()}
                                    loading={loading}
                                    columns={[
                                        {
                                            expander: true,
                                            Header: () => <strong> </strong>,
                                            width: 50,
                                            Expander: ({isExpanded, ...rest}) => {
                                                if (rest.original.validationError && rest.original.validationError.identifier) {
                                                    return (
                                                        <div>
                                                            { isExpanded
                                                                ? (<span className="glyphicon glyphicon-chevron-right"/>)
                                                                : (<span className="glyphicon glyphicon-chevron-down"/>)
                                                            }
                                                        </div>
                                                    );
                                                }
                                                return null;
                                            },
                                            getProps: (state, rowInfo, column) => {
                                                if (rowInfo) {
                                                    if (!(rowInfo.original.validationError && rowInfo.original.validationError.identifier)) {
                                                        return {
                                                            onClick: () => {}
                                                        };
                                                    }
                                                }
                                                return {
                                                    className: "show-pointer"
                                                };
                                            },
                                            style: {
                                                fontSize: 15,
                                                padding: "0",
                                                margin: "auto",
                                                textAlign: "center",
                                                userSelect: "none"
                                            }
                                        },
                                        {
                                            id: 'type',
                                            width: 80,
                                            Header: 'Type',
                                            accessor: log => log,
                                            Cell: ({value}) =>
                                                <span>
                                                    <span className={`label label-${this.getTypeLabelClass(value.level)}`}>
                                                        {value.level}
                                                    </span>
                                                </span>
                                        },
                                        {
                                            id: 'code',
                                            width: 130,
                                            Header: 'Code',
                                            accessor: log => log,
                                            Cell: ({value}) =>
                                                <span>
                                                    {(value.validationError && value.validationError.identifier) ? value.validationError.identifier : 'N/A'}
                                                </span>
                                        },
                                        {
                                            id: 'message',
                                            Header: 'Message',
                                            accessor: log => log,
                                            Cell: ({value}) =>
                                                <span>
                                                    {(value.validationError && value.validationError.identifier) ? value.validationError.text : value.message}
                                                </span>
                                        }
                                    ]}
                                    sorted={[{
                                        id: 'time',
                                        desc: false
                                    }]}
                                    onExpandedChange={(expanded, index, event) => {
                                        this.setState({expanded});
                                    }}
                                    expanded={this.state.expanded}
                                    minRows={5}
                                    defaultPageSize={100}
                                    SubComponent={row => {
                                        let err = row.original.validationError;
                                        return (
                                            <ul>
                                                <li><b>Identifier:</b> err.identifier</li>
                                                <li><b>Test:</b> err.test</li>
                                                <li><b>Flag:</b> err.flag</li>
                                                <li><b>Location:</b> err.location</li>
                                                <li><b>Text:</b> err.text</li>
                                            </ul>
                                        );
                                    }}
                                />
                            </div>
                        </div>
                    </div>
                    <div className="form-submit text-right transmission-detail-actions">
                        { showInfos
                            ? <button className="btn btn-info" onClick={() => this.setState({showInfos: false})}>Hide Infos</button>
                            : <button className="btn btn-info btn-passive" onClick={() => this.setState({showInfos: true})}>Show Infos</button>
                        }
                        { showWarnings
                            ? <button className="btn btn-warning" onClick={() => this.setState({showWarnings: false})}>Hide Warnings</button>
                            : <button className="btn btn-warning btn-passive" onClick={() => this.setState({showWarnings: true})}>Show Warnings</button>
                        }
                        { showErrors
                            ? <button className="btn btn-danger" onClick={() => this.setState({showErrors: false})}>Hide Errors</button>
                            : <button className="btn btn-danger btn-passive" onClick={() => this.setState({showErrors: true})}>Show Errors</button>
                        }
                    </div>
                </div>
            </div>
        );
    }
}

export default StandaloneValidator;