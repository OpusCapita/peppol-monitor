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
        if (file.type !== 'text/xml') {
            this.context.showNotification('Please select an XML file', 'error', 10);
            return;
        }

        this.setState({loading: true});
        let data = new FormData();
        data.append('file', file);

        this.api.validateFile(data).then((response) => {
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

        if (result.messages.length === 0) {
            return [{time: 1, level: "INFO", message: "Validation SUCCESSFUL!"}];
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
            default:
                return 'default';
        }
    }

    render() {
        const {loading, result, showInfos, showErrors, showWarnings} = this.state;

        return (
            <div>
                <h3>Standalone Validator</h3>
                <label className="btn btn-default upload-btn">
                    Select the document for validation!
                    <input type="file" hidden onChange={e => this.validateFile(e)}/>
                </label>
                <div>
                    <h3>Validation Result</h3>
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
                                            width: 120,
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
                                            accessor: 'message',
                                        }
                                    ]}
                                    sorted={[{
                                        id: 'time',
                                        desc: true
                                    }]}
                                    minRows={5}
                                    defaultPageSize={100}
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