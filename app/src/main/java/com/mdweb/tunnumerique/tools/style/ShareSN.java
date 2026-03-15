package com.mdweb.tunnumerique.tools.style;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ShareSN {

	private Activity act;
	private String textToShare;
	private String subject;
	private Context contextAct;

	Intent shareimage = new Intent(Intent.ACTION_SEND);


	public ShareSN(Activity mAct) {
		act = mAct;
	}

	public void share(String txtToShare, String subject) {

		setSubject(subject);
		setTextToShare(txtToShare);
		startShare();

	}
	
	
	public void shareImage(Uri uri, String txtToShare, String subject){
		shareimage.setType("image/*");
		shareimage.putExtra(Intent.EXTRA_STREAM, uri);
		shareimage.putExtra(Intent.EXTRA_TEXT, getTextToShare());
		shareimage.putExtra(Intent.EXTRA_SUBJECT, getSubject());

		shareimage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

		act.startActivity(Intent.createChooser(shareimage, "Partager"));
	}

	private void startShare() {

		// create the send intent
		Intent shareIntent = new Intent(Intent.ACTION_SEND);

		// set the type
		shareIntent.setType("text/plain");
		try {

			shareIntent.putExtra(Intent.EXTRA_TEXT,getTextToShare());
			shareIntent.putExtra(Intent.EXTRA_SUBJECT,getSubject());
			act.startActivity(Intent.createChooser(shareIntent, "Partager"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String msubject) {
		subject = msubject;
	}

	public String getTextToShare() {
		return textToShare;
	}

	public void setTextToShare(String textToShare) {
		this.textToShare = textToShare;
	}

}