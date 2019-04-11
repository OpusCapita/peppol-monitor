import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
import {ApiBase} from '../../api';
import 'react-table/react-table.css';
import ProcessTable from '../ProcessTable';
import ProcessDetail from '../ProcessDetail';
import './PeppolMonitor.css';

class PeppolMonitor extends Components.ContextComponent {

    state = {
        processDetailId: 0,
        showProcessTable: false,
        showProcessDetail: false,
        showAccessPointTable: false,
        showCustomerTable: false,
    };

    constructor(props, context) {
        super(props);
        this.api = new ApiBase();
    }

    hideAll() {
        this.setState({
            showProcessTable: false,
            showProcessDetail: false,
            showAccessPointTable: false,
            showCustomerTable: false
        });
    }

    showProcessTable(event) {
        event.preventDefault();
        this.hideAll();
        this.setState({showProcessTable: true});
    }

    showAccessPointTable(event) {
        event.preventDefault();
        this.hideAll();
        this.setState({showAccessPointTable: true});
    }

    showCustomerTable(event) {
        event.preventDefault();
        this.hideAll();
        this.setState({showCustomerTable: true});
    }

    handleBackClick(event) {
        event.preventDefault();
        this.hideAll();
    }

    showProcessDetail(processId) {
        this.hideAll();
        this.setState({processDetailId: processId, showProcessDetail: true});
    }

    render() {
        const {showProcessTable, showProcessDetail, showAccessPointTable, showCustomerTable, processDetailId} = this.state;

        return (
            <div>
                <h2>PEPPOL Access Point Monitoring</h2>
                <div>
                    {
                        !showProcessTable && !showProcessDetail && !showAccessPointTable && !showCustomerTable &&
                        <div className="form-horizontal monitoring-home">
                            <div className="row">
                                <div className="col-2 offset-3">
                                    <a href="#" className="thumbnail" onClick={event => this.showProcessTable(event)}>
                                        <span className="glyphicon glyphicon-envelope"></span>
                                        Process List
                                    </a>
                                </div>
                                <div className="col-2">
                                    <a href="#" className="thumbnail"
                                       onClick={event => this.showAccessPointTable(event)}>
                                        <span className="glyphicon glyphicon-globe"></span>
                                        Access Points
                                    </a>
                                </div>
                                <div className="col-2">
                                    <a href="#" className="thumbnail" onClick={event => this.showCustomerTable(event)}>
                                        <span className="glyphicon glyphicon-user"></span>
                                        Participants
                                    </a>
                                </div>
                            </div>
                        </div>
                    }
                    {
                        (showProcessTable || showProcessDetail || showAccessPointTable || showCustomerTable) &&
                        <button className='btn btn-default' onClick={(event) => this.handleBackClick(event)}
                                style="margin-bottom: 10px">
                            <span className="icon glyphicon glyphicon-chevron-left"/>&nbsp;
                        </button>
                    }
                    {
                        showProcessTable &&
                        <div className="process-table-wrapper">
                            <ProcessTable goProcessDetail={processId => this.showProcessDetail(processId)}/>
                        </div>
                    }
                    {
                        showProcessDetail &&
                        <div className="process-table-wrapper">
                            <ProcessDetail processId={processDetailId}/>
                        </div>
                    }
                    {
                        showAccessPointTable &&
                        <div className="access-point-wrapper">
                            <h4>Access points table here</h4>
                        </div>
                    }
                    {
                        showCustomerTable &&
                        <div className="customer-table-wrapper">
                            <h4>Customer table here</h4>
                        </div>
                    }
                </div>
            </div>
        );
    }
}

export default PeppolMonitor;
