import org.junit.Assert;
import org.junit.Test;
import scala.collection.immutable.HashSet;

public class LogicJavaTest {
    @Test
    public void testKitten() {
        Kitten kitten = new Kitten(1, new HashSet());
        Assert.assertEquals(0, kitten.attributes().size());
    }
}
