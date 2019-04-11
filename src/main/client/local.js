'use strict';
import 'babel-polyfill';
import PeppolMonitor from './components/PeppolMonitor';

const React = require('react');
const ReactDOM = require('react-dom');

const Local = () => (
    <div className={'wrapper'}>
        <PeppolMonitor/>
    </div>
);

ReactDOM.render(
    <Local/>,
    document.getElementById('react')
);

export default Local;