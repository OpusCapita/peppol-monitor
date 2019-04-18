import React from 'react';
import {Containers} from '@opuscapita/service-base-ui';
import ProcessTable from 'components/ProcessTable';
import ProcessDetail from 'components/ProcessDetail';
import AccessPoints from 'components/AccessPoints';
import ParticipantTable from 'components/ParticipantTable';
import DocumentTypes from 'components/DocumentTypes';

import {Route} from 'react-router';

const menu = (router) => (
    <ul className="nav nav-tabs">
        <li><a href="#" onClick={() => router.push('/peppol-monitor/')}>Messages</a></li>
        <li><a href="#" onClick={() => router.push('/peppol-monitor/accessPoints')}>Access Points</a></li>
        <li><a href="#" onClick={() => router.push('/peppol-monitor/participants')}>Participants</a></li>
        <li><a href="#" onClick={() => router.push('/peppol-monitor/documentTypes')}>Document Types</a></li>
    </ul>
);

const messageDetail = (props) => (
    <div>
        {menu(props.router)}
        <ProcessDetail processId={props.params.transmissionId}/>
    </div>
);

const documentTypeList = (props) => (
    <div>
        {menu(props.router)}
        <DocumentTypes/>
    </div>
);

const participantList = (props) => (
    <div>
        {menu(props.router)}
        <ParticipantTable/>
    </div>
);

const accessPointList = (props) => (
    <div>
        {menu(props.router)}
        <AccessPoints/>
    </div>
);

const messageList = (props) => (
    <div>
        {menu(props.router)}
        <ProcessTable goProcessDetail={processId => props.router.push(`/detail/${processId}`)}/>
    </div>
);

const App = () => (
    <Containers.ServiceLayout serviceName="peppol-monitor">
        <Route path="/" component={messageList}/>
        <Route path="/accessPoints" component={accessPointList}/>
        <Route path="/participants" component={participantList}/>
        <Route path="/documentTypes" component={documentTypeList}/>
        <Route path="/detail/:transmissionId" components={messageDetail}/>
    </Containers.ServiceLayout>
);

export default App;
