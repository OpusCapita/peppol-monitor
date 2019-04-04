'use strict';
import 'babel-polyfill';
import {Route} from 'react-router';
import PeppolMonitor from './components/PeppolMonitor';
import {Containers} from '@opuscapita/service-base-ui';

const React = require('react');
const ReactDOM = require('react-dom');

const home = () => (
    <div className={'wrapper'}>
        <PeppolMonitor/>
    </div>
);

const Local = () => (
    <Containers.ServiceLayout serviceName="user">
        <Route path="/" component={home}/>
    </Containers.ServiceLayout>
);

ReactDOM.render(
    <Local/>,
    document.getElementById('react')
);

export default Local;