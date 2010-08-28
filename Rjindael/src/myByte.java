class myByte {
//class represents a byte as an array of 8 booleans

    public boolean[] bytes;//holds the boolean values

    //a constructor which initialises the array
    public myByte() {
        bytes = new boolean[8];
    }

    // a constructor that takes in 8 boolean values and fills an array
    public myByte(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g, boolean h) {
        bytes = new boolean[8];
        bytes[7] = a;
        bytes[6] = b;
        bytes[5] = c;
        bytes[4] = d;
        bytes[3] = e;
        bytes[2] = f;
        bytes[1] = g;
        bytes[0] = h;
    }

    //a constructor that converts an int to a myByte object
    public myByte(int a) {
        bytes = new boolean[8];
        int temp = a;
        for (int i = 0; i < 8; i++) {
            if ((temp % 2) == 1) {
                bytes[i] = true;
            } else {
                bytes[i] = false;
            }
            temp = temp / 2;
        }
    }

    //a method that XOR a myByte object with the current myByte object
    public myByte XOR(myByte b) {
        myByte temp = new myByte();
        for (int i = 0; i < 8; i++) {
            temp.bytes[i] = intXOR(bytes[i], b.bytes[i]);
        }
        return temp;
    }

    //result of the XOR operation on a single bit
    private boolean intXOR(boolean a, boolean b) {
        boolean temp = false;
        if (((a == true) && (b == false)) || ((b == true) && (a == false))) {
            temp = true;
        }
        return temp;
    }

    //this constructor takes in a string containing 1s and 0s
    public myByte(String a) {
        bytes = new boolean[8];
        String s;
        Character c;
        for (int i = 0; i < 8; i++) {
            c = new Character(a.charAt(i));
            s = c.toString();
            if (s.equals("1")) {
                bytes[7 - i] = true;
            } else {
                bytes[7 - i] = false;
            }
        }
    }

    //a constructor that takes in a boolean array
    public myByte(boolean b[]) {
        bytes = b;
    }

    //method that returns an decimal value (int) of the array
    public int getVal() {
        int temp = 0;
        for (int i = 0; i < 8; i++) {
            if (bytes[i] == true) {
                temp = temp + (int) java.lang.Math.pow(2, i);
            }
        }
        return temp;
    }
}