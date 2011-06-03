package edu.cwru.apo;

public static class API{

	public JSONObject login(String user, String pass)
	{
		//add code for Login here
		HttpClient httpClient = new TrustAPOHttpClient(getApplicationContext());
		Map<String, String> kvPairs = new HashMap<String, String>();
		kvPairs.put("method","login");
		kvPairs.put("user",user);
		kvPairs.put("pass", md5(pass));
		kvPairs.put("submitLogin", "1");
		try{
			HttpResponse httpResponse = doPost(httpClient, "https://apo.case.edu/api/api.php", kvPairs);
			HttpEntity httpEntity = httpResponse.getEntity();
			String result = EntityUtils.toString(httpEntity);
			JSONObject jObject = new JSONObject(result);
			return jObject;
		} catch(ClientProtocolException e) {
			Log.e("ClientProtocolException", e.gotMessage());
			e.printStackTrace();
		} catch(IOException e) {
			Log.e("IOException", e.gotMessage());
			e.printStackTrace();
		} catch(JSONException e) {
			Log.e("JSONException", e.gotMessage());
			e.printStackTrace();
		}
		JSONObject jObject = null;
		return jObject;
	}

	public JSONObject getContract()
	{
		//add code for getting the Contract here
	}
}
