public class Testing {


    public static void main(String[] args) {

        TestObj testObj = new TestObj("First");
        updateName(testObj);

        System.out.println(testObj.name);

        int c1 = 12;
        int c2 = 23;
        int c3 = 1234;

        System.out.printf("%-22s%-22s%-22s\n", "Column 1", "Column 2", "Column 3");
        System.out.printf("%-22d%-22d%-22d\n", c1, c2, c3);
    }


    public static void updateName(TestObj testObj) {
        testObj.name = "NotFirst";
        testObj = new TestObj("Third");
    }
}
