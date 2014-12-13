package com.zeroone_creative.goodsdb.controller.provider;import com.android.volley.Response.ErrorListener;import com.android.volley.Response.Listener;import com.android.volley.toolbox.JsonArrayRequest;import org.json.JSONArray;public class CustomJSONArrayRequest extends JsonArrayRequest {		private Priority mPriority = Priority.LOW;	/**	 * コンストラクタ	 * @param url	 * @param listener	 * @param errorListener	 */	public CustomJSONArrayRequest(String url, Listener<JSONArray> listener,ErrorListener errorListener) {		super(url,listener, errorListener);	}	/**	 * 優先順位を設定する	 * @param priority 優先順位設定	 */	public void setPriority(final Priority priority) {		this.mPriority = priority;	}	/**	 * 現在の優先順位を返却する	 * @return 優先順位	 */	public Priority getPriority() {		return mPriority;	}}