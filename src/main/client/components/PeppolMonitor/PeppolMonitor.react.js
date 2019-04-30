import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
import './PeppolMonitor.css';

class PeppolMonitor extends Components.ContextComponent {

    state = {};

    constructor(props, context) {
        super(props);
    }

    showPage(page, event) {
        event && event.preventDefault();
        const {router} = this.context;

        router.push(`/peppol-monitor/${page}`);
    }

    render() {
        return (
            <div>
                <h2>PEPPOL Access Point Monitoring</h2>
                <div className="form-horizontal monitoring-home">
                    <div className="row">
                        <div className="col-lg-6">
                            <a href="#" className="thumbnail" onClick={e => this.showPage('messages', e)}>
                                <span className="glyphicon glyphicon-envelope"></span>
                                Messages
                            </a>
                        </div>
                        <div className="col-lg-6">
                            <a href="#" className="thumbnail" onClick={e => this.showPage('validator', e)}>
                                <span className="glyphicon glyphicon-check"></span>
                                Validator
                            </a>
                        </div>
                        <div className="col-lg-6">
                            <a href="#" className="thumbnail" onClick={e => this.showPage('accessPoints', e)}>
                                <span className="glyphicon glyphicon-globe"></span>
                                Access Points
                            </a>
                        </div>
                        <div className="col-lg-6">
                            <a href="#" className="thumbnail" onClick={e => this.showPage('participants', e)}>
                                <span className="glyphicon glyphicon-user"></span>
                                Participants
                            </a>
                        </div>
                        <div className="col-lg-6">
                            <a href="#" className="thumbnail" onClick={e => this.showPage('documentTypes', e)}>
                                <span className="glyphicon glyphicon-file"></span>
                                Document Types
                            </a>
                        </div>
                        <div className="col-lg-6">
                            <a href="#" className="thumbnail" onClick={e => this.showPage('systemStatus', e)}>
                                <span className="glyphicon glyphicon-alert"></span>
                                System Status
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default PeppolMonitor;
