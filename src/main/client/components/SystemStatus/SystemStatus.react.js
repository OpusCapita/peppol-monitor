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
            if (this.state.results.hasOwnProperty(serviceName)) {
                this.fetchServiceStatus(serviceName);
            }
        }
    }

    fetchServiceStatus(serviceName) {
        this.api.getStatus(serviceName).then(result => {
            const {results} = this.state;

            if (result && result.statusCode) {
                results[serviceName] = result.statusCode + ': ';
            } else {
                results[serviceName] = '500: ';
            }

            if (result && result.body) {
                results[serviceName] += result.body.message;
            } else if (results[serviceName].startsWith("2")) {
                results[serviceName] += 'OK!';
            } else {
                results[serviceName] += 'Unknown Exception';
            }

            this.setState({results: results});

        }).catch(e => {
            const {results} = this.state;
            results[serviceName] = (e && e.code && e.message) ? e.code + ': ' + e.message : "500: Unknown Exception";
            this.setState({results: results});
        });
    }

    render() {
        const {loading, results} = this.state;

        return (
            <div>
                <h3>System Status</h3>
                {
                    Object.keys(results).map((service, i) => {
                        return (
                            <div key={i} className={
                                results[service].startsWith("...") ? 'row status-line' :
                                    (results[service].startsWith("2") ? 'row status-line green' :
                                        'row status-line red')
                            }>
                                <div className="col-md-4">{service}</div>
                                <div className="col-md-8 text-right">{results[service]}</div>
                            </div>
                        )
                    })
                }
            </div>
        );
    }
}

export default SystemStatus;