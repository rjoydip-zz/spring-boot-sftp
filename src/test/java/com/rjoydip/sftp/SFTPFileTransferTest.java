package com.rjoydip.sftp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SFTPFileTransferTest {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${sftp.PORT}")
    private int PORT;
    @Value("${sftp.HOST}")
    private String HOST;
    @Value("${sftp.REMOTE.USERNAME}")
    private String REMOTE_USERNAME;
    @Value("${sftp.REMOTE.PASSWORD}")
    private String REMOTE_PASSWORD;

    @Test
    public void testVariables() {
        logger.info("PORT {}", PORT);
        logger.info("HOST {}", HOST);
        logger.info("REMOTE.USERNAME {}", REMOTE_USERNAME);
        logger.info("REMOTE.PASSWORD {}", REMOTE_PASSWORD);
    }

    @Test
    public void testPutAndGetFile() throws JSchException, SftpException, IOException {
        JSch jsch = new JSch();

        Hashtable<String, String> config = new Hashtable<String, String>();
        config.put("StrictHostKeyChecking", "no");
        JSch.setConfig(config);

        Session session = jsch.getSession(REMOTE_USERNAME, HOST, PORT);
        session.setPassword(REMOTE_PASSWORD);

        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();

        ChannelSftp sftpChannel = (ChannelSftp) channel;

        final String testFileContents = "some file contents";

        String uploadedFileName = "uploadFile";
        sftpChannel.put(new ByteArrayInputStream(testFileContents.getBytes()), uploadedFileName);

        String downloadedFileName = "downLoadFile";
        sftpChannel.get(uploadedFileName, downloadedFileName);

        File downloadedFile = new File(downloadedFileName);
        logger.info("File getAbsolutePath: {}", downloadedFile.getAbsolutePath());
        assertTrue(downloadedFile.exists());

        String fileData = getFileContents(downloadedFile);
        logger.info("File data: {}", fileData);

        assertThat(testFileContents).isEqualTo(fileData);

        if (sftpChannel.isConnected()) {
            sftpChannel.exit();
            logger.info("Disconnected channel");
        }

        if (session.isConnected()) {
            session.disconnect();
            logger.info("Disconnected session");
        }

    }

    private String getFileContents(File downloadedFile) throws FileNotFoundException, IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(new FileReader(downloadedFile));

        try {
            char[] buf = new char[1024];
            for (int numRead = 0; (numRead = reader.read(buf)) != -1; buf = new char[1024]) {
                fileData.append(String.valueOf(buf, 0, numRead));
            }
        } finally {
            reader.close();
        }

        return fileData.toString();
    }
}