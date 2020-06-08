import java.net.*;
import java.io.*;
import java.util.*;
import javafx.util.Pair;

public class SPNServer {
	private final String baseURL = "https://diffcryptanal.herokuapp.com";
	//private final String baseURL = "http://127.0.0.1:8081";

	public SPNServer(){};

	public ArrayList<String> encrypt(ArrayList<String> plaintext_array, int team_num){
		// Convert array of plaintext inputs to single JSON array string
		String plaintext = toJSONArray(plaintext_array);

		// Create key-value pairs for JSON
		ArrayList<Pair<String, String>> kvpair= new ArrayList<Pair<String, String>>();
		kvpair.add(new Pair<String, String>("plaintext", plaintext));
		kvpair.add(new Pair<String, String>("team_num",Integer.toString(team_num)));

		String request = toJSON(kvpair);

		// Send the request
		try {
			String rawResp = httpRequest("/encrypt", request);
			ArrayList<String> resp = parseResponse(rawResp);
			if(resp == null){
				throw new Exception("Attempted to parse malformed response JSON. Double-check your request code.");
			}
			else return resp;
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	/* 
	 * For encrypting 1 plaintext
	 * Clearly, it just makes an array of 1 element
	 * since the server expects an array.
	 * WARNING: Encrypting one-by-one is slow. 
	 */
	public String encrypt(String plaintext, int team_num){
		ArrayList<String> txt = new ArrayList<String>();
		txt.add(plaintext);
		return encrypt(txt, team_num).get(0);
	}

	// Generic httpRequest method
	private String httpRequest(String route, String requestBody) throws Exception {
		//System.out.println("Sending POST request to " + route);
		URL url = new URL(baseURL + route);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setConnectTimeout(5000);
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8"); 
		con.setRequestProperty("Accept", "application/json");

		con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(requestBody.getBytes("UTF-8"));
        os.close();

		int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'POST' request to URL : " + url);
		//System.out.println("Post parameters : " + requestBody);
		//System.out.println("Response Code : " + responseCode);

		BufferedReader in;
		boolean error = false;
		if (responseCode != 200){
			error = true;
			in = new BufferedReader( new InputStreamReader(con.getErrorStream()) );
		}
		else {
			in = new BufferedReader( new InputStreamReader(con.getInputStream()) );
		}
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		
		in.close();

		if (error) {
			System.out.println("\n" + (char)27 + "[31m" + response.toString() + "\n" + (char)27 + "[0m");
			throw new Exception("Request error");
		}
		return(response.toString());
	}

	// Example JSON:
	// {"plaintext":0000000000000000,"team_num":1}
	// {"plaintext":[0000000000000000, 0000000000000001],"team_id":1}

	private String toJSON(ArrayList<Pair<String, String>> data) {
		String json = "{";
		for(int i = 0; i < data.size()-1; i++){
			json += "\"" + data.get(i).getKey() + "\":" + data.get(i).getValue() + ",";
		}
		json += "\"" + data.get(data.size()-1).getKey() + "\":" +data.get(data.size()-1).getValue() + "}";
		return json;
	}

	private String toJSONArray(ArrayList<String> data) {
		String json = "[";
		for(int i = 0; i < data.size()-1; i++){
			json += "\"" + data.get(i) + "\"" + ",";
		}
		json += "\"" + data.get(data.size()-1) + "\"" + "]";
		return json;
	}

	// Response comes in a JSON array (i.e. a string)
	// For example "[0000111100001111, 1100110011001100]"
	// This function parses it into an arraylist
	private ArrayList<String> parseResponse(String str){
		if (!str.substring(0,1).equals("[") || !str.substring(str.length()-1,str.length()).equals("]")){
			System.out.println("\nERROR: Malformed JSON in method parsedResponse\n");
			return null;
		}
		else{
			str = str.substring(1,str.length()-1);
			ArrayList<String> parsed = new ArrayList<String>(Arrays.asList(str.split(",")));
			for(int i = 0; i < parsed.size(); i++){
				parsed.set(i, parsed.get(i).substring(1,parsed.get(i).length()-1));
			}
			//System.out.println(Arrays.toString(parsed.toArray()));
			return parsed;
		}
	}
}