import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class Main {
    public static void main(String[] args) {
        MyClass myClass = new MyClass(null, null, null);
        MyClass myClass2 = new MyClass(null, MyClassKt.makeNetworkCall("hello.world.org", null, null));
        MyClass myClass3 = new MyClass("test", null, new Configuration(MyClassKt.createUrlFrom2Parts(null, "bazzz")));
        Configuration defaultConfiguration = new Configuration(null);
        System.out.println(myClass);
        System.out.println(myClass2);
        System.out.println(myClass3);
        System.out.println(defaultConfiguration);
    }
}
