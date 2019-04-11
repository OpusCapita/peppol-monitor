import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
import {ApiBase} from '../../api';
import 'react-table/react-table.css';
import ProcessTable from '../ProcessTable';
import ProcessDetail from '../ProcessDetail';
import AccessPoints from '../AccessPoints';
import ParticipantTable from '../ParticipantTable';
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
                                <div className="col-2">
                                    <a href="#" className="thumbnail">
                                        <span className="glyphicon glyphicon-file"></span>
                                        Document Types
                                    </a>
                                </div>
                            </div>
                        </div>
                    }
                    {
                        showProcessTable &&
                        <div className="process-table-wrapper">
                            <ProcessTable goProcessDetail={processId => this.showProcessDetail(processId)}/>
                        </div>
                    }
                    {
                        showProcessDetail &&
                        <div className="process-detail-wrapper">
                            <ProcessDetail processId={processDetailId}/>
                        </div>
                    }
                    {
                        showAccessPointTable &&
                        <div className="access-point-wrapper">
                            <AccessPoints/>
                        </div>
                    }
                    {
                        showCustomerTable &&
                        <div className="customer-table-wrapper">
                            <ParticipantTable/>
                        </div>
                    }
                </div>
                {
                    (showProcessTable || showProcessDetail || showAccessPointTable || showCustomerTable) &&
                    <div className="footer-wrapper">
                        <button className='btn btn-default' onClick={(event) => this.handleBackClick(event)}>
                            <span className="icon glyphicon glyphicon-chevron-left"/> Go to Menu
                        </button>
                    </div>
                }
            </div>
        );
    }
}

export default PeppolMonitor;
