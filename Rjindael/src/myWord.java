class myWord {
//class represents a word as an array of 4 myByte objects

    public myByte[] theWord;
    //the first myByte is the MSByte

    public void myWord(myByte a, myByte b, myByte c, myByte d) {
        theWord = new myByte[4];
        theWord[3] = a;
        theWord[2] = b;
        theWord[1] = c;
        theWord[0] = d;
    }

    //returns the bits of each byte
    @Override
    public String toString() {
        String temp = "";
        for (int i = 3; i >= 0; i--) {
            if (theWord[i] != null) {
                temp = temp + "  " + theWord[i].toString();
            }
        }
        return temp;
    }

    //returns the decimal value of each byte
    public String toStringInt() {
        String temp = "";
        for (int i = 3; i >= 0; i--) {
            if (theWord[i] != null) {
                temp = temp + "  " + theWord[i].getVal();
            }
        }
        return temp;
    }

    //an simple constructor that merely initializes the array
    public myWord(){
	theWord = new myByte[4];
    }

    //a constructor that takes in 4 myByte objects
    public myWord(myByte a, myByte b, myByte c, myByte d) {
        theWord = new myByte[4];
        theWord[3] = a;
        theWord[2] = b;
        theWord[1] = c;
        theWord[0] = d;
    }

    //a method which adds 4 myByte objects into the word array
    public void addWord(myByte a, myByte b, myByte c, myByte d) {
        theWord[3] = a;
        theWord[2] = b;
        theWord[1] = c;
        theWord[0] = d;
    }

    //as previous but takes in MyByte elements an array of myByte objects
    public void addWord(myByte[] m) {
        for (int i = 0; i < 4; i++) {
            theWord[i] = m[i];
        }
    }

    //a construtor that takes in an array of myBytes
    public void myWord(myByte[] b) {
        theWord = b;
    }

    //a method that XOR a word taken with the current word
    public myWord XOR(myWord b) {
        myWord temp = new myWord();
        temp.addWord(theWord[3].XOR(b.theWord[3]), theWord[2].XOR(b.theWord[2]), theWord[1].XOR(b.theWord[1]), theWord[0].XOR(b.theWord[0]));
        return temp;
    }
}