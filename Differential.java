/* Nom, Matricule
 * Nom, Matricule
 */

import java.util.*;

public class Differential{
	//Écrire votre numéro d'équipe içi !!!
	public static int teamNumber = 14;

	public static SPNServer server = new SPNServer();

	//ex :     "0000101100000000"
	//assH19 : "0000000000001110"
	//assH20 : "0000000010110000" or "0000101100000000"
	public static String plain_diff = "0000101100000000";

	//ex :     "0000011000000110"
	//assH19 : "1001000000001001"
	//assH20 : "0000100110010000" or "0000011001100000"
	public static String char_diff = "0000011001100000";

	public static String[] sub_box_exemple = new String[]{"1110", "0100", "1101", "0001", "0010", "1111", "1011", "1000",
			   											  "0011", "1010", "0110", "1100", "0101", "1001", "0000", "0111"};

	public static String[] sub_box_inv_exemple = new String[]{"1110", "0011", "0100", "1000", "0001", "1100", "1010", "1111", 
				   									  		  "0111", "1101", "1001", "0110", "1011", "0010", "0000", "0101"};

	/* H19 EXAMPLE
	public static String[] sub_box = new String[]{"0010", "1011", "1001", "0011", "0111", "1110", "1101", "0101", 
												  "1100", "0110", "0000", "1111", "1000", "0001", "0100", "1010"};

	public static String[] sub_box_inv = new String[]{"1010", "1101", "0000", "0011", "1110", "0111", "1001", "0100", 
												      "1100", "0010", "1111", "0001", "1000", "0110", "0101", "1011"};*/

	// H20 = > [1011, 0101, 0100, 1100, 0110, 0011, 1001, 1010, 1101, 1111, 0001, 0000, 1110, 1000, 0111, 0010]
	public static String[] sub_box = new String[]{"1011", "0101", "0100", "1100", "0110", "0011", "1001", "1010",
												  "1101", "1111", "0001", "0000", "1110", "1000", "0111", "0010"};

	public static String[] sub_box_inv = new String[]{"1011", "1010", "1111", "0101", "0010", "0001", "0100", "1110", 
				   									  "1101", "0110", "0111", "0000", "0011", "1000", "1100", "1001"};											  

	public static int[] perm = new int[]{0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15};

	public static int[] pc1 = new int[]{15, 10, 5, 0, 16, 9, 7, 1, 17, 3, 19, 8, 6, 4, 18, 12, 14, 11, 13, 2};

	public static int[] pc1_inv = new int[]{3, 7, 19, 9, 13, 2, 12, 6, 11, 5, 1, 17, 15, 18, 16, 0, 4, 8, 14, 10};

	public static int[] pc2 = new int[]{9, 7, 0, 8, 5, 1, 4, 2, 16, 12, 19, 10, 17, 15, 13, 14};

	public static int[] pc2_inv = new int[]{2, 5, 7, 6, 4, 1, 3, 0, 11, 9, 14, 15, 13, 8, 12, 10};

	public static int[][] produceDiffTable(String[] sub){
		int[][] result = new int[16][16]; 

		for(int i = 0; i < 16; i++){
			int[] line = new int[16];

			for(int j = 0; j < 16; j++){
				line[Integer.parseInt(sub[j], 2) ^ Integer.parseInt(sub[j^i], 2)]++;
			}
			result[i] = line;
		}

		return result;
	}

	/* Function used to derive inverse of certain permutations
	public static int[] computeInv(int[] arr){
		int[] result = new int[arr.length];

		for(int i = 0; i < arr.length; i++){
			for(int j = 0; j < arr.length; j++){
				if(i == arr[j]){
					result[i] = j;
				}
			}
		}

		return result;
	}*/

	public static String getRandomPlaintext(){
		String text = Integer.toBinaryString((int) Math.floor(Math.random()* 65536));

		while(text.length() != 16){
			text = "0" + text;
		}

		return text;
	}

	public static String permute(String input, int[] perm){
		String output = "";

		for(int i = 0; i < perm.length; i++){
			output += input.charAt(perm[i]);
		}

		return output;
	}

	public static String sub(String input, String[] sub_box){
		int value = 0;

		for(int i = 0; i < input.length(); i++){
			value <<= 1;

			if(input.charAt(i) == '1'){
				value += 1;
			}
		}

		return sub_box[value];
	}

	public static String left_shift(String input, int amount){
		return input.substring(amount) + input.substring(0, amount);
	}

	public static String right_shift(String input, int amount){
		return input.substring(input.length() - amount) + input.substring(0, input.length() - amount);
	}

	public static String[] gen_keys(String master, int n){
		String[] result = new String[n];

		String pc1_res = permute(master, pc1);

		String left = pc1_res.substring(0,10);
		String right = pc1_res.substring(10);

		for(int i = 0; i < n; i++){
			int shift = i % 2 + 1;

			left = left_shift(left, shift);
			right = left_shift(right, shift);

			String temp = left + right;

			result[i] = permute(temp, pc2);
		}

		return result;
	}

	public static String xor(String a, String b){
		if(a.length() != b.length()){
			return null;
		}
		String result = "";

		for(int i = 0; i < a.length(); i++){
			result += a.charAt(i) ^ b.charAt(i);
		}

		return result;
	}

	public static String encrypt(String plaintext, String[] subkeys){
		String cipher = plaintext;

		for(int i = 0; i < 4; i++){
			//sub-key mixing
			cipher = xor(cipher, subkeys[i]);

			//substitution
			String temp = "";

			for(int j = 0; j < 4; j++){
				temp += sub(cipher.substring(j*4, j*4+4), sub_box);
			}

			cipher = temp;

			//permutation
			cipher = permute(cipher, perm);
		}

		//Final sub-key mixing (5th key)
		cipher = xor(cipher, subkeys[4]);

		return cipher;
	}

	/*
	public static String decrypt(String cipher, String[] subkeys, int num){
		String plaintext = cipher;

		//Final sub-key mixing (5th key)
		plaintext = xor(plaintext, subkeys[num - 1]);

		for(int i = num - 2; i >= 0; i--){
			//permutation
			plaintext = permute(plaintext, perm_inv);

			//substitution
			String temp = "";

			for(int j = 0; j < 4; j++){
				temp += sub(plaintext.substring(j*4, j*4+4), sub_box_inv);
			}

			plaintext = temp;

			//sub-key mixing
			plaintext = xor(plaintext, subkeys[i]);
		}

		return plaintext;
	}*/

	public static String getPartialSubkey(){
		int[] counts = new int[256];

		ArrayList<String> plaintexts = new ArrayList<>();

		for(int i = 0; i < 1000; i++){
			//creating a plaintext pair satisfying plain_diff
			String a = getRandomPlaintext();

			String b = xor(a, plain_diff);

			plaintexts.add(a);
			plaintexts.add(b);
		}

		ArrayList<String> ciphers = server.encrypt(plaintexts,teamNumber);

		for(int j = 0; j < 256; j++){
			String temp_key = Integer.toBinaryString(j);

			while(temp_key.length() != 8){
				temp_key = "0" + temp_key;
			}

			String mask = "0000" + temp_key.substring(0,4) + temp_key.substring(4) + "0000";

			mask = permute(mask, perm);

			for(int i = 0; i < ciphers.size(); i += 2){
				String res_a = ciphers.get(i);
				String res_b = ciphers.get(i+1);

				String res_a_round4 = xor(res_a, mask);
				String res_b_round4 = xor(res_b, mask);

				res_a_round4 = permute(res_a_round4, perm);
				res_b_round4 = permute(res_b_round4, perm);

				String temp = "";
				for(int k = 0; k < 4; k++){
					temp += sub(res_a_round4.substring(k*4, k*4+4), sub_box_inv);
				}
				res_a_round4 = temp;

				temp = "";
				for(int k = 0; k < 4; k++){
					temp += sub(res_b_round4.substring(k*4, k*4+4), sub_box_inv);
				}
				res_b_round4 = temp;

				if(xor(res_a_round4,res_b_round4).equals(char_diff)){
					counts[j] ++;
				}
			}
		}

		double max = 0;
		int index = 0;
		for(int j = 0; j < counts.length; j++){
			if(((double) counts[j])/1000 > max){
				max = ((double) counts[j])/1000;

				index = j;
			}
		}

		String result = Integer.toBinaryString(index);

		while(result.length() != 8){
			result = "0" + result;
		}

		return permute("XXXX" + result.substring(0,4) + result.substring(4) + "XXXX", perm);
	}

	public static String getPartialMasterkey(String partialSubkey, int n){
		String result = permute(partialSubkey, pc2_inv);

		result = result.substring(0,3) + "X" + result.substring(3);
		result = result.substring(0,6) + "X" + result.substring(6);
		result = result.substring(0,11) + "X" + result.substring(11);
		result = result.substring(0,18) + "X" + result.substring(18);

		String left = result.substring(0,10);
		String right = result.substring(10);

		for(int i = 0; i < n; i++){
			int shift = i % 2 + 1;

			left = right_shift(left, shift);
			right = right_shift(right, shift);
		}

		String temp = left + right;

		result = permute(temp, pc1_inv);

		return result;
	}

	public static String bruteForce(String partialMasterkey){
		String result = "";
		boolean found = false;

		//Generating random plaintext
		String text = getRandomPlaintext();

		String res_server = server.encrypt(text,teamNumber);

		for(int i = 0; i < 4096 && !found; i++){
			String candidate = partialMasterkey;

			String missingBits = Integer.toBinaryString(i);

			while(missingBits.length() != 12){
				missingBits = "0" + missingBits;
			}

			int counter = 0;
			for(int j = 0; j < candidate.length(); j++){
				if(candidate.charAt(j) == 'X'){
					candidate = candidate.substring(0,j) + missingBits.charAt(counter)
					 + candidate.substring(j+1);
					counter ++;
				}
			}

			String[] subkeys = gen_keys(candidate, 5);

			String res_local = encrypt(text, subkeys);

			if(res_server.equals(res_local)){
				boolean test_failed = false;

				for(int j = 0; j < 10 && !test_failed; j++){
					//Generating random plaintext
					String text2 = getRandomPlaintext();

					String res_server2 = server.encrypt(text2,teamNumber);
					String res_local2 = encrypt(text2, subkeys);

					if(!res_server2.equals(res_local2)){
						test_failed = true;
					}
				}

				if(!test_failed){
					found = true;
					result = candidate;
				}
			}

		}

		return result;
	}

	public static String getMasterkey(int teamNumber){
		String text = Integer.toBinaryString((int) (5*Math.pow(teamNumber,3) - 342*Math.pow(teamNumber,2)+263143));
		if(text.length() > 20){
			return "Input too big";
		}
		while(text.length() != 20){
			text = "0" + text;
		}

		return(text);
	}

	public static void main(String args[]){
		//Génération de la table des fréquences des différentielles de sortie
		//pour chaque différentielle d'entrée
		System.out.println(Arrays.deepToString(produceDiffTable(sub_box)));

		//Calcul de la sous-clef partielle k_5^*
		String partialSubkey = getPartialSubkey();
		System.out.println("Sous-clef partielle k_5^* : " + partialSubkey);

		//Calcul de la clef maître partielle k^* 
		String partialMasterkey = getPartialMasterkey(partialSubkey, 5);
		System.out.println("Clef maître partielle k^* : " + partialMasterkey);

		//Calcul de la clef maître par fouille exhaustive 
		String masterkey = bruteForce(partialMasterkey);
		System.out.println("Clef maître k :             " + masterkey);

		//Clef de l'exemple de la démo 3 : 00100100001111010101
		System.out.println("Vérification :              " + getMasterkey(teamNumber));

		//Generation of a new example
		/*
		boolean found = false;

		String[] result = new String[16];

		while(!found){
			ArrayList<String> candidate = new ArrayList<String>(Arrays.asList(sub_box));

			//candidate = Arrays.asList(sub_box);

			Collections.shuffle(candidate);

			String[] candidate_arr = new String[16];

			for(int i = 0; i < candidate.size(); i++){
				candidate_arr[i] = candidate.get(i);
			}

			int[][] temp = produceDiffTable(candidate_arr);

			int eight_count = 0;
			//int six_count_out1 = 0;
			int six_count_out2 = 0;
			int column = 0; 
			for(int i = 0; i < temp.length; i++){
				for(int j = 0; j < temp[0].length; j++){
					if(temp[i][j] == 8){
						eight_count++;
						column = j;
					}
					if(temp[i][j] == 6 && ((i == 2 && j == 9) || (i == 4 && j == 6)) ){
						//if(j == 1 || j == 2 || j == 4 || j == 8){
						//	six_count_out1 ++;
						//}
						six_count_out2 ++;
					}
				}
			}

			if(eight_count == 1 && six_count_out2 == 2 && (column == 4)){
				found = true;
				result = candidate_arr;
			}

			for(int i = 0; i < candidate_arr.length; i++){
				//System.out.println(i + " : " + Integer.parseInt(candidate_arr[i],2));
				if(Integer.parseInt(candidate_arr[i],2) == i){
					found = false;
				}
			}
		}

		System.out.println(Arrays.toString(result));
		System.out.println(Arrays.deepToString(produceDiffTable(result)));*/

	}

}