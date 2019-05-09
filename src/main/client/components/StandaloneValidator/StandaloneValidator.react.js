import React from 'react';
import {Components} from '@opuscapita/service-base-ui';
import {ApiBase} from '../../api';
import 'react-table/react-table.css';
import './StandaloneValidator.css';

class StandaloneValidator extends Components.ContextComponent {

    state = {
        loading: false,
        result: {},
    };

    constructor(props, context) {
        super(props);
        this.api = new ApiBase();
    }

    uploadFile(event) {
        return null; // WIP

        const file = event.target.files[0];
        if (file.type !== 'text/xml') {
            this.context.showNotification('Please select an XML file', 'error', 10);
            return;
        }

        this.setState({loading: true});
        let data = new FormData();
        data.append('file', file);

        this.api.uploadFile(this.props.transmissionId, data).then(() => {
            this.setState({loading: false});
            this.context.showNotification('Successfully updated the file', 'success', 10);
        }).catch(e => {
            this.setState({loading: false});
            this.context.showNotification(e.message, 'error', 10);
        });
    }

    render() {
        const {loading, result} = this.state;

        return (
            <div>
                <h3>Standalone Validator</h3>
                <label className="btn btn-default">
                    Upload<input type="file" hidden onChange={e => this.uploadFile(e)}/>
                </label>
                <label>Work In Progress</label>
            </div>
        );
    }
}

export default StandaloneValidator;
