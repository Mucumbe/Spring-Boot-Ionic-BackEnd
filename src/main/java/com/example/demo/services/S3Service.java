package com.example.demo.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.demo.services.exception.FileException;

@Service
public class S3Service {

    private Logger LOG = LoggerFactory.getLogger(S3Service.class);

    @Autowired
    private AmazonS3 s3cliente;

    @Value("${s3.bucket}")
    private String bucketName;

    public URI uploadFile(MultipartFile multiparFile) {
	try {
	    String fileName = multiparFile.getOriginalFilename();
	    InputStream is;
	    is = multiparFile.getInputStream();
	    String contentType = multiparFile.getContentType();
	    return uploadFile(is, fileName, contentType);
	} catch (IOException e) {
	    throw new FileException("Erro de IO"+ e.getMessage());
	}

    }

    public URI uploadFile(InputStream is, String fileName, String contentType) {
	try {
	    ObjectMetadata meta = new ObjectMetadata();
	    meta.setContentType(contentType);
	    LOG.info("Iniciando upLoad");
	    s3cliente.putObject(bucketName, fileName, is, meta);
	    LOG.info("UpLoad Finalizado");

	    return s3cliente.getUrl(bucketName, fileName).toURI();
	} catch (URISyntaxException e) {

	    throw new FileException("Erro ao converter URl para URI");
	}

    }

}
