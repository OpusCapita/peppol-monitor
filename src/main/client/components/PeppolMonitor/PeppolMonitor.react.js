import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
import './PeppolMonitor.css';

class PeppolMonitor extends Components.ContextComponent {

    state = {};

    constructor(props, context) {
        super(props);
    }

    componentDidMount() {
        const page = this.context.router.location.query.r;

        if (page) {
            this.showPage(page)
        }
    }

    showPage(page, event) {
        event && event.preventDefault();
        this.context.router.push(`/peppol-monitor/${page}`);
    }

    render() {
        // noinspection HtmlUnknownTarget
        return (
            <div>
                <h2>PEPPOL Access Point Monitoring</h2>
                <div className="form-horizontal monitoring-home">
                    <div className="row">
                        <div className="col-lg-6">
                            <a href="/peppol-monitor?r=messages" className="thumbnail"
                               onClick={e => this.showPage('messages', e)}>
                                <span className="glyphicon glyphicon-envelope"></span>
                                Messages
                            </a>
                        </div>
                        <div className="col-lg-6">
                            <a href="/peppol-monitor?r=validator" className="thumbnail"
                               onClick={e => this.showPage('validator', e)}>
                                <span className="glyphicon glyphicon-check"></span>
                                Validator
                            </a>
                        </div>
                        <div className="col-lg-6">
                            <a href="/peppol-monitor?r=sender" className="thumbnail"
                               onClick={e => this.showPage('sender', e)}>
                                <span className="glyphicon glyphicon-send"></span>
                                Sender
                            </a>
                        </div>
                        <div className="col-lg-6">
                            <a href="/peppol-monitor?r=accessPoints" className="thumbnail"
                               onClick={e => this.showPage('accessPoints', e)}>
                                <span className="glyphicon glyphicon-globe"></span>
                                Access Points
                            </a>
                        </div>
                        <div className="col-lg-6">
                            <a href="/peppol-monitor?r=participants" className="thumbnail"
                               onClick={e => this.showPage('participants', e)}>
                                <span className="glyphicon glyphicon-user"></span>
                                Participants
                            </a>
                        </div>
                        <div className="col-lg-6">
                            <a href="/peppol-monitor?r=documentTypes" className="thumbnail"
                               onClick={e => this.showPage('documentTypes', e)}>
                                <span className="glyphicon glyphicon-file"></span>
                                Document Types
                            </a>
                        </div>
                        <div className="col-lg-6">
                            <a href="/peppol-monitor?r=systemStatus" className="thumbnail"
                               onClick={e => this.showPage('systemStatus', e)}>
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
