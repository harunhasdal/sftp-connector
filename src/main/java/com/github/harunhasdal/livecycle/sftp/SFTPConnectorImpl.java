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

import java.util.Map;

public class SFTPConnectorImpl implements SFTPConnector {
	final AdobeLogger logger = AdobeLogger.getAdobeLogger(SFTPConnectorImpl.class.getName());
	private JSch jsch = new JSch();

	public void writeDocument(Document input, String target,Map<String, String> connectionParams) throws SFTPParameterException, SFTPTransferException {
		validateParameters(input, target, connectionParams);

		Session session = null;
		Channel channel = null;
		ChannelSftp sftpChannel = null;

		try {
			session = jsch.getSession(connectionParams.get(PARAMETERS.USERNAME), connectionParams.get(PARAMETERS.SFTP_HOST), Integer.valueOf(connectionParams.get(PARAMETERS.SFTP_PORT)));
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(connectionParams.get(PARAMETERS.PASSWORD));
			session.connect();

			channel = session.openChannel("sftp");
			channel.connect();

			sftpChannel = (ChannelSftp) channel;
			sftpChannel.cd(target);
			sftpChannel.put(input.getInputStream(), (String)input.getAttribute("wsfilename"));

		} catch (JSchException e) {
			throw new SFTPTransferException(e);
		} catch (SftpException e) {
			throw new SFTPTransferException(e);
		} finally{
			if(sftpChannel != null){
				sftpChannel.exit();
				logger.fine("SFTP Channel exited.");
				channel.disconnect();
				logger.fine("SFTP Channel disconnected.");
			}
			if(session != null){
				session.disconnect();
				logger.fine("SFTP Session disconnected.");
			}
		}
	}

	private void validateParameters(Document input, String target, Map<String, String> connectionParams) {
		if(input == null){
			throw new SFTPParameterException("Input is required");
		}
		if(target == null){
			throw new SFTPParameterException("Target path is required");
		}
		if(connectionParams == null){
			throw new SFTPParameterException("Connection parameter map is required");
		}
		if(!connectionParams.containsKey(PARAMETERS.SFTP_HOST)){
			throw new SFTPParameterException(PARAMETERS.SFTP_HOST + " parameter is required.");
		}
		if(!connectionParams.containsKey(PARAMETERS.SFTP_PORT)){
			throw new SFTPParameterException(PARAMETERS.SFTP_PORT + " parameter is required.");
		}
		if(!connectionParams.containsKey(PARAMETERS.USERNAME)){
			throw new SFTPParameterException(PARAMETERS.USERNAME + " parameter is required.");
		}
		if(!connectionParams.containsKey(PARAMETERS.PASSWORD)){
			throw new SFTPParameterException(PARAMETERS.PASSWORD + " parameter is required.");
		}
		try{
			Integer.parseInt(connectionParams.get(PARAMETERS.SFTP_PORT));
		} catch (NumberFormatException e){
			throw new SFTPParameterException(PARAMETERS.SFTP_PORT + " should be a valid port number", e);
		}
	}

	public void setJsch(JSch jSecureChannel){
		this.jsch = jSecureChannel;
	}
}
