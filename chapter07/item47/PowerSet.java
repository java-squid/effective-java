import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class PowerSet {
    public static final <E> Collection<Set<E>> of(Set<E> s) {
        List<E> src = new ArrayList<>(s);
        if (src.size() > 30) {
            throw new IllegalArgumentException("Set too big " + s);
        }
        return new AbstractCollection<Set<E>>() {
            @Override
            public int size() {
                return 1 << src.size();
            }

            @Override
            public boolean contains(Object o) {
                return o instanceof Set && src.containsAll((Set) o);
            }

            @Override
            public Iterator<Set<E>> iterator() {
                return new Iterator<Set<E>>() {
                    private int index = 0;
                    private int end = size();

                    @Override
                    public boolean hasNext() {
                        return index < end;
                    }

                    @Override
                    public Set<E> next() {
                        Set<E> result = new HashSet<>();
                        for (int i = 0, j = index; j != 0; i++, j >>= 1) {
                            if ((j & 1) == 1) {
                                result.add(src.get(i));
                            }
                        }
                        index++;
                        return result;
                    }
                };
            }
        };
    }

    public static void main(String[] args) {
        Set<Character> set = Set.of('a', 'b', 'c');
        for (Set<Character> s : PowerSet.of(set)) {
            System.out.println(s);
        }
    }
}

