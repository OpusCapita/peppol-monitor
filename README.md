# peppol-monitor [![CircleCI](https://circleci.com/gh/OpusCapita/peppol-monitor.svg?style=svg)](https://circleci.com/gh/OpusCapita/peppol-monitor)

Peppol OpusCapita Access Point monitor service running on Andariel Platform.

The service reads files from the `peppol.monitor.queue.in.name:peppol.message.monitor` queue and processes them. The processing includes:

* Saving metadata of the file
* Saving history of the file

After processing, monitoring information will be available in the support-ui.


Please check the wiki pages for more information:
* [Message History](https://github.com/OpusCapita/peppol-monitor/wiki/Message-History)
* [Message Statuses](https://github.com/OpusCapita/peppol-monitor/wiki/Message-Statuses)
* [Monitoring Events](https://github.com/OpusCapita/peppol-monitor/wiki/Monitoring-Events)
