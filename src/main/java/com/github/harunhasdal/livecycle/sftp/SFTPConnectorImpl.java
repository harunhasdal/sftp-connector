/*
The MIT License (MIT)

Copyright (c) 2014 Harun Hasdal

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.harunhasdal.livecycle.sftp;

import com.adobe.idp.Document;
import com.adobe.logging.AdobeLogger;
import com.jcraft.jsch.*;

public class SFTPConnectorImpl implements SFTPConnector {
	final AdobeLogger logger = AdobeLogger.getAdobeLogger(SFTPConnectorImpl.class.getName());

	String SFTPHOST = "localhost";
	int SFTPPORT = 22;
	String SFTPUSER = "username";
	String SFTPPASS = "password";

	public void writeDocument(Document input, String target) {
		JSch jsch = new JSch();
		Session session = null;
		Channel channel = null;
		ChannelSftp sftpChannel = null;
		try {
			session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(SFTPPASS);
			session.connect();

			channel = session.openChannel("sftp");
			channel.connect();

			sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(target);
			sftpChannel.put(input.getInputStream(), (String)input.getAttribute("wsfilename"));

			sftpChannel.exit();
			session.disconnect();
		} catch (JSchException e) {
			throw new SFTPTransferException(e);
		} catch (SftpException e) {
			throw new SFTPTransferException(e);
		} finally{
			sftpChannel.exit();
			logger.fine("sftp Channel exited.");
			channel.disconnect();
			logger.fine("Channel disconnected.");
			session.disconnect();
			logger.fine("Host Session disconnected.");
		}
	}
}
