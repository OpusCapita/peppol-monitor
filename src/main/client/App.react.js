import React from 'react';
import {Containers} from '@opuscapita/service-base-ui';
import PeppolMonitor from './components/PeppolMonitor';
import ProcessTable from './components/ProcessTable';
import ProcessDetail from './components/ProcessDetail';
import AccessPoints from './components/AccessPoints';
import ParticipantTable from './components/ParticipantTable';
import DocumentTypes from './components/DocumentTypes';

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
        <ProcessDetail processId={props.params.transmissionId}/>
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

const messageList = (props) => (
    <div>
        <ProcessTable/>
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
        <Route path="/accessPoints" component={accessPointList}/>
        <Route path="/participants" component={participantList}/>
        <Route path="/documentTypes" component={documentTypeList}/>
        <Route path="/messageDetail/:transmissionId" components={messageDetail}/>
    </Containers.ServiceLayout>
);

export default App;
