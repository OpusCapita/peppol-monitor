'use strict';

const React = require('react');

export default class MessageTableRow extends React.Component {
    render() {
        return (
            <tr>
                <td>{this.props.message.messageId}</td>
                <td>{this.props.message.filename}</td>
                <td>{this.props.message.status}</td>
                <td>{this.props.message.sender.id}</td>
                <td>{this.props.message.receiver.id}</td>
                <td>{this.props.message.accessPoint}</td>
                <td>{this.props.message.source}</td>
                <td>{this.props.message.direction}</td>
                <td>{this.props.message.arrivedAt}</td>
            </tr>
        )
    }
}