class rijnMaker {

    private int stateSize;
    private int keySize;
    int[] keyArray;
    startRijndael r;

    //constructor, sets up state and key size
    public rijnMaker(int sSize, int kSize) {
        stateSize = sSize;
        keySize = kSize;
        keyArray = new int[keySize];
    }

    //converts an array of ints to a string of chars
    public String intArrayToString(int[] t) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < t.length; i++) {
            sb.append((char) t[i]);
        }
        return sb.toString();
    }

    //converts a string of chars to an array of ints
    public int[] stringToIntArray(String s) {
        int[] temp = new int[s.length()];
        for (int i = 0; i < s.length(); i++) {
            temp[i] = s.charAt(i);
        }
        return temp;
    }

    //converts a string of hex values (2 digit) to an int array
    public int[] hexStringToIntArray(String s) {
        int[] temp = new int[s.length() / 2];
        for (int i = 0; i < s.length(); i = i + 2) {
            temp[i / 2] = Integer.valueOf(s.substring(i, i + 2), 16).intValue();
        }
        return temp;
    }

    //takes in 2 strings of any size representing the text and the key
    //returns a string representing the ciphertext in hex values (2 digits)
    public String encrypt(String t, String k) {
        String text = cleanText(t);
        String cipher = "";
        int[] temp = new int[stateSize];
        setKeyLength(k);

        //encrypt each chunk of text size sSize and store the result
        for (int i = 0; i < text.length(); i = i + stateSize) {
            temp = stringToIntArray(text.substring(i, i + stateSize));
            r = new startRijndael(temp, keyArray);
            r.encrypt();
            cipher += r.stateToHex();
        }
        return cipher;
    }

    //takes in 2 strings, one representing the ciphertext as a string of hex values
    //the other, the key as a normal string.
    //returns a string containing the plain text. works with any size of key and text
    public String decrypt(String t, String k) {
        String text = t;
        String plain = "";
        setKeyLength(k);
        int[] temp;// new int[stateSize];

        //decrypt each chunk of text size sSize and store the result
        for (int i = 0; i < text.length(); i = i + stateSize * 2) {
            temp = hexStringToIntArray(text.substring(i, i + stateSize * 2));
            r = new startRijndael(temp, keyArray);
            r.decrypt();
            plain += (intArrayToString(r.toInt()));
        }
        return plain;
    }

    //sets the keyArray to the correct size and fills it with the correct ints
    public void setKeyLength(String k) {
        String key = k;
        if (key.length() > keySize) {
            key = key.substring(0, keySize);
        }

        while (key.length() < keySize) {
            key = key + " ";
        }

        for (int i = 0; i < key.length(); i++) {
            keyArray[i] = key.charAt(i);
        }
    }

    //sets the text to the correct length by adding on spaces if required
    public String cleanText(String t) {
        String text = t;

        while (text.length() < stateSize) {
            text = text + " ";
        }

        while (text.length() % stateSize != 0) {
            text = text + " ";
        }

        return text;
    }
}