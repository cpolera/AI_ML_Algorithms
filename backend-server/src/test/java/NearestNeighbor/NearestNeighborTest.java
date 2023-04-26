package NearestNeighbor;

import io.github.cdimascio.dotenv.Dotenv;
import com.implementations.models.NearestNeighbor.ObjectNN;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static NearestNeighbor.NearestNeighborTestHelper.initDataSpecific;
import static com.implementations.models.NearestNeighbor.NearestNeighborClassifier.classifyNeighbors;

@Test()
public class NearestNeighborTest {
    @DataProvider
    public Object[][] data() {
        return new Object[][]{new ArrayList[]{initDataSpecific()}};
    }

    @BeforeClass
    public void beforeClass() {
        Dotenv.configure().systemProperties().load();
    }

    @Test(dataProvider = "data")
    public void testNeighborhood(ArrayList<ObjectNN> neighborhood) {
        ObjectNN formerO = neighborhood.get(0);
        ObjectNN formerX = neighborhood.get(1);
        Assert.assertEquals(formerO.classification, "O");
        Assert.assertEquals(formerX.classification, "X");
        classifyNeighbors(neighborhood, 5);
        Assert.assertEquals(formerO.classification, "X");
        Assert.assertEquals(formerX.classification, "O");
    }

}