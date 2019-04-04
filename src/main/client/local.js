'use strict';
import 'babel-polyfill';
import {Route} from 'react-router';
import SupportHome from './components/SupportHome';
import {Containers} from '@opuscapita/service-base-ui';

const React = require('react');
const ReactDOM = require('react-dom');

const home = () => (
    <div className={'wrapper'}>
        <h2>PEPPOL OC Access Point Monitoring</h2>
        <hr/>
        <SupportHome/>
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