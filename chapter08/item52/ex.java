import java.util.*;
import java.util.concurrent.*;

class ex {

    public static void main(String[] args) {

        ExecutorService e = Executors.newCachedThreadPool();

        // compile-error because both submit and println are overloaded
        e.submit(System.out::println);
        
        // assign to a variable of static type Runnable to resolve ambiguity
        Runnable r = System.out::println;
        e.submit(r);

        // cast to Runnable to resolve ambiguity
        e.submit((Runnable) System.out::println);


        // examples where arguments are not overloaded

        // yield takes 0 input and returns 0 output, i.e. is a Runnable
        e.submit(Thread::yield);

        // currentThread takes 0 and returns 1 output, i.e. is a Callable
        e.submit(Thread::currentThread);

        e.shutdown();

    }
    
}

