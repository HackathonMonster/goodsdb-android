package com.zeroone_creative.goodsdb.model.system;

public class AppConfig {

	private AppConfig(){
		//restrict instantiation
	}
	//デバッグ常態化どうかのフラグ
	public static final boolean DEBUG = true;
	public static final boolean REAL_DEVICE = true;

    public static final String TAG_MESSSAGE_DIALOG = "message_dialog";

    public static final String TAG_ADD_TAG_DIALOG = "add_tag_dialog";
}
