import React from 'react';
import {ApiBase} from '../../api';
import Select from '@opuscapita/react-select';
import {Components} from '@opuscapita/service-base-ui';
import './StandaloneSender.css';

class StandaloneSender extends Components.ContextComponent {

    static sources = [
        'NETWORK',
        'A2A',
        'XIB',
        'SIRIUS'
    ];

    state = {
        data: null,
        source: null
    };

    constructor(props, context) {
        super(props);
        this.api = new ApiBase();
    }

    mapSourcesSelect() {
        return StandaloneSender.sources.map(value => {
            return {value: value, label: value};
        });
    }

    handleSourceSelect(source) {
        this.setState({source});
    }

    loadFile(event) {
        const file = event.target.files[0];
        if (file && file.type !== 'text/xml') {
            this.context.showNotification('Please select an XML file', 'error', 3);
            return;
        }

        let data = new FormData();
        data.append('file', file);
        data.append('filename', file.name);
        this.setState({data});
    }

    sendFile() {
        if (!this.state.source) {
            this.context.showNotification('Please select the Source', 'error', 3);
            return;
        }

        this.context.showSpinner();
        this.api.sendFile(this.state.data, this.state.source.value, this.context.userData.id).then(() => {
            this.context.showNotification('The file has been sent to Access Point', 'info', 3);
        }).catch(e => {
            this.context.showNotification(e.message, 'error', 10);
        }).finally(() => {
            this.context.hideSpinner();
        })
    }

    render() {
        const {source} = this.state;

        return (
            <div>
                <h2>Standalone Sender</h2>
                <label className="btn btn-default upload-btn">
                    Select the file to send!
                    <input type="file" hidden onChange={e => this.loadFile(e)}/>
                </label>
                <br/>
                <div className="form-horizontal transmission-search">
                    <div className="row">
                        <div className="col-md-3">&nbsp;</div>
                        <div className="col-md-6">
                            <div className="form-group">
                                <div className="col-sm-3">
                                    <label className="control-label">Source</label>
                                </div>
                                <div className="offset-md-1 col-md-8">
                                    <Select className="react-select" isMulti={false}
                                            options={this.mapSourcesSelect()}
                                            onChange={value => this.handleSourceSelect(value)}
                                            value={source}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="form-submit text-right transmission-detail-actions">
                    <button className="btn btn-primary" onClick={() => this.sendFile()}>Send</button>
                </div>

            </div>
        );
    }
}

export default StandaloneSender;