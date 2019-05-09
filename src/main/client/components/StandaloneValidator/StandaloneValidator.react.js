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

    validateFile(event) {
        const file = event.target.files[0];
        if (file.type !== 'text/xml') {
            this.context.showNotification('Please select an XML file', 'error', 10);
            return;
        }

        this.setState({loading: true});
        let data = new FormData();
        data.append('file', file);

        this.api.validateFile(data).then((response) => {
            this.setState({loading: false, result: response});
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
                <label className="btn btn-default upload-btn">
                    Select the document for validation!
                    <input type="file" hidden onChange={e => this.validateFile(e)}/>
                </label>
                <h4 className="text-center">{JSON.stringify(result, null, 4)}</h4>
            </div>
        );
    }
}

export default StandaloneValidator;