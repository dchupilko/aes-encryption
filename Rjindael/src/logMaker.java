public class logMaker {

    private int pos = 0;
    private static int NR;
    private static String initMsg;
    private static String result;
    private static String[] fullKey = new String[60];
    private static String[] strState = new String[60];
    private static String[] strDescr = new String[60];

    public void clear() {
        for (int i=0;i<strState.length;i++){
            strState[i] = "";
        }

        for (int i=0;i<strDescr.length;i++){
            strDescr[i] = "";
        }

        for (int i=0;i<fullKey.length;i++) {
            fullKey[i] = "";
        }
    }

    public void addState(String s) {
        strState[pos] += s;
        pos++;
    }

    public void addDescr(String s, int i) {
        strDescr[i] = s;
    }

    public void addKey(int i, String s) {
        fullKey[i] += s;
    }

    public void setInit(String s) {
        initMsg = s;
    }

    public void setResult(String s) {
        result = s;
    }

    public void setNR(int i) {
        NR = i;
    }

    public String getState(int i) {
        return strState[i];
    }

    public String getDescr(int i) {
        return strDescr[i];
    }

    public String getInit() {
        return initMsg;
    }

    public String getResult() {
        return result;
    }

    public int getNR() {
        return NR;
    }

    public String getKey(int i) {
        return fullKey[i];
    }
}