public class BoxedTypeIdentity {

    public static void main(String[] args) {
        int i = 1;
        int j = 1;
        System.out.println(i == j); // true

        Integer I = Integer.valueOf(i);
        Integer J = Integer.valueOf(j);
        System.out.println(I.equals(J)); // true
        System.out.println(I == J); // true...?!

        i = 128;
        j = 128;
        I = Integer.valueOf(i);
        J = Integer.valueOf(j);
        System.out.println(I.equals(J)); // true
        System.out.println(I == J); // false
    }

}
