import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
import {ApiBase} from '../../api';
import 'react-table/react-table.css';
import ProcessTable from '../ProcessTable';
import ProcessDetail from '../ProcessDetail';
import AccessPoints from '../AccessPoints';
import ParticipantTable from '../ParticipantTable';
import DocumentTypes from '../DocumentTypes';
import './PeppolMonitor.css';

class PeppolMonitor extends Components.ContextComponent {

    state = {
        show: {
            processTable: false,
            processDetail: false,
            accessPointTable: false,
            customerTable: false,
            documentTypes: false,
        },
        processDetailId: 0,
    };

    constructor(props, context) {
        super(props);
        this.api = new ApiBase();
    }

    hideAll() {
        const {show} = this.state;
        Object.keys(show).forEach((p) => {
            show[p] = false;
        });
        this.setState({show});
    }

    isAllHide() {
        const {show} = this.state;
        return Object.keys(show).filter((p) => show[p]).length === 0;
    }

    showPage(page, event) {
        event && event.preventDefault();

        const {show} = this.state;
        Object.keys(show).forEach((p) => {
            show[p] = page === p;
        });
        this.setState({show});
    }

    goProcessDetail(processId) {
        this.setState({processDetailId: processId});
        this.showPage('processDetail')
    }

    render() {
        const {show, processDetailId} = this.state;

        return (
            <div>
                <h2>PEPPOL Access Point Monitoring</h2>
                <div>
                    {
                        this.isAllHide() &&
                        <div className="form-horizontal monitoring-home">
                            <div className="row">
                                <div className="col-12">
                                    <a href="#" className="thumbnail" onClick={e => this.showPage('processTable', e)}>
                                        <span className="glyphicon glyphicon-envelope"></span>
                                        Process List
                                    </a>
                                </div>
                                <div className="col-12">
                                    <a href="#" className="thumbnail" onClick={e => this.showPage('accessPointTable', e)}>
                                        <span className="glyphicon glyphicon-globe"></span>
                                        Access Points
                                    </a>
                                </div>
                                <div className="col-12">
                                    <a href="#" className="thumbnail" onClick={e => this.showPage('customerTable', e)}>
                                        <span className="glyphicon glyphicon-user"></span>
                                        Participants
                                    </a>
                                </div>
                                <div className="col-12">
                                    <a href="#" className="thumbnail" onClick={e => this.showPage('documentTypes', e)}>
                                        <span className="glyphicon glyphicon-file"></span>
                                        Document Types
                                    </a>
                                </div>
                            </div>
                        </div>
                    }
                    {
                        show.processTable &&
                        <ProcessTable goProcessDetail={processId => this.goProcessDetail(processId)}/>
                    }
                    {
                        show.processDetail &&
                        <ProcessDetail processId={processDetailId}/>
                    }
                    {
                        show.accessPointTable &&
                        <AccessPoints/>
                    }
                    {
                        show.customerTable &&
                        <ParticipantTable/>
                    }
                    {
                        show.documentTypes &&
                        <DocumentTypes/>
                    }
                </div>
                {
                    (!this.isAllHide()) &&
                    <div className="footer-wrapper">
                        <button className='btn btn-default' onClick={() => this.hideAll()}>
                            <span className="icon glyphicon glyphicon-chevron-left"/> Go to Menu
                        </button>
                    </div>
                }
            </div>
        );
    }
}

export default PeppolMonitor;
