'use strict';
import 'babel-polyfill';
import PeppolMonitor from './components/PeppolMonitor';
import PeppolMonitorDetail from './components/PeppolMonitorDetail';

const React = require('react');
const ReactDOM = require('react-dom');

const Local = () => (
    <div className={'wrapper'}>
        <PeppolMonitor/>
        {/*<PeppolMonitorDetail messageId={2}/>*/}
    </div>
);

ReactDOM.render(
    <Local/>,
    document.getElementById('react')
);

export default Local;