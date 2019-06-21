import React from 'react';
import PropTypes from 'prop-types';
import {Components} from '@opuscapita/service-base-ui';
import {ApiBase} from '../../api';
import './AdvancedOperations.css';

class AdvancedOperations extends Components.ContextComponent {

    state = {
        loading: false,
        rawFile: null,
        transmissionList: []
    };

    constructor(props, context) {
        super(props);
        this.api = new ApiBase();
    }

    loadFile(event) {
        const file = event.target.files[0];
        if (!file) {
            return;
        }

        const reader = new FileReader();
        reader.onload = (e) => {
            const raw = e.target.result;
            if (raw) {
                this.setState({rawFile: file});
                this.setState({transmissionList: raw.split("\n")});
                this.context.showNotification(this.state.transmissionList.length + ' transmissions loaded', 'info', 3);
            }
        };
        reader.readAsText(file);
    }

    async bulkReprocess() {

    }

    async bulkMarkAsFixed() {

    }

    async bulkSendMlr() {
        const {transmissionList} = this.state;
        const {showModalDialog, hideModalDialog} = this.context;

        const onConfirmationClick = (btn) => {
            hideModalDialog();

            if (btn === 'yes') {
                this.setState({loading: true});

                setTimeout(() => {
                    this.api.sendMlrsAdvanced(transmissionList).then(() => {
                        this.setState({loading: false});
                        this.context.showNotification('MLR sending operation of the messages has been started', 'info', 3);
                    }).catch(e => {
                        this.setState({loading: false});
                        this.context.showNotification(e.message, 'error', 10);
                    });

                }, 500);
            }
        }

        const modalTitle = "Bulk Send MLR";
        const modalText = `${transmissionList.length} transmissions will be processed in the background. New MLR report will be created for each one according to its status and will be send to the owner\n\nDo you want to continue?`;
        const modalButtons = {no: 'No', yes: 'Yes'};

        showModalDialog(modalTitle, modalText, onConfirmationClick, modalButtons);
    }

    render() {
        return (
            <div>
                <h2>Advanced Operations</h2>
                <label className="btn btn-default upload-btn">
                    Select the text file to load the transmissions
                    <input type="file" hidden onChange={e => this.loadFile(e)}/>
                </label>
                <div className="form-submit text-right advanced-actions">
                    <button className="btn btn-default" onClick={() => this.bulkReprocess()}>Reprocess</button>
                    <button className="btn btn-default" onClick={() => this.bulkMarkAsFixed()}>Mark as Fixed</button>
                    <button className="btn btn-default" onClick={() => this.bulkSendMlr()}>Send MLR</button>
                </div>
            </div>
        );
    }
}

export default AdvancedOperations;
