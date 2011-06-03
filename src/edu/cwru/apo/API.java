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
		return null;
	}

	public JSONObject getContract()
	{
		//add code for getting the Contract here
	}

	private String md5(String in)
	{
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(in.getBytes());
			byte[] a = digest.digest();
			int len = a.length;
			StringBuilder sb = new StringBuilder(len << 1);
			for(int i = 0; i<len; i++)
			{
				sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16);
				sb.append(Character.forDigit(a[i] & 0x0f, 16));
			}
			return sb.toString();
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static HttpResponse doPost(HttpClient httpClient, String url, Map<String, String> kvPairs) throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(url);
		
		if(kvPairs != null && kvPairs.isEmpty() == false) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(kvPairs.size());
			String k, v;
			Iterator<String> itKeys = kvPairs.keySet().iterator();
			
			while(itKeys.hasNext()) {
				k = itKeys.next();
				v = kvPairs.get(k);
				nameValuePairs.add(new BasicNameValuePair(k,v));
			}

			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}

		return httpClient.execute(httpPost);
	} 
		
}
