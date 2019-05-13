import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
import {ApiBase} from '../../api';
import './SystemStatus.css';

class SystemStatus extends Components.ContextComponent {

    state = {
        loading: false,
        results: {
            "peppol-inbound": "...",
            "peppol-processor": "...",
            "peppol-validator": "...",
            "peppol-outbound": "...",
            "peppol-monitor": "...",
            "peppol-mlr-reporter": "...",
            "peppol-xib-adaptor": "...",
        }
    };

    constructor(props, context) {
        super(props);
        this.api = new ApiBase();
    }

    componentDidMount() {
        for (var serviceName in this.state.results) {
            this.api.getStatus(serviceName).then(result => {
                const {results} = this.state;
                results[serviceName] = result;
                this.setState({results: results});

            }).catch(e => {
                this.context.showNotification(e.message, 'error', 10);
            });
        }
    }

    render() {
        const {loading, results} = this.state;

        return (
            <div>
                <h3>System Status</h3>
                {
                    Object.keys(results).map((service, i) => {
                        return (
                            <div key={i} className="row status-line">
                                <div className="col-md-4 col-md-offset-1">{service}</div>
                                <div className="col-md-7 text-right">{results[service]}</div>
                            </div>
                        )
                    })
                }
            </div>
        );
    }
}

export default SystemStatus;