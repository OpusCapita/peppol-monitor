import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
import Select from '@opuscapita/react-select';
import {ApiBase} from '../../api';
import './SystemStatus.css';

class SystemStatus extends Components.ContextComponent {

    state = {
        loading: false,
        selectedYear: new Date().getFullYear(),
        selectedMonth: new Date().getMonth() + 1,
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

    handleYearChange(value) {
        this.setState({selectedYear: value});
    }

    handleMonthChange(value) {
        this.setState({selectedMonth: value});
    }

    getYearOptions() {
        const result = [];
        for (var i = 2019; i < 2119; i++) {
            result.push({value: i, label: i});
        }
        return result;
    }

    getMonthOptions() {
        const result = [];
        result.push({value: 1, label: 'January'});
        result.push({value: 2, label: 'February'});
        result.push({value: 3, label: 'March'});
        result.push({value: 4, label: 'April'});
        result.push({value: 5, label: 'May'});
        result.push({value: 6, label: 'June'});
        result.push({value: 7, label: 'July'});
        result.push({value: 8, label: 'August'});
        result.push({value: 9, label: 'September'});
        result.push({value: 10, label: 'October'});
        result.push({value: 11, label: 'November'});
        result.push({value: 12, label: 'December'});
        return result;
    }

    getStats() {
        const {selectedYear, selectedMonth} = this.state;

        const startYear = selectedYear;
        const startMonth = selectedMonth < 10 ? `0${selectedMonth}` : selectedMonth;
        const endYear = selectedMonth === 12 ? selectedYear + 1 : selectedYear;
        const endMonth = ((selectedMonth + 1) % 12) < 10 ? `0${(selectedMonth + 1) % 12}` : ((selectedMonth + 1) % 12);

        const period = `${startYear}-${startMonth}`;
        const from = `${period}-01`;
        const to = `${endYear}-${endMonth}-01`;

        this.context.showSpinner();
        this.api.getStatistics(from, to).then(stats => {
            this.context.hideSpinner();

            let csvContent = "data:text/csv;charset=utf-8,";
            stats.forEach((stat) => {
                let row = `${period},${stat.doc_type},${stat.direction},${stat.files}`;
                csvContent += row + "\r\n";
            });
            const encodedUri = encodeURI(csvContent);

            const a = document.createElement("a");
            a.setAttribute("href", encodedUri);
            a.setAttribute("download", `PEPPOL-OpusCapitaAPStatistics-${period}.csv`);
            a.click();

        }).catch(e => {
            this.context.hideSpinner();
            this.context.showNotification(e.message, 'error', 10);
        });
    }

    render() {
        const {selectedYear, selectedMonth, loading, results} = this.state;

        return (
            <div>
                <h3>System Status</h3>
                <div className="row">
                    <div className="col-md-2 col-md-offset-4">
                        <div className="form-group">
                            <Select className="react-select"
                                    options={this.getYearOptions()}
                                    onChange={value => this.handleYearChange(value)}
                                    value={selectedYear}
                            />
                        </div>
                    </div>
                    <div className="col-md-2">
                        <div className="form-group">
                            <Select className="react-select"
                                    options={this.getMonthOptions()}
                                    onChange={value => this.handleMonthChange(value)}
                                    value={selectedMonth}
                            />
                        </div>
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-2 col-md-offset-5 text-center">
                        <div className="form-group">
                            <a className="btn btn-default" onClick={() => this.getStats()}>Get Statistics</a>
                        </div>
                    </div>
                </div>
                <hr />
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