class startRijndael {

    private int[] S;//the S-Box
    private int[] Sinv;//the inverse S-Box
    private final int[] log = new int[256];//these tables are used to allow us
    private final int[] alog = new int[256];//to multiply in the GF(2^8) field
    private final int ROOT = 0x11B;//constant XORed with Byte when 2 bytes being multiplied exceed 8 bits

    private myByte[] key;   //holds the key used for encryption and decryption
    private myByte[] state; //holds the initial plaintext and it's status at every stage of the encryption
                            //ends up holding the ciphertext when encryption is complete
    private myByte[] rcon;  //holds all 30 possible round constants as used in the Key Expansion
    private myWord[] w;     //used to hold the fully expanded key

    private int NB; // block length of state divided by 32 (no. of columns of state)
    private int NK; // block length of key divided by 32 (no. of columns of key)
    private int NR; // no. of rounds to carry out. based on function of NB & NK

    logMaker logger = new logMaker();

    //returns the state converted to characters in a string
    public String stateToString() {
        byte[] b = new byte[state.length];
        for (int i = 0; i < state.length; i++) {
            b[i] = (byte) state[i].getVal();
        }
        return new String(b);
    }

    //returns a string representation of the state in hex values
    //each int is returned as a 2 digit hex number
    public String stateToHex() {
        String temp = "";
        for (int i = 0; i < state.length; i++) {
            if (state[i].getVal() < 16) {
                //0 appended if int value would only give us a 1 digit hex number
                temp = temp + "0";
            }
            temp += Integer.toString(state[i].getVal(), 16);
        }
        return temp;
    }

    public String byteToHex(myByte b) {
        String temp = "";
        if (b.getVal() < 16) {
            //0 appended if int value would only give us a 1 digit hex number
            temp = temp + "0";
        }
        temp += Integer.toString(b.getVal(), 16);
        return temp;
    }

    //converts a string containing 2 digit hex numbers to an array of ints
    public static int[] hexToInt(String s) {
        int[] temp = new int[s.length() / 2];
        for (int i = 0; i < s.length(); i = i + 2) {
            temp[i / 2] = Integer.valueOf(s.substring(i, i + 2), 16).intValue();
        }
        return temp;
    }

    //a constructor that takes in an array of ints for message and key
    //arrays must be either of length 16, 24 or 32
    //and sets up all tables, variables ready for encryption or decryption
    public startRijndael(int[] m, int[] k) {
        NB = m.length / 4;
        NK = k.length / 4;

        //find out the correct number of rounds
        if (Math.max(NB,NK)==4) {
            NR = 10;
        } else if (Math.max(NB,NK)==6) {
            NR = 12;
        } else if (Math.max(NB,NK)==8) {
            NR = 14;
        }

        logger.setNR(NR);
        
        //instantiate the key and state arrays to the correct size
        key = new myByte[4 * NK];
        state = new myByte[4 * NB];

        //fill the state with myByte objects representing the text
        for (int i = 0; i < m.length; i++) {
            state[i] = new myByte(m[i]);
        }

        //fill the key with myByte objects representing the key
        for (int i = 0; i < k.length; i++) {
            key[i] = new myByte(k[i]);
        }

        //create tables needed for calculations
        createSBox();       //used by ByteSub
        createSinvBox();    //used by ByteSub Inverse
        createRcon();       //used by key expansion
        createLogs();       //used for multiplying in GF(2^8)
        keyExpansion();     //used for encrypt and decrypt
    }

    //constructor which takes in a message or ciphertext and key
    //must be exact sizes (16, 24 or 32 characters)
    //and sets up all tables, variables ready for encryption or decryption
    public startRijndael(String message, String theKey) {
        NB = message.length() / 4;
        NK = theKey.length() / 4;

        //find out the correct number of rounds
        if (Math.max(NB,NK)==4) {
            NR = 10;
        } else if (Math.max(NB,NK)==6) {
            NR = 12;
        } else if (Math.max(NB,NK)==8) {
            NR = 14;
        }

        //instantiate the key and state arrays to the correct size
        key = new myByte[4 * NK];
        state = new myByte[4 * NB];

        //create an array of bytes representing the message(ASCII values)
        byte messArray[] = message.getBytes();

        //fill the state with myByte objects representing the text
        for (int i = 0; i < messArray.length; i++) {
            state[i] = new myByte(messArray[i]);
        }

        //create an array of bytes representing the key(ASCII values)
        byte keyArray[] = theKey.getBytes();

        //fill the key with myByte objects representing the key
        for (int i = 0; i < keyArray.length; i++) {
            key[i] = new myByte(keyArray[i]);
        }

        //create tables needed for calculations
        createSBox();       //used by ByteSub
        createSinvBox();    //used by ByteSub Inverse
        createRcon();       //used by key expansion
        createLogs();       //used for multiplying in GF(2^8)
        keyExpansion();     //used for encrypt and decrypt
    }

    public void encrypt() {
        logger.clear();
        logger.setInit(stateToHex());

        AddRoundKey(0);
        logger.addDescr("start: ",0);
        logger.addState(stateToHex());

        doRounds();
        doFinalRound();
    }

    public void decrypt() {
        logger.clear();
        logger.setInit(stateToHex());

        doFinalRoundInv();
        doRoundsInv();
        AddRoundKey(0);

        logger.addDescr("final: ",4*NR-1);
        logger.addState(stateToHex());
        logger.setResult(stateToString());
    }

    private void doRounds() {
        for (int i = 1; i < NR; i++) {
            logger.addDescr("start: ",4*(i-1));

            ByteSub();
            logger.addDescr("b_sub: ",4*(i-1)+1);
            logger.addState(stateToHex());

            ShiftRow();
            logger.addDescr("s_row: ",4*(i-1)+2);
            logger.addState(stateToHex());

            MixColumn();
            logger.addDescr("m_col: ",4*(i-1)+3);
            logger.addState(stateToHex());

            AddRoundKey(i);
            logger.addState(stateToHex());
        }
    }

    private void doRoundsInv() {
        for (int i = NR - 1; i > 0; i--) {
            AddRoundKey(i);
            logger.addDescr("start: ",(4*((NR-i)-1))+3);
            logger.addState(stateToHex());

            MixColumnInv();
            logger.addDescr("m_col: ",((4*((NR-i)-1))+1)+3);
            logger.addState(stateToHex());

            ShiftRowInv();
            logger.addDescr("s_row: ",((4*((NR-i)-1)+2))+3);
            logger.addState(stateToHex());

            ByteSubInv();
            logger.addDescr("b_sub: ",((4*((NR-i)-1)+3))+3);
            logger.addState(stateToHex());
        }
    }

    private void doFinalRound() {
        logger.addDescr("start: ",4*(NR-1));

        logger.addDescr("b_sub: ",4*(NR-1)+1);
        logger.addState(stateToHex());
        ByteSub();

        logger.addDescr("s_row: ",4*(NR-1)+2);
        logger.addState(stateToHex());
        ShiftRow();

        logger.addDescr("final: ",4*(NR-1)+3);
        logger.addState(stateToHex());
        AddRoundKey(NR);
    }

    private void doFinalRoundInv() {
        logger.addDescr("start: ",0);
        logger.addState(stateToHex());
        AddRoundKey(NR);

        logger.addDescr("s_row: ",1);
        logger.addState(stateToHex());
        ShiftRowInv();

        logger.addDescr("b_sub: ",2);
        logger.addState(stateToHex());
        ByteSubInv();
    }

    //performs byte substitution on all bytes of the state
    private void ByteSub() {
        for (int i = 0; i < (NB * 4); i++) {
            state[i] = doSBox(state[i]);
        }
    }

    //performs Shift Row
    private void ShiftRow() {
        //the shift offset is determined by the size of the state
        if (NB == 4 || NB == 6) {
            doShift(1, 1);
            doShift(2, 2);
            doShift(3, 3);
        } else if (NB == 8) {
            doShift(1, 1);
            doShift(2, 3);
            doShift(3, 4);
        }
    }

    private void MixColumn() {
        myByte[] col2 = new myByte[4]; //holds newly multiplied column

        //for each column of the state
        for (int i = 0; i < NB; i++) {
            //In GF(2^8) field XOR takes the place of addition
            //for each column of the state we multiply it by 2 3 1 1, 1 2 3 1, 1 1 2 3, 3 1 1 2 matrix
            col2[0] = mul(state[i * 4].getVal(), 2).XOR(mul(state[i * 4 + 1].getVal(), 3).XOR(state[i * 4 + 2].XOR(state[i * 4 + 3])));
            col2[1] = state[i * 4].XOR(mul(state[i * 4 + 1].getVal(), 2).XOR(mul(state[i * 4 + 2].getVal(), 3).XOR(state[i * 4 + 3])));
            col2[2] = state[i * 4].XOR(state[i * 4 + 1].XOR(mul(state[i * 4 + 2].getVal(), 2).XOR(mul(state[i * 4 + 3].getVal(), 3))));
            col2[3] = mul(state[i * 4].getVal(), 3).XOR(state[i * 4 + 1].XOR(state[i * 4 + 2].XOR(mul(state[i * 4 + 3].getVal(), 2))));

            //place the results back into the state
            state[i * 4] = col2[0];
            state[i * 4 + 1] = col2[1];
            state[i * 4 + 2] = col2[2];
            state[i * 4 + 3] = col2[3];
        }
    }

    private void AddRoundKey(int i) {
        //i tells us what round we're in
        for (int j = 0; j < NB * 4; j++) {
            //for each byte of the state we must XOR it with the correct byte in the expanded key
            //w[(i*NB) + (j/4)].theWord[3-(j%4)] gives us the right word and right byte in that word for each
            //byte of the state depending what round we're in
            state[j] = state[j].XOR(w[(i * NB) + (j / 4)].theWord[3 - (j % 4)]);
            logger.addKey(i, byteToHex(w[(i * NB) + (j / 4)].theWord[3 - (j % 4)]));
        }
    }

    //perfroms inverse byte substitution on all bytes of the state
    private void ByteSubInv() {
        for (int i = 0; i < (NB * 4); i++) {
            state[i] = doSinvBox(state[i]);
        }
    }

    //performs the inverse of Shift row
    private void ShiftRowInv() {
        //the shift offset is determined by the size of the state
        switch (NB) {
            case 4:
                doShift(1, 3);
                doShift(2, 2);
                doShift(3, 1);
                break;
            case 6:
                doShift(1, 5);
                doShift(2, 4);
                doShift(3, 3);
                break;
            case 8:
                doShift(1, 7);
                doShift(2, 5);
                doShift(3, 4);
                break;
        }
    }

    private void MixColumnInv() {
        myByte[] temp = new myByte[4];
        myByte[] temp2 = new myByte[4];

        //for each column of the state
        for (int i = 0; i < NB; i++) {
            //System.out.println("i: "+ i + " NB:" + NB);
            temp[0] = state[i * 4];
            temp[1] = state[i * 4 + 1];
            temp[2] = state[i * 4 + 2];
            temp[3] = state[i * 4 + 3];

            //temp now contains the column in question
            //internal method to mix 1 particular column
            temp2 = MixColumnInvInt(temp);
            state[i * 4] = temp2[0];
            state[i * 4 + 1] = temp2[1];
            state[i * 4 + 2] = temp2[2];
            state[i * 4 + 3] = temp2[3];
            //the state row in question is now updated
        }
    }

    //takes in a myByte object and returns its substitute from the S-Box
    //used my method SubByte()
    private myByte doSBox(myByte m) {
        return new myByte(S[m.getVal()]);
    }

    //takes in a myByte object and returns its substitute from the Sinv-Box
    //used by method SubByteInv()
    private myByte doSinvBox(myByte m) {
        return new myByte(Sinv[m.getVal()]);
    }

    //method used my both ShiftRow and ShiftRowInv which takes in what
    //row of the state to shift and how many places to shift it
    private void doShift(int row, int shift) {
        myByte[] temp = new myByte[NB];
        myByte[] temp2 = new myByte[NB];

        for (int i = 0; i < NB; i++) {
            temp[i] = state[i * 4 + row];
        }
        //temp array now contains row in question from state

        for (int i = 0; i < NB; i++) {
            temp2[i] = temp[(i + shift) % NB];
        }
        //temp2 now contains the row having been shifted

        for (int i = 0; i < NB; i++) {
            state[row + i * 4] = temp2[i];
        }
        //state is updated with it's new values
    }

    private void keyExpansion() {
        myWord temp = new myWord();
        myWord rconWord = new myWord();
        w = new myWord[NB * (NR + 1)]; //will hold the fully expanded key as an array of words

        //this just initialises an array of empty words
        //it must be big enough to hold the full expanded key
        for (int i = 0; i < NB * (NR + 1); i++) {
            w[i] = new myWord();
        }

        //this adds the cipher key to the beginning of the array of words
        for (int i = 0; i < NK; i++) {
            w[i].addWord(key[4 * i], key[4 * i + 1], key[4 * i + 2], key[4 * i + 3]);
        }

        //main key expansion algorithm
        for (int i = NK; i < NB * (NR + 1); i++) {
            temp = w[i - 1];
            if (i % NK == 0) {
                temp = SubByte(RotByte(temp));
                rconWord.addWord(rcon[(i / NK)], new myByte(0), new myByte(0), new myByte(0));
                temp = temp.XOR(rconWord);
            }

            // a different version is used if NK > 6
            if ((NK > 6) && (i % NK == 4)) {
                temp = SubByte(temp);
            }

            w[i] = w[i - NK];
            w[i] = w[i].XOR(temp);
        }

        //System.out.println("Expanded Key:\n");
        //for(int i=0;i<w.length;i++){
        //	System.out.println(w[i]);
        //}
    }

    //used by the key expansion method
    //this takes in a word and returns a word in which each byte
    //has been replaced by it's S-Box equivalent
    private myWord SubByte(myWord mw) {
        return new myWord(doSBox(mw.theWord[3]), doSBox(mw.theWord[2]), doSBox(mw.theWord[1]), doSBox(mw.theWord[0]));
    }

    //used by the key expansion method
    //this returns a word in which the bytes have been shifted one place to the left
    private myWord RotByte(myWord a) {
        return new myWord(a.theWord[2], a.theWord[1], a.theWord[0], a.theWord[3]);
    }

    //internal method used by MixColumnInv
    //takes in an array of 4 myByte objects and multiplies them in the GF(2^8)
    //by the array 14 11 13 9,  9 14 11 13,  13 9 14 11,  11 13 9 14
    private myByte[] MixColumnInvInt(myByte[] temp) {
        myByte[] col2 = new myByte[4]; //holds newly multiplied column
        col2[0] = mul(temp[0].getVal(), 14).XOR(mul(temp[1].getVal(), 11).XOR(mul(temp[2].getVal(), 13).XOR(mul(temp[3].getVal(), 9))));
        col2[1] = mul(temp[0].getVal(), 9).XOR(mul(temp[1].getVal(), 14).XOR(mul(temp[2].getVal(), 11).XOR(mul(temp[3].getVal(), 13))));
        col2[2] = mul(temp[0].getVal(), 13).XOR(mul(temp[1].getVal(), 9).XOR(mul(temp[2].getVal(), 14).XOR(mul(temp[3].getVal(), 11))));
        col2[3] = mul(temp[0].getVal(), 11).XOR(mul(temp[1].getVal(), 13).XOR(mul(temp[2].getVal(), 9).XOR(mul(temp[3].getVal(), 14))));
        return col2;
    }

    //this method takes in two values and uses the log and alog tables
    //to return the value of them being multiplied together
    //it must use the tables as multiplication in the GF(2^8) is different
    //from regular multiplication
    private myByte mul(int a, int b) {
        if (a != 0 && b != 0) {
            return new myByte(alog[(log[a] + log[b]) % 255]);
        } else {
            return new myByte(0);
        }
    }

    //returns an array of ints (ASCII) representation of the state which will be the ciphertext or plaintext
    //depending on when it's called
    public int[] toInt() {
        int t[] = new int[4 * NB];
        for (int i = 0; i < NB * 4; i++) {
            t[i] = state[i].getVal();
        }

        //byte[] temp2 = new byte[NB*4];
        //for(int i=0;i<NB*4;i++){
        //temp2[i] = (byte)state[i].getVal();
        //}
        //return new String(temp2);
        return t;
    }

    private void createSBox() {
        S = new int[]{
                    99, 124, 119, 123, 242, 107, 111, 197,
                    48, 1, 103, 43, 254, 215, 171, 118,
                    202, 130, 201, 125, 250, 89, 71, 240,
                    173, 212, 162, 175, 156, 164, 114, 192,
                    183, 253, 147, 38, 54, 63, 247, 204,
                    52, 165, 229, 241, 113, 216, 49, 21,
                    4, 199, 35, 195, 24, 150, 5, 154,
                    7, 18, 128, 226, 235, 39, 178, 117,
                    9, 131, 44, 26, 27, 110, 90, 160,
                    82, 59, 214, 179, 41, 227, 47, 132,
                    83, 209, 0, 237, 32, 252, 177, 91,
                    106, 203, 190, 57, 74, 76, 88, 207,
                    208, 239, 170, 251, 67, 77, 51, 133,
                    69, 249, 2, 127, 80, 60, 159, 168,
                    81, 163, 64, 143, 146, 157, 56, 245,
                    188, 182, 218, 33, 16, 255, 243, 210,
                    205, 12, 19, 236, 95, 151, 68, 23,
                    196, 167, 126, 61, 100, 93, 25, 115,
                    96, 129, 79, 220, 34, 42, 144, 136,
                    70, 238, 184, 20, 222, 94, 11, 219,
                    224, 50, 58, 10, 73, 6, 36, 92,
                    194, 211, 172, 98, 145, 149, 228, 121,
                    231, 200, 55, 109, 141, 213, 78, 169,
                    108, 86, 244, 234, 101, 122, 174, 8,
                    186, 120, 37, 46, 28, 166, 180, 198,
                    232, 221, 116, 31, 75, 189, 139, 138,
                    112, 62, 181, 102, 72, 3, 246, 14,
                    97, 53, 87, 185, 134, 193, 29, 158,
                    225, 248, 152, 17, 105, 217, 142, 148,
                    155, 30, 135, 233, 206, 85, 40, 223,
                    140, 161, 137, 13, 191, 230, 66, 104,
                    65, 153, 45, 15, 176, 84, 187, 22
                };
    }

    //sets up the inverse S-Box
    private void createSinvBox() {
        Sinv = new int[]{
                    82, 9, 106, 213, 48, 54, 165, 56,
                    191, 64, 163, 158, 129, 243, 215, 251,
                    124, 227, 57, 130, 155, 47, 255, 135,
                    52, 142, 67, 68, 196, 222, 233, 203,
                    84, 123, 148, 50, 166, 194, 35, 61,
                    238, 76, 149, 11, 66, 250, 195, 78,
                    8, 46, 161, 102, 40, 217, 36, 178,
                    118, 91, 162, 73, 109, 139, 209, 37,
                    114, 248, 246, 100, 134, 104, 152, 22,
                    212, 164, 92, 204, 93, 101, 182, 146,
                    108, 112, 72, 80, 253, 237, 185, 218,
                    94, 21, 70, 87, 167, 141, 157, 132,
                    144, 216, 171, 0, 140, 188, 211, 10,
                    247, 228, 88, 5, 184, 179, 69, 6,
                    208, 44, 30, 143, 202, 63, 15, 2,
                    193, 175, 189, 3, 1, 19, 138, 107,
                    58, 145, 17, 65, 79, 103, 220, 234,
                    151, 242, 207, 206, 240, 180, 230, 115,
                    150, 172, 116, 34, 231, 173, 53, 133,
                    226, 249, 55, 232, 28, 117, 223, 110,
                    71, 241, 26, 113, 29, 41, 197, 137,
                    111, 183, 98, 14, 170, 24, 190, 27,
                    252, 86, 62, 75, 198, 210, 121, 32,
                    154, 219, 192, 254, 120, 205, 90, 244,
                    31, 221, 168, 51, 136, 7, 199, 49,
                    177, 18, 16, 89, 39, 128, 236, 95,
                    96, 81, 127, 169, 25, 181, 74, 13,
                    45, 229, 122, 159, 147, 201, 156, 239,
                    160, 224, 59, 77, 174, 42, 245, 176,
                    200, 235, 187, 60, 131, 83, 153, 97,
                    23, 43, 4, 126, 186, 119, 214, 38,
                    225, 105, 20, 99, 85, 33, 12, 125
                };
    }

    //sets up the array of round constants as used by the key expansion
    private void createRcon() {
        rcon = new myByte[]{new myByte(0),
                    new myByte(1), new myByte(2), new myByte(4), new myByte(8), new myByte(16),
                    new myByte(32), new myByte(64), new myByte(128), new myByte(27), new myByte(54),
                    new myByte(108), new myByte(216), new myByte(123), new myByte(246), new myByte(247),
                    new myByte(245), new myByte(241), new myByte(249), new myByte(233), new myByte(201),
                    new myByte(151), new myByte(53), new myByte(106), new myByte(212), new myByte(179),
                    new myByte(125), new myByte(250), new myByte(239), new myByte(197), new myByte(145)
                };
    }

    //creates alog and log tables as used by mul(tiply) method which allows multiplication in GF(2^8)
    private void createLogs() {
        alog[0] = 1;
        for (int i = 1; i < 256; i++) {
            int j = (alog[i - 1] << 1) ^ alog[i - 1];
            if ((j & 0x100) != 0) {
                j ^= ROOT;
            }
            alog[i] = j;
        }

        for (int i = 1; i < 255; i++) {
            log[alog[i]] = i;
        }
    }
}