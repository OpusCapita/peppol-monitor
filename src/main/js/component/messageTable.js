'use strict';

const React = require('react');

import MessageTableRow from './messageTableRow';

export default class MessageTable extends React.Component {
    render() {
        const messages = this.props.messages.map(message =>
            <MessageTableRow key={message.id} message={message}/>
        );
        return (
            <table>
                <tbody>
                <tr>
                    <th>Id</th>
                    <th>Filename</th>
                    <th>Status</th>
                    <th>Sender</th>
                    <th>Receiver</th>
                    <th>Access Point</th>
                    <th>Source</th>
                    <th>Direction</th>
                    <th>Arrived</th>
                </tr>
                {messages}
                </tbody>
            </table>
        )
    }
}