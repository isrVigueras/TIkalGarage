package com.tikal.tallerWeb.server;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@SuppressWarnings("serial")
public class Upload extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
		BlobKey blobKey = blobs.get("file");
		if (blobKey == null) {
			res.sendRedirect("/");
		} else {
			String strStatus = "";
			try {
				res.getWriter().println("Archivo Subido");
			} catch (Exception ex) {
				strStatus = "Could not update Twitter Status : " + ex.getMessage();
				
			} finally {
//				res.sendRedirect("/submitpic.jsp?blob-key=" + blobKey.getKeyString() + "&status=" + strStatus);
			}
		}
	}
}
