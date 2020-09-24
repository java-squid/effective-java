public class HelloJNI {
    
    static {
        System.loadLibrary("native");
    }

    public static native void helloFromC();

    public static void main(String[] args) {
        helloFromC();
    }

}
