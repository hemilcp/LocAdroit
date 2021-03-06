package com.example.MultiUserPack;

import android.graphics.Bitmap;

public class SpinnerEntry {
	/** The unique id of this contact as defined in the raw contact table **/
	private final int contactId;

	/** The picture of this contact - if available **/
	private final Bitmap contactPhoto;

	/** The name which should be displayed for this contact **/
	private final String contactName;

	public SpinnerEntry(int contactID, Bitmap contactPhoto,
			String contactName) {
		this.contactId = contactID;
		this.contactPhoto = contactPhoto;
		this.contactName = contactName;
	}

	public int getContactId() {
		return contactId;
	}

	public Bitmap getContactPhoto() {
		return contactPhoto;
	}

	public String getContactName() {
		return contactName;
	}
}
