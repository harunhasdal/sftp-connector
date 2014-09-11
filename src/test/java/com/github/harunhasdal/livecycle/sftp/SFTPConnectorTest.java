package com.github.harunhasdal.livecycle.sftp;

import static org.junit.Assert.*;

import com.adobe.idp.Document;
import org.junit.Test;
import org.junit.Before;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import com.jcraft.jsch.*;
import org.mockito.Matchers;

import static org.mockito.Mockito.*;

public class SFTPConnectorTest {

	SFTPConnectorImpl connector = new SFTPConnectorImpl();

	String testTarget = "/var/www/test";
	Document testDocument = new Document(new byte[]{'1'});
	Map<String,String> testParams = new HashMap<String, String>();

	@Before
	public void setTestParameters() {
		testParams.put(SFTPConnector.PARAMETERS.SFTP_HOST, "testhost");
		testParams.put(SFTPConnector.PARAMETERS.SFTP_PORT, "22");
		testParams.put(SFTPConnector.PARAMETERS.USERNAME, "user1");
		testParams.put(SFTPConnector.PARAMETERS.PASSWORD, "pass1");
	}

	@Test
	public void nullArguments() throws Exception {
		try{
			connector.writeDocument(null, testTarget, testParams);
			fail("Should throw exception");
		} catch (SFTPParameterException e){
			assertNotNull("Should throw SFTPTransferException with a message", e.getMessage());
		} catch (Exception e){
			fail("Shouldn't throw another type of exception.");
		}
		try{
			connector.writeDocument(testDocument, null, testParams);
			fail("Should throw exception");
		} catch (SFTPParameterException e){
			assertNotNull("Should throw SFTPTransferException with a message", e.getMessage());
		} catch (Exception e){
			fail("Shouldn't throw another type of exception.");
		}
		try{
			connector.writeDocument(testDocument, testTarget, null);
			fail("Should throw exception");
		} catch (SFTPParameterException e){
			assertNotNull("Should throw SFTPTransferException with a message", e.getMessage());
		} catch (Exception e){
			fail("Shouldn't throw another type of exception.");
		}
	}

	@Test
	public void noHostParameter() throws Exception {
		testParams.remove(SFTPConnector.PARAMETERS.SFTP_HOST);
		try{
			connector.writeDocument(testDocument, testTarget, testParams);
			fail("Should throw exception");
		} catch (SFTPParameterException e){
			assertNotNull("Should throw SFTPTransferException with a message", e.getMessage());
		} catch (Exception e){
			fail("Shouldn't throw another type of exception.");
		}
	}

	@Test
	public void noUsernameParameter() throws Exception {
		testParams.remove(SFTPConnector.PARAMETERS.USERNAME);
		try{
			connector.writeDocument(testDocument, testTarget, testParams);
			fail("Should throw exception");
		} catch (SFTPParameterException e){
			assertNotNull("Should throw SFTPTransferException with a message", e.getMessage());
		} catch (Exception e){
			fail("Shouldn't throw another type of exception.");
		}
	}

	@Test
	public void noPasswordParameter() throws Exception {
		testParams.remove(SFTPConnector.PARAMETERS.PASSWORD);
		try{
			connector.writeDocument(testDocument, testTarget, testParams);
			fail("Should throw exception");
		} catch (SFTPParameterException e){
			assertNotNull("Should throw SFTPTransferException with a message", e.getMessage());
		} catch (Exception e){
			fail("Shouldn't throw another type of exception.");
		}
	}

	@Test
	public void invalidPortParameter() throws Exception {
		testParams.put(SFTPConnector.PARAMETERS.SFTP_PORT, "asd");
		try{
			connector.writeDocument(testDocument, testTarget, testParams);
			fail("Should throw exception");
		} catch (SFTPParameterException e){
			assertNotNull("Should throw SFTPTransferException with a message", e.getMessage());
		} catch (Exception e){
			e.printStackTrace();
			fail("Shouldn't throw another type of exception.");
		}
	}

	@Test
	public void usesJschSession() throws Exception {
		JSch jschMock = mock(JSch.class);
		Session sessionMock = mock(Session.class);
		ChannelSftp channelMock = mock(ChannelSftp.class);

		when(jschMock.getSession("user1", "testhost", 22)).thenReturn(sessionMock);
		when(sessionMock.openChannel("sftp")).thenReturn(channelMock);

		connector.setJsch(jschMock);
		connector.writeDocument(testDocument, testTarget, testParams);

		verify(sessionMock).setConfig("StrictHostKeyChecking", "no");
		verify(sessionMock).setPassword(testParams.get(SFTPConnector.PARAMETERS.PASSWORD));
		verify(sessionMock).connect();
		verify(sessionMock).openChannel("sftp");
		verify(channelMock).connect();
		verify(channelMock).cd(anyString());
		verify(channelMock).put(Matchers.<InputStream>any(),anyString());
		verify(channelMock).disconnect();
		verify(sessionMock).disconnect();
	}

}
