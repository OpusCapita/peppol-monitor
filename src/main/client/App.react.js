import React from 'react';
import {Containers} from '@opuscapita/service-base-ui';
import PeppolMonitor from './components/PeppolMonitor';
import TransmissionTable from './components/TransmissionTable';
import TransmissionDetail from './components/TransmissionDetail';
import AccessPoints from './components/AccessPoints';
import ParticipantTable from './components/ParticipantTable';
import DocumentTypes from './components/DocumentTypes';
import SystemStatus from './components/SystemStatus';
import StandaloneValidator from './components/StandaloneValidator';
import StandaloneSender from './components/StandaloneSender';
import AdvancedOperations from './components/AdvancedOperations';

import {Route} from 'react-router';

const menuButton = (router) => (
    <div className="footer-wrapper">
        <a className='btn btn-default' href="#" onClick={() => router.push('/peppol-monitor/')}>
            <span className="icon glyphicon glyphicon-chevron-left"/> Go to Menu
        </a>
    </div>
);

const messageDetail = (props) => (
    <div>
        <TransmissionDetail transmissionId={props.params.transmissionId}/>
    </div>
);

const advancedOperations = (props) => (
    <div>
        <AdvancedOperations/>
        {menuButton(props.router)}
    </div>
);

const systemStatus = (props) => (
    <div>
        <SystemStatus/>
        {menuButton(props.router)}
    </div>
);

const documentTypeList = (props) => (
    <div>
        <DocumentTypes/>
        {menuButton(props.router)}
    </div>
);

const participantList = (props) => (
    <div>
        <ParticipantTable/>
        {menuButton(props.router)}
    </div>
);

const accessPointList = (props) => (
    <div>
        <AccessPoints/>
        {menuButton(props.router)}
    </div>
);

const validator = (props) => (
    <div>
        <StandaloneValidator/>
        {menuButton(props.router)}
    </div>
);

const sender = (props) => (
    <div>
        <StandaloneSender/>
        {menuButton(props.router)}
    </div>
);

const messageList = (props) => (
    <div>
        <TransmissionTable/>
        {menuButton(props.router)}
    </div>
);

const home = (props) => (
    <PeppolMonitor/>
);

const App = () => (
    <Containers.ServiceLayout serviceName="peppol-monitor">
        <Route path="/" component={home}/>
        <Route path="/messages" component={messageList}/>
        <Route path="/validator" component={validator}/>
        <Route path="/sender" component={sender}/>
        <Route path="/accessPoints" component={accessPointList}/>
        <Route path="/participants" component={participantList}/>
        <Route path="/documentTypes" component={documentTypeList}/>
        <Route path="/systemStatus" component={systemStatus}/>
        <Route path="/advancedOperations" component={advancedOperations}/>
        <Route path="/messageDetail/:transmissionId" components={messageDetail}/>
    </Containers.ServiceLayout>
);

export default App;
