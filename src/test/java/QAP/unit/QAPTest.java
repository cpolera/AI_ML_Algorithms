package QAP.unit;

import io.github.cdimascio.dotenv.Dotenv;
import models.QuadraticAssignment.QAP;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test()
public class QAPTest {
    @DataProvider
    public Object[] data() throws Exception {
        QAP QAP1 = new QAP("qap4.txt");
        QAP1.runSolution();
        return new QAP[]{QAP1};
    }

    @BeforeClass
    public void beforeClass() {
        Dotenv.configure().systemProperties().load();
    }

    @Test(dataProvider = "data")
    public void testPermutationCount(QAP QAP) {
        Assert.assertEquals(QAP.countResults(), 24);
    }

    @Test(dataProvider = "data")
    public void testBestPermutation(QAP QAP) {
        int[] bestPermutation = QAP.getBestPermutation();
        int bestPermutationCost = QAP.calculateCost(bestPermutation);
        Assert.assertEquals(bestPermutationCost, 35);
    }

    public void testPermutationCostCalculation() {
        QAP QAP = new QAP("qap4_Realistic.txt");
        int[] testPermutation = new int[]{1,0,3,2};
        int cost = QAP.calculateCost(testPermutation);
        Assert.assertEquals(cost, 419);
    }
}