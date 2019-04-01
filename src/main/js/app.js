'use strict';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

import MessageTable from './component/messageTable';

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {messages: []};
    }

    componentDidMount() {
        client({method: 'GET', path: '/api/messages/0'}).done(response => {
            this.setState({messages: response.entity._embedded.messages});
        });
    }

    render() {
        return (
            <MessageTable messages={this.state.messages}/>
        )
    }
}

ReactDOM.render(
    <App/>,
    document.getElementById('react')
);
